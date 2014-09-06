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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;
import com.badlogic.gdx.utils.Array;

public class FamilyListenerTest {

	public static int entitesInMovementSystem = 0;
	public static int entitesInPositionSystem = 0;

	public static void main(String[] args) {
		PooledEngine engine = new PooledEngine();

		Family movementFamily = Family.getFor(PositionComponent.class,
				MovementComponent.class);
		MovementSystem movementSystem = new MovementSystem(movementFamily);

		Family positionFamily = Family.getFor(PositionComponent.class);
		PositionSystem positionSystem = new PositionSystem(positionFamily);

		engine.addSystem(movementSystem);
		engine.addSystem(positionSystem);

		Array<Entity> entities = new Array<Entity>();

		for (int i = 0; i < 10; i++) {
			Entity entity = engine.createEntity();
			entity.add(new PositionComponent(10, 0));
			entity.add(new MovementComponent(10, 2));

			engine.addEntity(entity);

			entities.add(entity);
		}

		for (int i = 0; i < 10; i++) {
			engine.update(0.25f);
			checkNumOfEntitiesInSystems(engine, movementFamily, positionFamily);
		}

		for (int i = 0; i < entities.size; i++) {
			if (i >= 5)
				entities.get(i).remove(MovementComponent.class);
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, movementFamily, positionFamily);

		for (int i = 0; i < entities.size; i++) {
			if (i < 5)
				entities.get(i).remove(PositionComponent.class);
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, movementFamily, positionFamily);

		for (int i = 0; i < entities.size; i++) {
			if (i < 5)
				entities.get(i).add(engine.createComponent(PositionComponent.class));
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, movementFamily, positionFamily);
		
		
		for (int i = 0; i < entities.size; i++) {
			if (i >= 5)
				entities.get(i).add(engine.createComponent(MovementComponent.class));
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, movementFamily, positionFamily);

	}

	private static void checkNumOfEntitiesInSystems(PooledEngine engine,
			Family movementFamily, Family positionFamily) {

		boolean test = (entitesInMovementSystem == engine.getEntitiesFor(
				movementFamily).size());
		log("Num entities(" + entitesInMovementSystem
				+ ") in MovementSystem ok?= " + test, !test);

		test = (entitesInPositionSystem == engine
				.getEntitiesFor(positionFamily).size());
		log("Num entities(" + entitesInPositionSystem
				+ ") in PositionSystem ok?= " + test, !test);
	
		log("###################################################");
	}

	public static class PositionSystem extends IteratingSystem {
		public PositionSystem(Family family) {
			super(family);
		}

		@Override
		public void processEntity(Entity e, float deltaTime) {
			// log(this.getClass().getSimpleName() + "= entity [" + e
			// + "] updated.");
		}

		@Override
		public void entityAddedToSystem(Entity e) {
//			log(this.getClass().getSimpleName() + "= [" + e
//					+ "] added to System.");
			FamilyListenerTest.entitesInPositionSystem++;
		}

		@Override
		public void entityRemovedFromSystem(Entity e) {
//			log(this.getClass().getSimpleName() + "= [" + e
//					+ "] removed from System.");
			FamilyListenerTest.entitesInPositionSystem--;
		}

	}

	public static class MovementSystem extends IteratingSystem {

		private ComponentMapper<PositionComponent> pm = ComponentMapper
				.getFor(PositionComponent.class);
		private ComponentMapper<MovementComponent> mm = ComponentMapper
				.getFor(MovementComponent.class);

		public MovementSystem(Family family) {
			super(family);
		}

		@Override
		public void processEntity(Entity e, float deltaTime) {
			PositionComponent p = pm.get(e);
			MovementComponent m = mm.get(e);

			p.x += m.velocityX * deltaTime;
			p.y += m.velocityY * deltaTime;

		}

		@Override
		public void entityAddedToSystem(Entity e) {
//			log(this.getClass().getSimpleName() + "= [" + e
//					+ "] added to System.");
			FamilyListenerTest.entitesInMovementSystem++;
		}

		@Override
		public void entityRemovedFromSystem(Entity e) {
//			log(this.getClass().getSimpleName() + "= [" + e
//					+ "] removed from System.");
			FamilyListenerTest.entitesInMovementSystem--;
		}

	}

	public static void log(String string) {
		log(string, false);
	}

	public static void log(String string, boolean err) {
		if (err)
			System.err.println(string);
		else
			System.out.println(string);
	}
}
