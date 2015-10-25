/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.core;

import java.util.Comparator;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * The heart of the Entity framework. It is responsible for keeping track of {@link Entity} and
 * managing {@link EntitySystem} objects. The Engine should be updated every tick via the {@link #update(float)} method.
 *
 * With the Engine you can:
 *
 * <ul>
 * <li>Add/Remove {@link Entity} objects</li>
 * <li>Add/Remove {@link EntitySystem}s</li>
 * <li>Obtain a list of entities for a specific {@link Family}</li>
 * <li>Update the main loop</li>
 * <li>Register/unregister {@link EntityListener} objects</li>
 * </ul>
 *
 * @author Stefan Bachmann
 */
public class Engine {
	private static SystemComparator systemComparator = new SystemComparator();
	private static Family empty = Family.all().get();
	
	private final Listener<Entity> componentAdded;
	private final Listener<Entity> componentRemoved;
	
	private Array<Entity> entities;
	private ObjectSet<Entity> entitySet;
	private ImmutableArray<Entity> immutableEntities;
	private Array<EntityOperation> entityOperations;
	private EntityOperationPool entityOperationPool;
	private Array<EntitySystem> systems;
	private ImmutableArray<EntitySystem> immutableSystems;
	private ObjectMap<Class<?>, EntitySystem> systemsByClass;
	private ObjectMap<Family, Array<Entity>> families;
	private ObjectMap<Family, ImmutableArray<Entity>> immutableFamilies;
	private SnapshotArray<EntityListenerData> entityListeners;
	private ObjectMap<Family, Bits> entityListenerMasks;
	private boolean updating;
	private boolean notifying;
	private ComponentOperationPool componentOperationsPool;
 	private Array<ComponentOperation> componentOperations;
 	private ComponentOperationHandler componentOperationHandler;

	public Engine(){
		entities = new Array<Entity>(false, 16);
		entitySet = new ObjectSet<Entity>();
		immutableEntities = new ImmutableArray<Entity>(entities);
		entityOperations = new Array<EntityOperation>(false, 16);
		entityOperationPool = new EntityOperationPool();
		systems = new Array<EntitySystem>(false, 16);
		immutableSystems = new ImmutableArray<EntitySystem>(systems);
		systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
		families = new ObjectMap<Family, Array<Entity>>();
		immutableFamilies = new ObjectMap<Family, ImmutableArray<Entity>>();
		entityListeners = new SnapshotArray<EntityListenerData>(true, 16);
		entityListenerMasks = new ObjectMap<Family, Bits>();

		componentAdded = new ComponentListener(this);
		componentRemoved = new ComponentListener(this);

		updating = false;
		notifying = false;

		componentOperationsPool = new ComponentOperationPool();
		componentOperations = new Array<ComponentOperation>();
		componentOperationHandler = new ComponentOperationHandler(this);
	}

	/**
	 * Adds an entity to this Engine.
	 * This will throw an IllegalArgumentException if the given entity
	 * was already registered with an engine.
	 */
	public void addEntity(Entity entity){
		if (entitySet.contains(entity)) {
			throw new IllegalArgumentException("Entity is already registered with the Engine " + entity);
		}
		
		if (updating || notifying) {
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Add;
			entityOperations.add(operation);
		}
		else {
			addEntityInternal(entity);
		}
	}

	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		if (updating || notifying) {
			if(entity.scheduledForRemoval) {
				return;
			}
			entity.scheduledForRemoval = true;
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Remove;
			entityOperations.add(operation);
		}
		else {
			removeEntityInternal(entity);
		}
	}

	/**
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		if (updating || notifying) {
			for(Entity entity: entities) {
				entity.scheduledForRemoval = true;
			}
			EntityOperation operation = entityOperationPool.obtain();
			operation.type = EntityOperation.Type.RemoveAll;
			entityOperations.add(operation);
		}
		else {
			while(entities.size > 0) {
				removeEntity(entities.first());
			}
		}
	}

	public ImmutableArray<Entity> getEntities() {
		return immutableEntities;
	}

	/**
	 * Adds the {@link EntitySystem} to this Engine.
	 * If the Engine already had a system of the same class,
	 * the new one will replace the old one.
	 */
	public void addSystem(EntitySystem system){
		Class<? extends EntitySystem> systemType = system.getClass();		
		EntitySystem oldSytem = getSystem(systemType);
		
		if (oldSytem != null) {
			removeSystem(oldSytem);
		}
		
		systems.add(system);
		systemsByClass.put(systemType, system);
		system.addedToEngineInternal(this);

		systems.sort(systemComparator);
	}

	/**
	 * Removes the {@link EntitySystem} from this Engine.
	 */
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			system.removedFromEngineInternal(this);
		}
	}

	/**
	 * Quick {@link EntitySystem} retrieval.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return (T) systemsByClass.get(systemType);
	}

	/**
	 * @return immutable array of all entity systems managed by the {@link Engine}.
	 */
	public ImmutableArray<EntitySystem> getSystems() {
		return immutableSystems;
	}

	/**
	 * Returns immutable collection of entities for the specified {@link Family}. Will return the same instance every time.
	 */
	public ImmutableArray<Entity> getEntitiesFor(Family family){
		return registerFamily(family);
	}

	/**
	 * Adds an {@link EntityListener}.
	 *
	 * The listener will be notified every time an entity is added/removed to/from the engine.
	 */
	public void addEntityListener (EntityListener listener) {
		addEntityListener(empty, 0, listener);
	}

	/**
	 * Adds an {@link EntityListener}. The listener will be notified every time an entity is added/removed
	 * to/from the engine. The priority determines in which order the entity listeners will be called. Lower
	 * value means it will get executed first.
	 */
	public void addEntityListener (int priority, EntityListener listener) {
		addEntityListener(empty, priority, listener);
	}

	/**
	 * Adds an {@link EntityListener} for a specific {@link Family}.
	 *
	 * The listener will be notified every time an entity is added/removed to/from the given family.
	 */
	public void addEntityListener(Family family, EntityListener listener) {
		addEntityListener(family, 0, listener);
	}

	/**
	 * Adds an {@link EntityListener} for a specific {@link Family}. The listener will be notified every time an entity is
	 * added/removed to/from the given family. The priority determines in which order the entity listeners will be called. Lower
	 * value means it will get executed first.
	 */
	public void addEntityListener (Family family, int priority, EntityListener listener) {
		registerFamily(family);

		int insertionIndex = 0;
		while (insertionIndex < entityListeners.size) {
			if (entityListeners.get(insertionIndex).priority <= priority) {
				insertionIndex++;
			} else {
				break;
			}
		}

		// Shift up bitmasks by one step
		for (Bits mask : entityListenerMasks.values()) {
			for (int k = mask.length(); k > insertionIndex; k--) {
				if (mask.get(k - 1)) {
					mask.set(k);
				} else {
					mask.clear(k);
				}
			}
			mask.clear(insertionIndex);
		}

		entityListenerMasks.get(family).set(insertionIndex);

		EntityListenerData entityListenerData = new EntityListenerData();
		entityListenerData.listener = listener;
		entityListenerData.priority = priority;
		entityListeners.insert(insertionIndex, entityListenerData);
	}

	/**
	 * Removes an {@link EntityListener}
	 */
	public void removeEntityListener (EntityListener listener) {
		for (int i = 0; i < entityListeners.size; i++) {
			EntityListenerData entityListenerData = entityListeners.get(i);
			if (entityListenerData.listener == listener) {
				// Shift down bitmasks by one step
				for (Bits mask : entityListenerMasks.values()) {
					for (int k = i, n = mask.length(); k < n; k++) {
						if (mask.get(k + 1)) {
							mask.set(k);
						} else {
							mask.clear(k);
						}
					}
				}

				entityListeners.removeIndex(i--);
			}
		}
	}

	/**
	 * Updates all the systems in this Engine.
	 * @param deltaTime The time passed since the last frame.
	 */
	public void update(float deltaTime){
		if (updating) {
			throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
		}
		
		updating = true;
		for(int i=0; i<systems.size; i++){
			EntitySystem system = systems.get(i);
			if (system.checkProcessing()) {
				system.update(deltaTime);
			}

			processComponentOperations();
			processPendingEntityOperations();
		}

		updating = false;
	}

	private void updateFamilyMembership (Entity entity, boolean removing) {
		// Find families that the entity was added to/removed from, and fill
		// the bitmasks with corresponding listener bits.
		Bits addListenerBits = new Bits();
		Bits removeListenerBits = new Bits();

		for (Family family : entityListenerMasks.keys()) {
			final int familyIndex = family.getIndex();
			final Bits entityFamilyBits = entity.getFamilyBits();

			boolean belongsToFamily = entityFamilyBits.get(familyIndex);
			boolean matches = family.matches(entity) && !removing;

			if (belongsToFamily != matches) {
				final Bits listenersMask = entityListenerMasks.get(family);
				final Array<Entity> familyEntities = families.get(family);
				if (matches) {
					addListenerBits.or(listenersMask);
					familyEntities.add(entity);
					entityFamilyBits.set(familyIndex);
				} else {
					removeListenerBits.or(listenersMask);
					familyEntities.removeValue(entity, true);
					entityFamilyBits.clear(familyIndex);
				}
			}
		}

		// Notify listeners; set bits match indices of listeners
		notifying = true;
		Object[] items = entityListeners.begin();

		for (int i = removeListenerBits.nextSetBit(0); i >= 0; i = removeListenerBits.nextSetBit(i + 1)) {
			((EntityListenerData)items[i]).listener.entityRemoved(entity);
		}

		for (int i = addListenerBits.nextSetBit(0); i >= 0; i = addListenerBits.nextSetBit(i + 1)) {
			((EntityListenerData)items[i]).listener.entityAdded(entity);
		}

		entityListeners.end();
		notifying = false;
	}

	protected void removeEntityInternal(Entity entity) {
		entity.scheduledForRemoval = false;
		entities.removeValue(entity, true);
		entitySet.remove(entity);
		
		updateFamilyMembership(entity, true);

		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;
	}

	protected void addEntityInternal(Entity entity) {
		entities.add(entity);
		entitySet.add(entity);

		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);

		updateFamilyMembership(entity, false);

		entity.componentOperationHandler = componentOperationHandler;
	}

	private ImmutableArray<Entity> registerFamily(Family family) {
		ImmutableArray<Entity> immutableEntitiesInFamily = immutableFamilies.get(family);

		if (immutableEntitiesInFamily == null) {
			Array<Entity> familyEntities = new Array<Entity>(false, 16);
			immutableEntitiesInFamily = new ImmutableArray<Entity>(familyEntities);
			families.put(family, familyEntities);
			immutableFamilies.put(family, immutableEntitiesInFamily);
			entityListenerMasks.put(family, new Bits());

			for (Entity entity : this.entities){
				updateFamilyMembership(entity, false);
			}
		}

		return immutableEntitiesInFamily;
	}

	private void processPendingEntityOperations() {
		while (entityOperations.size > 0) {
			EntityOperation operation = entityOperations.removeIndex(entityOperations.size - 1);

			switch(operation.type) {
				case Add: addEntityInternal(operation.entity); break;
				case Remove: removeEntityInternal(operation.entity); break;
				case RemoveAll:
					while(entities.size > 0) {
						removeEntityInternal(entities.first());
					}
					break;
				default:
					throw new AssertionError("Unexpected EntityOperation type");
			}

			entityOperationPool.free(operation);
		}

		entityOperations.clear();
	}

	private void processComponentOperations() {
		for (int i = 0; i < componentOperations.size; ++i) {
			ComponentOperation operation = componentOperations.get(i);

			switch(operation.type) {
				case Add:
					operation.entity.notifyComponentAdded();
					break;
				case Remove:
					operation.entity.notifyComponentRemoved();
					break;
				default: break;
			}

			componentOperationsPool.free(operation);
		}

		componentOperations.clear();
	}

	private static class ComponentListener implements Listener<Entity> {
		private Engine engine;

		public ComponentListener(Engine engine) {
			this.engine = engine;
		}

		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			engine.updateFamilyMembership(object, false);
		}
	}

	static class ComponentOperationHandler {
		private Engine engine;

		public ComponentOperationHandler(Engine engine) {
			this.engine = engine;
		}

		public void add(Entity entity) {
			if (engine.updating) {
				ComponentOperation operation = engine.componentOperationsPool.obtain();
				operation.makeAdd(entity);
				engine.componentOperations.add(operation);
			}
			else {
				entity.notifyComponentAdded();
			}
		}

		public void remove(Entity entity) {
			if (engine.updating) {
				ComponentOperation operation = engine.componentOperationsPool.obtain();
				operation.makeRemove(entity);
				engine.componentOperations.add(operation);
			}
			else {
				entity.notifyComponentRemoved();
			}
		}
	}

	private static class ComponentOperation implements Pool.Poolable {
		public enum Type {
			Add,
			Remove,
		}

		public Type type;
		public Entity entity;

		public void makeAdd(Entity entity) {
			this.type = Type.Add;
			this.entity = entity;
		}

		public void makeRemove(Entity entity) {
			this.type = Type.Remove;
			this.entity = entity;
		}

		@Override
		public void reset() {
			entity = null;
		}
	}

	private static class ComponentOperationPool extends Pool<ComponentOperation> {
		@Override
		protected ComponentOperation newObject() {
			return new ComponentOperation();
		}
	}

	private static class EntityListenerData {
		public EntityListener listener;
		public int priority;
	}

	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}

	private static class EntityOperation implements Pool.Poolable {
		public enum Type {
			Add,
			Remove,
			RemoveAll
		}

		public Type type;
		public Entity entity;

		@Override
		public void reset() {
			entity = null;
		}
	}

	private static class EntityOperationPool extends Pool<EntityOperation> {
		@Override
		protected EntityOperation newObject() {
			return new EntityOperation();
		}
	}
}
