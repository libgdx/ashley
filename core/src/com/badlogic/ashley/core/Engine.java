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
import com.badlogic.ashley.utils.ImmutableIntMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * The Engine class is the heart of the Entity framework. It is responsible for keeping track of entities and
 * managing EntitySystems. The Engine should be updated every tick via the {@link #update(float)} method.
 * 
 * With the Engine you can:
 * 
 * <ul>
 * <li>Add/Remove Entities</li>
 * <li>Add/Remove EntitySystems</li>
 * <li>Obtain a list of entities for a specific Family</li>
 * <li>Update the main loop</li>
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
	/** A hashmap that organises EntitySystems by class for easy retrieval */
	private ObjectMap<Class<?>, EntitySystem> systemsByClass;
	/** A hashmap that organises all entities into family buckets */
	private ObjectMap<Family, IntMap<Entity>> families;
	/** A hashmap that organises all entities into immutable family buckets */
	private ObjectMap<Family, ImmutableIntMap<Entity>> immutableFamilies;
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
		systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
		families = new ObjectMap<Family, IntMap<Entity>>();
		immutableFamilies = new ObjectMap<Family, ImmutableIntMap<Entity>>();
		listeners = new Array<EntityListener>();
		removalPendingListeners = new Array<EntityListener>();
		notifying = false;
		
		componentAdded = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				componentAdded(object);
			} 
		};
		
		componentRemoved = new Listener<Entity>(){
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				componentRemoved(object);
			} 
		};
	}
	
	/**
	 * Add an entity to this Engine
	 * @param entity The Entity to add
	 */
	public void addEntity(Entity entity){
		entities.add(entity);
		
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(entry.key.matches(entity)){
				entry.value.put(entity.getIndex(), entity);
				entity.getFamilyBits().set(entry.key.getFamilyIndex());
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
	 * Remove an entity from this Engine
	 * @param entity The Entity to remove
	 */
	public void removeEntity(Entity entity){
		entities.removeValue(entity, true);
		
		if(!entity.getFamilyBits().isEmpty()){
			Entries<Family, IntMap<Entity>> entries = families.entries();
			while(entries.hasNext){
				Entry<Family, IntMap<Entity>> entry = entries.next();
				if(entry.key.matches(entity)){
					entry.value.remove(entity.getIndex());
					entity.getFamilyBits().clear(entry.key.getFamilyIndex());
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
	 * Removes all entities registered with this Engine
	 */
	public void removeAllEntities() {
		while(entities.size > 0) {
			removeEntity(entities.first());
		}
	}
	
	/**
	 * Add the EntitySystem to this Engine
	 * @param system The system to add
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
	 * Removes the EntitySystem from this Engine
	 * @param system The system to remove
	 */
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			system.removedFromEngine(this);
		}
	}
	
	/**
	 * Quick entity system retrieval
	 * @param systemType The EntitySystem class to retrieve
	 * @return The Entity System
	 */
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return systemType.cast(systemsByClass.get(systemType));
	}
	
	/**
	 * Returns an IntMap of entities for the specified Family. Will return the same instance every time.
	 * @param family The Family
	 * @return An IntMap of Entities
	 */
	public ImmutableIntMap<Entity> getEntitiesFor(Family family){
		IntMap<Entity> entities = families.get(family, null);
		if(entities == null){
			entities = new IntMap<Entity>();
			for(Entity e:this.entities){
				if(family.matches(e)) {
					entities.put(e.getIndex(), e);
					e.getFamilyBits().set(family.getFamilyIndex());
				}
			}
			families.put(family, entities);
			immutableFamilies.put(family, new ImmutableIntMap<Entity>(entities));
		}
		
		return immutableFamilies.get(family);
	}
	
	/**
	 * Adds entity listener
	 *  
	 * @param listener listener to be added
	 */
	public void addEntityListener(EntityListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes entity listener 
	 * 
	 * @param listener listener to be removed
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
	 * Internal listener for when a Component is added to an entity
	 * @param entity The Entity that had a component added to
	 */
	private void componentAdded(Entity entity){
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(!entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(entry.key.matches(entity)){
					entry.value.put(entity.getIndex(), entity);
					entity.getFamilyBits().set(entry.key.getFamilyIndex());
				}
			}
		}
	}
	

	/**
	 * Internal listener for when a Component is removed from an entity
	 * @param entity The Entity that had a component removed from
	 */
	private void componentRemoved(Entity entity){
		Entries<Family, IntMap<Entity>> entries = families.entries();
		while(entries.hasNext){
			Entry<Family, IntMap<Entity>> entry = entries.next();
			if(entity.getFamilyBits().get(entry.key.getFamilyIndex())){
				if(!entry.key.matches(entity)){
					entry.value.remove(entity.getIndex());
					entity.getFamilyBits().clear(entry.key.getFamilyIndex());
				}
			}
		}
	}
	
	/**
	 * Removes pending listeners
	 */
	private void removePendingListeners() {
		for (EntityListener listener : removalPendingListeners) {
			listeners.removeValue(listener, true);
		}
		
		removalPendingListeners.clear();
	}
	
	/**
	 * Updates all the systems in this Engine
	 * @param deltaTime The time passed since the last frame
	 */
	public void update(float deltaTime){
		for(int i=0; i<systems.size; i++){
            if (systems.get(i).checkProcessing()) {
                systems.get(i).update(deltaTime);
            }
		}
	}
	
	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
}
