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
	private Array<EntityListener> listeners;
	/** EntityListeners that await removal */
	private Array<EntityListener> removalPendingListeners;
	/** Whether or not the entity listeners are being notified of an event */
	private boolean notifying;
	
	/** A listener for the Engine that's called every time a component is added. */
	private final Listener<Entity> componentAdded;
	/** A listener for the Engine that's called every time a component is removed. */
	private final Listener<Entity> componentRemoved;
	
	public Engine(){
		entities = new Array<Entity>();
		systems = new Array<EntitySystem>();
		immutableSystems = new ImmutableArray<EntitySystem>(systems);
		systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
		families = new ObjectMap<Family, Array<Entity>>();
		immutableFamilies = new ObjectMap<Family, ImmutableArray<Entity>>();
		listeners = new Array<EntityListener>();
		removalPendingListeners = new Array<EntityListener>();
		notifying = false;
		
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
	}
	
	/**
	 * Adds an entity to this Engine.
	 */
	public void addEntity(Entity entity){
		entities.add(entity);
		
		for (Entry<Family, Array<Entity>> entry : families.entries()) {
			if(entry.key.matches(entity)){
				entry.value.add(entity);
				entity.getFamilyBits().set(entry.key.getIndex());
			}
		}
		
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		
		notifying = true;
		for (EntityListener listener : listeners) {
			listener.entityAdded(entity);
		}
		notifying = false;
		removePendingListeners();
	}
	
	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		entities.removeValue(entity, true);
		
		if(!entity.getFamilyBits().isEmpty()){
			for (Entry<Family, Array<Entity>> entry : families.entries()) {
				if(entry.key.matches(entity)){
					entry.value.removeValue(entity, true);
					entity.getFamilyBits().clear(entry.key.getIndex());
				}
			}
		}
		
		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		
		notifying = true;
		for (EntityListener listener : listeners) {
			listener.entityRemoved(entity);
		}
		notifying = false;
		removePendingListeners();
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
		Array<Entity> entities = families.get(family, null);
		if(entities == null){
			entities = new Array<Entity>();
			for(Entity e:this.entities){
				if(family.matches(e)) {
					entities.add(e);
					e.getFamilyBits().set(family.getIndex());
				}
			}
			families.put(family, entities);
			immutableFamilies.put(family, new ImmutableArray<Entity>(entities));
		}
		
		return immutableFamilies.get(family);
	}
	
	/**
	 * Adds an {@link EntityListener}
	 */
	public void addEntityListener(EntityListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes an {@link EntityListener} 
	 */
	public void removeEntityListener(EntityListener listener) {
		if (notifying) {
			removalPendingListeners.add(listener);
		}
		else {
			listeners.removeValue(listener, true);
		}
	}
	
	/**
	 * Updates all the systems in this Engine.
	 * @param deltaTime The time passed since the last frame.
	 */
	public void update(float deltaTime){
		for(int i=0; i<systems.size; i++){
            if (systems.get(i).checkProcessing()) {
                systems.get(i).update(deltaTime);
            }
		}
	}
	
	private void updateFamilyMembership(Entity entity){
		for (Entry<Family, Array<Entity>> entry : families.entries()) {
			boolean belongsToFamily = entity.getFamilyBits().get(entry.key.getIndex());
			boolean matches = entry.key.matches(entity);
			
			if (!belongsToFamily && matches) {
				entry.value.add(entity);
				entity.getFamilyBits().set(entry.key.getIndex());
			}
			else if (belongsToFamily && !matches) {
				entry.value.removeValue(entity, true);
				entity.getFamilyBits().clear(entry.key.getIndex());
			}
		}
	}

	
	private void removePendingListeners() {
		for (EntityListener listener : removalPendingListeners) {
			listeners.removeValue(listener, true);
		}
		
		removalPendingListeners.clear();
	}
	
	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
}
