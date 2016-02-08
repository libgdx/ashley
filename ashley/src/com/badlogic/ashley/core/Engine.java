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
	private static Family empty = Family.all().get();
	
	private final Listener<Entity> componentAdded = new ComponentListener();
	private final Listener<Entity> componentRemoved = new ComponentListener();
	
	private SystemManager systemManager = new SystemManager(new EngineSystemListener());
	private EntityManager entityManager = new EntityManager(new EngineEntityListener());
	private ComponentOperationHandler componentOperationHandler = new ComponentOperationHandler(new EngineDelayedInformer());
	private FamilyManager familyManager = new FamilyManager(entityManager.getEntities());	
	private boolean updating;


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
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeAllEntities(delayed);
	}

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
	 * Updates all the systems in this Engine.
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
	
				componentOperationHandler.processOperations();
				entityManager.processPendingOperations();
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
