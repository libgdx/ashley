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

package com.badlogic.ashley.benchmark.artemis.systems;



import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.ashley.benchmark.artemis.components.MovementComponent;
import com.badlogic.ashley.benchmark.artemis.components.PositionComponent;
import com.badlogic.gdx.math.Vector2;

public class MovementSystem extends EntityProcessingSystem {
	private Vector2 tmp = new Vector2();
	@Mapper ComponentMapper<PositionComponent> pm;
	@Mapper ComponentMapper<MovementComponent> mm;

	public MovementSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, MovementComponent.class));
	}
	
	@Override
	protected void initialize() {
		pm = world.getMapper(PositionComponent.class);
		mm = world.getMapper(MovementComponent.class);
	};
	
	@Override
	protected void process(Entity entity) {
		PositionComponent pos = pm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		tmp.set(mov.accel).scl(world.getDelta());
		mov.velocity.add(tmp);
		
		tmp.set(mov.velocity).scl(world.getDelta());
		pos.pos.add(tmp.x, tmp.y, 0.0f);
	}
}
