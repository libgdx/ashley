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

package com.badlogic.ashley.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;
import com.badlogic.ashley.tests.systems.MovementSystem;
import com.badlogic.ashley.tests.utils.Timer;
import com.badlogic.gdx.utils.Array;

public class SpeedTest {
	public static int NUMBER_ENTITIES = 100000;
	
	public static void main(String[] args){
		Timer timer = new Timer();
		Array<Entity> entities = new Array<>();
		
		PooledEngine engine = new PooledEngine();
		
		engine.addSystem(new MovementSystem());
		
		System.out.println("Number of entities: " + NUMBER_ENTITIES);
		
		/** Adding entities */
		timer.start("entities");
		
		entities.ensureCapacity(NUMBER_ENTITIES);
		
		for(int i=0; i<NUMBER_ENTITIES; i++){
			Entity entity = engine.createEntity();
			
			entity.add(new MovementComponent(10, 10));
			entity.add(new PositionComponent(0, 0));
			
			engine.addEntity(entity);
			
			entities.add(entity);
		}
		
		System.out.println("Entities added time: " + timer.stop("entities") + "ms");
		
		/** Removing components */
		timer.start("componentRemoved");
		
		for(Entity e:entities){
			e.remove(PositionComponent.class);
		}
		
		System.out.println("Component removed time: " + timer.stop("componentRemoved") + "ms");
		
		/** Adding components */
		timer.start("componentAdded");
		
		for(Entity e:entities){
			e.add(new PositionComponent(0, 0));
		}
		
		System.out.println("Component added time: " + timer.stop("componentAdded") + "ms");
		
		/** System processing */
		timer.start("systemProcessing");
		
		engine.update(0);
		
		System.out.println("System processing times " + timer.stop("systemProcessing") + "ms");
		
		/** Removing entities */
		timer.start("entitiesRemoved");
		
		engine.removeAllEntities();
		
		System.out.println("Entity removed time: " + timer.stop("entitiesRemoved") + "ms");
	}
}
