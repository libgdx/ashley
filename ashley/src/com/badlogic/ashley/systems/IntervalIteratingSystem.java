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
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * A simple {@link EntitySystem} that processes a {@link Family} of entities not once per frame, but after a given interval.
 * Entity processing logic should be placed in {@link IntervalIteratingSystem#processEntity(Entity)}.
 * @author David Saltares
 */
public abstract class IntervalIteratingSystem extends IntervalSystem {
	private Family family;
	private ImmutableArray<Entity> entities;

	/**
	 * @param family represents the collection of family the system should process
	 * @param interval time in seconds between calls to {@link IntervalIteratingSystem#updateInterval()}.
	 */
	public IntervalIteratingSystem (Family family, float interval) {
		this(family, interval, 0);
	}

	/**
	 * @param family represents the collection of family the system should process
	 * @param interval time in seconds between calls to {@link IntervalIteratingSystem#updateInterval()}.
	 * @param priority
	 */
	public IntervalIteratingSystem (Family family, float interval, int priority) {
		super(interval, priority);
		this.family = family;
	}

	@Override
	public void addedToEngine (Engine engine) {
		entities = engine.getEntitiesFor(family);
	}

	@Override
	protected void updateInterval () {
		for (int i = 0; i < entities.size(); ++i) {
			processEntity(entities.get(i));
		}
	}

	/**
	 * @return set of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities () {
		return entities;
	}

    /**
	 * @return the Family used when the system was created
	 */
	public Family getFamily () {
		return family;
	}

	/**
	 * The user should place the entity processing logic here.
	 * @param entity
	 */
	protected abstract void processEntity (Entity entity);
}
