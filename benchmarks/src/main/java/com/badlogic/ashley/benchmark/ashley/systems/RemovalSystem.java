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

package com.badlogic.ashley.benchmark.ashley.systems;

import com.badlogic.ashley.benchmark.ashley.components.RemovalComponent;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class RemovalSystem extends EntitySystem {
	private Engine engine;
	private ImmutableArray<Entity> entities;
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		entities = engine.getEntitiesFor(Family.getFor(RemovalComponent.class));
	}

	@Override
	public void update(float deltaTime) {
		while (entities.size() > 0) {
			engine.removeEntity(entities.get(0));
		}
	}
}
