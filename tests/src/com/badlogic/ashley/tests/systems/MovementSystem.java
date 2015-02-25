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

package com.badlogic.ashley.tests.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;

public class MovementSystem extends IteratingSystem {
	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
	private ComponentMapper<MovementComponent> mm = ComponentMapper.getFor(MovementComponent.class);

	public MovementSystem () {
		super(Family.all(PositionComponent.class, MovementComponent.class).get());
	}

	@Override
	public void processEntity (Entity entity, float deltaTime) {
		PositionComponent position = pm.get(entity);
		MovementComponent movement = mm.get(entity);

		position.x += movement.velocityX * deltaTime;
		position.y += movement.velocityY * deltaTime;
	}
}
