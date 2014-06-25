package com.badlogic.ashley.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;
import com.badlogic.ashley.tests.systems.MovementSystem;
import com.badlogic.ashley.tests.utils.Timer;
import com.badlogic.ashley.utils.Array;

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
