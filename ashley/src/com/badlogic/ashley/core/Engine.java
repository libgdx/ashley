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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
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
	private static SystemComparator comparator = new SystemComparator();
	
	/** An unordered array that holds all entities in the Engine */
	private Array<Entity> entities;
	/** An unoredered array that keeps track of entities pending removal for safe in-loop removal */
	private Array<Entity> pendingRemovalEntities;
	/** An unordered list of EntitySystem */
	private Array<EntitySystem> systems;
	/** An unordered and immutable list of EntitySystem */
	private ImmutableArray<EntitySystem> immutableSystems;
	/** A hashmap that organises EntitySystems by class for easy retrieval */
	private ObjectMap<Class<?>, EntitySystem> systemsByClass;
	/** A hashmap that organises all entities into family buckets */
	private ObjectMap<Family, Array<Entity>> families;
	/** A hashmap that organises all entities into immutable family buckets */
	private ObjectMap<Family, ImmutableArray<Entity>> immutableFamilies;
	/** A collection of entity added/removed event listeners */
	private SnapshotArray<EntityListener> listeners;
	/** Entity added/removed event listeners per family */
	private ObjectMap<Family,SnapshotArray<EntityListener>> familyListeners;
	
	/** A listener for the Engine that's called every time a component is added. */
	private final Listener<Entity> componentAdded;
	/** A listener for the Engine that's called every time a component is removed. */
	private final Listener<Entity> componentRemoved;
	
	/** Whether or not the engine is ticking */
	private boolean updating;
	
	/** Mechanism to delay component addition/removal to avoid affecting system processing */
	private ComponentOperationPool componentOperationsPool;
 	private Array<ComponentOperation> componentOperations;
 	private ComponentOperationHandler componentOperationHandler;
	
	public Engine(){
		entities = new Array<Entity>(false, 16);
		pendingRemovalEntities = new Array<Entity>(false, 16);
		systems = new Array<EntitySystem>(false, 16);
		immutableSystems = new ImmutableArray<EntitySystem>(systems);
		systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
		families = new ObjectMap<Family, Array<Entity>>();
		immutableFamilies = new ObjectMap<Family, ImmutableArray<Entity>>();
		listeners = new SnapshotArray<EntityListener>(false, 16);
		familyListeners = new ObjectMap<Family,SnapshotArray<EntityListener>>();
		
		componentAdded = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				updateFamilyMembership(object);
			} 
		};
		
		componentRemoved = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				updateFamilyMembership(object);
			} 
		};
		
		updating = false;
		
		componentOperationsPool = new ComponentOperationPool();
		componentOperations = new Array<ComponentOperation>();
		componentOperationHandler = new ComponentOperationHandler() {
			public void add(Entity entity, Component component) {
				if (updating) {
					ComponentOperation operation = componentOperationsPool.obtain();
					operation.makeAdd(entity, component);
					componentOperations.add(operation);
				}
				else {
					entity.addInternal(component);
				}
			}
			
			public void remove(Entity entity, Class<? extends Component> componentClass) {
				if (updating) {
					ComponentOperation operation = componentOperationsPool.obtain();
					operation.makeRemove(entity, componentClass);
					componentOperations.add(operation);
				}
				else {
					entity.removeInternal(componentClass);
				}
			}
		};
	}
	
	/**
	 * Adds an entity to this Engine.
	 */
	public void addEntity(Entity entity){
		entities.add(entity);
		
		updateFamilyMembership(entity);
		
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		entity.componentOperationHandler = componentOperationHandler;
		
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			EntityListener listener = (EntityListener)items[i];
			listener.entityAdded(entity);
		}
		listeners.end();		
	}
	
	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		if (updating) {
			pendingRemovalEntities.add(entity);
		}
		else {
			removeEntityInternal(entity);
		}
	}
	
	/**
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		while(entities.size > 0) {
			removeEntity(entities.first());
		}
	}
	
	/**
	 * Adds the {@link EntitySystem} to this Engine.
	 */
	public void addSystem(EntitySystem system){
		Class<? extends EntitySystem> systemType = system.getClass();
		
		if (!systemsByClass.containsKey(systemType)) {
			systems.add(system);
			systemsByClass.put(systemType, system);
			system.addedToEngine(this);
			
			systems.sort(comparator);
		}
	}
	
	/**
	 * Removes the {@link EntitySystem} from this Engine.
	 */
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			system.removedFromEngine(this);
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
		registerFamily(family);
		return immutableFamilies.get(family);
	}
	
	/**
	 * Adds an {@link EntityListener}.
	 * 
	 * The listener will be notified every time an entity is added/removed to/from the engine.
	 */
	public void addEntityListener(EntityListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Adds an {@link EntityListener} for a specific {@link Family}.
	 * 
	 * The listener will be notified every time an entity is added/removed to/from the given family.
	 */
	public void addEntityListener(Family family, EntityListener listener) {
		registerFamily(family);
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);
		
		if (listeners == null) {
			listeners = new SnapshotArray<EntityListener>(false, 16);
			familyListeners.put(family, listeners);
		}
		
		listeners.add(listener);
	}
	
	/**
	 * Removes an {@link EntityListener} 
	 */
	public void removeEntityListener(EntityListener listener) {
		listeners.removeValue(listener, true);
		
		for (SnapshotArray<EntityListener> familyListenerArray : familyListeners.values()) {
			familyListenerArray.removeValue(listener, true);
		}
	}
	
	/**
	 * Updates all the systems in this Engine.
	 * @param deltaTime The time passed since the last frame.
	 */
	public void update(float deltaTime){
		updating = true;
		for(int i=0; i<systems.size; i++){
            if (systems.get(i).checkProcessing()) {
                systems.get(i).update(deltaTime);
            }
            
            processComponentOperations();
            removePendingEntities();
		}
		
		updating = false;
	}
	
	private void updateFamilyMembership(Entity entity){
		for (Entry<Family, Array<Entity>> entry : families.entries()) {
			Family family = entry.key;
			Array<Entity> entities = entry.value;
			int familyIndex = family.getIndex();
			
			
			boolean belongsToFamily = entity.getFamilyBits().get(familyIndex);
			boolean matches = family.matches(entity);
			
			if (!belongsToFamily && matches) {
				entities.add(entity);
				entity.getFamilyBits().set(familyIndex);
				
				notifyFamilyListenersAdd(family, entity);
			}
			else if (belongsToFamily && !matches) {
				entities.removeValue(entity, true);
				entity.getFamilyBits().clear(familyIndex);
				
				notifyFamilyListenersRemove(family, entity);
			}
		}
	}
	
	private void removePendingEntities() {
		int numPending = pendingRemovalEntities.size;
		
		for (int i = 0; i < numPending; ++i) {
			removeEntityInternal(pendingRemovalEntities.get(i));
		}
		
		pendingRemovalEntities.clear();
	}
	
	protected void removeEntityInternal(Entity entity) {
		entities.removeValue(entity, true);
		
		if(!entity.getFamilyBits().isEmpty()){
			for (Entry<Family, Array<Entity>> entry : families.entries()) {
				Family family = entry.key;
				Array<Entity> entities = entry.value;
				
				if(family.matches(entity)){
					entities.removeValue(entity, true);
					entity.getFamilyBits().clear(family.getIndex());
					notifyFamilyListenersRemove(family, entity);
				}
			}
		}
		
		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;
		
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			EntityListener listener = (EntityListener)items[i];
			listener.entityRemoved(entity);
		}
		listeners.end();
	}
	
	private void notifyFamilyListenersAdd(Family family, Entity entity) {
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);
		
		if (listeners != null) {
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				EntityListener listener = (EntityListener)items[i];
				listener.entityAdded(entity);
			}
			listeners.end();	
		}
	}
	
	private void notifyFamilyListenersRemove(Family family, Entity entity) {
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);
		
		if (listeners != null) {
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				EntityListener listener = (EntityListener)items[i];
				listener.entityRemoved(entity);
			}
			listeners.end();	
		}
	}
	
	private Array<Entity> registerFamily(Family family) {
		Array<Entity> entities = families.get(family);
		
		if (entities == null) {
			entities = new Array<Entity>(false, 16);
			families.put(family, entities);
			immutableFamilies.put(family, new ImmutableArray<Entity>(entities));
			
			for(Entity e : this.entities){
				if(family.matches(e)) {
					entities.add(e);
					e.getFamilyBits().set(family.getIndex());
				}
			}
		}
		
		return entities;
	}
	
	private void processComponentOperations() {
		int numOperations = componentOperations.size;
		
		for (int i = 0; i < numOperations; ++i) {
			ComponentOperation operation = componentOperations.get(i);
			
			if (operation.type == ComponentOperation.Type.Add) {
				operation.entity.addInternal(operation.component);
			}
			else if (operation.type == ComponentOperation.Type.Remove) {
				operation.entity.removeInternal(operation.componentClass);
			}
			
			componentOperationsPool.free(operation);
		}
		
		componentOperations.clear();
	}
	
	static interface ComponentOperationHandler {
		public void add(Entity entity, Component component);
		public void remove(Entity entity, Class<? extends Component> componentClass);
	}
	
	private static class ComponentOperation {
		public enum Type {
			Add,
			Remove,
		}
		
		public Type type;
		public Entity entity;
		public Component component;
		public Class<? extends Component> componentClass;
		
		public void makeAdd(Entity entity, Component component) {
			this.type = Type.Add;
			this.entity = entity;
			this.component = component;
			this.componentClass = null;
		}
		
		public void makeRemove(Entity entity, Class<? extends Component> componentClass) {
			this.type = Type.Remove;
			this.entity = entity;
			this.component = null;
			this.componentClass = componentClass;
		}
	}
	
	private static class ComponentOperationPool extends Pool<ComponentOperation> {
		@Override
		protected ComponentOperation newObject() {
			return new ComponentOperation();
		}		
	}
	
	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
}
