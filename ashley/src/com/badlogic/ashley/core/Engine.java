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

import com.badlogic.ashley.core.ComponentOperationHandler.BooleanInformer;
import com.badlogic.ashley.core.SystemManager.SystemListener;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

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

	final ObjectMap<Class, Object> configurations = new ObjectMap<Class, Object>();
	public final Signal<Object> configurationAdded = new Signal<Object>();
	public final Signal<Object> configurationRemoved = new Signal<Object>();

	private static Family empty = Family.all().get();
	
	private final Listener<Entity> componentAdded = new ComponentListener();
	private final Listener<Entity> componentRemoved = new ComponentListener();
	
	private SystemManager systemManager = new SystemManager(new EngineSystemListener());
	private EntityManager entityManager = new EntityManager(new EngineEntityListener());
	private ComponentOperationHandler componentOperationHandler = new ComponentOperationHandler(new EngineDelayedInformer());
	private FamilyManager familyManager = new FamilyManager(entityManager.getEntities());	
	private boolean updating;

	/**
	 * Creates a new Entity object.
	 * @return @{@link Entity}
	 */

	public Entity createEntity () {
		return new Entity();
	}

	/**
	 * Creates a new {@link Component}. To use that method your components must have a visible no-arg constructor
	 */
	public <T extends Component> T createComponent (Class<T> componentType) {
		try {
			return ClassReflection.newInstance(componentType);
		} catch (ReflectionException e) {
			return null;
		}
	}

	/**
	 * Adds an entity to this Engine.
	 * This will throw an IllegalArgumentException if the given entity
	 * was already registered with an engine.
	 */
	public void addEntity(Entity entity){
		boolean delayed = updating || familyManager.notifying();
		entityManager.addEntity(entity, delayed);
	}

	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeEntity(entity, delayed);
	}
	
	/**
	 * Removes all entities of the given {@link Family}.
	 */
	public void removeAllEntities(Family family) {
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeAllEntities(getEntitiesFor(family), delayed);
	}

	/**
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeAllEntities(delayed);
	}

	/**
	 * Returns an {@link ImmutableArray} of {@link Entity} that is managed by the the Engine
	 *  but cannot be used to modify the state of the Engine. This Array is not Immutable in
	 *  the sense that its contents will not be modified, but in the sense that it only reflects
	 *  the state of the engine.
	 *
	 * The Array is Immutable in the sense that you cannot modify its contents through the API of
	 *  the {@link ImmutableArray} class, but is instead "Managed" by the Engine itself. The engine
	 *  may add or remove items from the array and this will be reflected in the returned array.
	 *
	 * This is an important note if you are looping through the returned entities and calling operations
	 *  that may add/remove entities from the engine, as the underlying iterator of the returned array
	 *  will reflect these modifications.
	 *
	 * The returned array will have entities removed from it if they are removed from the engine,
	 *   but there is no way to introduce new Entities through the array's interface, or remove
	 *   entities from the engine through the array interface.
	 *
	 *  Discussion of this can be found at https://github.com/libgdx/ashley/issues/224
	 *
	 * @return An unmodifiable array of entities that will match the state of the entities in the
	 *  engine.
	 */
	public ImmutableArray<Entity> getEntities() {
		return entityManager.getEntities();
	}

	/**
	 * Adds the {@link EntitySystem} to this Engine.
	 * If the Engine already had a system of the same class,
	 * the new one will replace the old one.
	 */
	public void addSystem(EntitySystem system){
		systemManager.addSystem(system);
	}

	/**
	 * Removes the {@link EntitySystem} from this Engine.
	 */
	public void removeSystem(EntitySystem system){
		systemManager.removeSystem(system);
	}

	/**
	 * Quick {@link EntitySystem} retrieval.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return systemManager.getSystem(systemType);
	}

	/**
	 * @return immutable array of all entity systems managed by the {@link Engine}.
	 */
	public ImmutableArray<EntitySystem> getSystems() {
		return systemManager.getSystems();
	}

	/**
	 * Returns immutable collection of entities for the specified {@link Family}. Will return the same instance every time.
	 */
	public ImmutableArray<Entity> getEntitiesFor(Family family){
		return familyManager.getEntitiesFor(family);
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
		familyManager.addEntityListener(family, priority, listener);
	}

	/**
	 * Removes an {@link EntityListener}
	 */
	public void removeEntityListener (EntityListener listener) {
		familyManager.removeEntityListener(listener);
	}

	/**
	 * Retrieves the configuration item associated with that class
	 */
	public <T> T getConfiguration(Class<T> clazz) {
		return (T) configurations.get(clazz);
	}

	/**
	 * Retrieves an iterator for the configuration entries 
	 */
	public Entries<Class, Object> getConfigurationEntries() {
		return configurations.entries();
	}
	
	/**
	 * Retrieves all stored configurations
	 */
	public Object[] getConfigurations() {
		Object[] configs = new Object[configurations.size];
		Values values = configurations.values();
		for (int i = 0; values.hasNext(); i++) {
			configs[i] = values.next();
		}
		return configs;
	}

	/**
	 * Adds a configuration to the engine
	 */
	public <T> T addConfiguration(Class<T> clazz, T configuration) {
		if (configuration instanceof Configuration)
			((Configuration) configuration).onAdd(this);
		T result = (T) configurations.put(clazz, configuration);
		if (result instanceof Configuration)
			((Configuration) result).onRemove(this);
		if (result != null)
			configurationRemoved.dispatch(result);
		configurationAdded.dispatch(configuration);
		return result;
	}

	/**
	 * Removes a configuration from the engine 
	 */
	public Object removeConfiguration(Class<? extends Object> clazz) {
		Object config = configurations.get(clazz);
		if (config == null)
			return null;
		Object oldConfig = configurations.remove(clazz);
		if (oldConfig instanceof Configuration) {
			((Configuration) oldConfig).onRemove(this);
		}
		configurationRemoved.dispatch(config);
		return oldConfig;

	}

	/**
	 * Removes a configuration from the engine returns the class that the configuration was associated to
	 */
	public Class removeConfiguration(Object configuration) {
		Class configType = configurations.findKey(configuration, true);
		if (configType == null)
			return null;
		removeConfiguration(configType);
		return configType;
	}

	Class[] getConfigurationKeys() {
		Class[] result = new Class[configurations.size];
		Keys<Class> keys = configurations.keys();
		for (int i = 0; keys.hasNext(); i++)
			result[i] = keys.next();
		return result;
	}

	/**
	 * Removes all configurations from the engine
	 */
	public void clearConfigurations() {
		for (Class key : getConfigurationKeys()) {
			removeConfiguration(key);
		}
	}

	/**
	 * Updates all the systems in this Engine.
	 * 
	 * @param deltaTime The time passed since the last frame.
	 */
	public void update(float deltaTime){
		if (updating) {
			throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
		}
		
		updating = true;
		ImmutableArray<EntitySystem> systems = systemManager.getSystems();
		try {
			for (int i = 0; i < systems.size(); ++i) {
				EntitySystem system = systems.get(i);
				
				if (system.checkProcessing()) {
					system.update(deltaTime);
				}
	
				while(componentOperationHandler.hasOperationsToProcess() || entityManager.hasPendingOperations()) {
					componentOperationHandler.processOperations();
					entityManager.processPendingOperations();
				}
			}
		}
		finally {
			updating = false;
		}	
	}
	
	protected void addEntityInternal(Entity entity) {
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		entity.componentOperationHandler = componentOperationHandler;
		
		familyManager.updateFamilyMembership(entity);
	}
	
	protected void removeEntityInternal(Entity entity) {
		familyManager.updateFamilyMembership(entity);

		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;
	}
	
	private class ComponentListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			familyManager.updateFamilyMembership(object);
		}
	}
	
	private class EngineSystemListener implements SystemListener {
		@Override
		public void systemAdded (EntitySystem system) {
			system.addedToEngineInternal(Engine.this);
		}

		@Override
		public void systemRemoved (EntitySystem system) {
			system.removedFromEngineInternal(Engine.this);
		}
	}
	
	private class EngineEntityListener implements EntityListener {
		@Override
		public void entityAdded (Entity entity) {
			addEntityInternal(entity);
		}

		@Override
		public void entityRemoved (Entity entity) {
			removeEntityInternal(entity);
		}
	}
	
	private class EngineDelayedInformer implements BooleanInformer {
		@Override
		public boolean value () {
			return updating;
		}
	}
}
