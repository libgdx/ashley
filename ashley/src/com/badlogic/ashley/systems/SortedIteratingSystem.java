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

package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

/**
 * A simple EntitySystem that iterates over each entity in a specified order and calls processEntity() for
 * each entity every time the EntitySystem is updated. This is really just a convenience class as rendering
 * systems tend to iterate over a list of entities in a sorted manner.
 * 
 * @author Santo Pfingsten
 */
public abstract class SortedIteratingSystem extends EntitySystem implements EntityListener {
	/** The family describing this systems entities */
	private Family family;
	/** The entities used by this system */
	private Array<Entity> entities;
	/** The immutable entities used by this system */
	private final ImmutableArray<Entity> immutableEntities;
	/** Set to true if the entities list needs to be resorted */
	private boolean resort;
	/** The comparator to sort the entities */
	private Comparator<Entity> comparator;
	
	/**
	 * Instantiates a system that will iterate over the entities described by the Family.
	 * @param family The family of entities iterated over in this System
	 * @param comparator The comparator to sort the entities
	 */
	public SortedIteratingSystem(Family family, Comparator<Entity> comparator){
		this(family, comparator, 0);
	}
	
	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a 
	 * specific priority.
	 * @param family The family of entities iterated over in this System
	 * @param comparator The comparator to sort the entities
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public SortedIteratingSystem(Family family, Comparator<Entity> comparator, int priority){
		super(priority);
		
		this.family = family;
		entities = new Array<Entity>(false, 16);
		immutableEntities = new ImmutableArray<Entity>(entities);
		this.comparator = comparator;
	}
		
	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntityListener(family, this);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		entities.clear();
	}

	@Override
	public void entityAdded(Entity entity) {
		entities.add(entity);
		resort = true;
	}

	@Override
	public void entityRemoved(Entity entity) {
		entities.removeValue(entity, true);
	}

	@Override
	public void update(float deltaTime) {
		if (resort) {
			entities.sort(comparator);
			resort = false;
		}
		for (int i = 0; i < entities.size; ++i) {
			processEntity(entities.get(i), deltaTime);
		}
	}
	
	/**
	 * @return set of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities() {
		if (resort) {
			entities.sort(comparator);
			resort = false;
		}
		return immutableEntities;
	}

	/**
	 * This method is called on every entity on every update call of the EntitySystem. Override this to implement
	 * your system's specific processing.
	 * @param entity The current Entity being processed
	 * @param deltaTime The delta time between the last and current frame
	 */
	protected abstract void processEntity(Entity entity, float deltaTime);
}
