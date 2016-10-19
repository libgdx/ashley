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

import com.badlogic.ashley.utils.ImmutableArray;

/**
 * A simple EntityIterator that iterates over each entity and calls a callback for each entity every time
 * the EntitySystem is updated. This is really just a convenience class as most systems iterate over many
 * lists of entities.
 * 
 * @author Santo Pfingsten
 */
public class EntityIterator {
	private Family family;
	private ImmutableArray<Entity> entities;
	
	/**
	 * Instantiates an iterator that will iterate over the entities described by the Family.
	 * @param family The family of entities iterated over in this System
	 */
	public EntityIterator(Family family){
		this.family = family;
	}

	/**
	 * Call when this EntityIterator is added to an {@link Engine}.
	 * @param engine The {@link Engine} this iterator was added to.
	 */
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(family);
	}

	/**
	 * Call when this EntityIterator is removed from an {@link Engine}.
	 * @param engine The {@link Engine} the iterator was removed from.
	 */
	public void removedFromEngine(Engine engine) {
		entities = null;
	}

	/**
	 * Call to iterate over all entities.
	 * @param callback The callback to call for all entities
	 * @param param An object parameter to pass
	 * @param <T> Parameter type passed to the callback
	 */
	public <T> void iterate(Callback<T> callback, T param) {
		for (int i = 0; i < entities.size(); ++i) {
			callback.processEntity(entities.get(i), param);
		}
	}
	
	/**
	 * @return set of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities() {
		return entities;
	}

	public static interface Callback<T> {
		/**
		 * This method is called on every entity on every iterate call of the EntityIterator.
		 * Override this to implement your iterator's specific processing.
		 * @param entity The current Entity being processed
		 * @param param An object parameter to pass
		 */
		void processEntity(Entity entity, T param);
	}
}
