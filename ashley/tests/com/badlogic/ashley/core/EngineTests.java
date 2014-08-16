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

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public class EngineTests {
	
	private float deltaTime = 0.16f;
	
	private static class ComponentA extends Component {}
	
	private static class ComponentB extends Component {}
	
	private static class ComponentC extends Component {}
	
	private static class EntityListenerMock implements EntityListener {

		public int addedCount = 0;
		public int removedCount = 0;
		
		@Override
		public boolean entityAdded(Entity entity) {
			++addedCount;
			assertNotNull(entity);
			return true;
		}

		@Override
		public void entityRemoved(Entity entity) {
			++removedCount;
			assertNotNull(entity);
		}
	}
	
	private static class EntitySystemMock extends EntitySystem {
		public int updateCalls = 0;
		public int addedCalls = 0;
		public int removedCalls = 0;
		
		private Array<Integer> updates;
		private boolean active = true;
		
		
		public EntitySystemMock() {
			super();
		}
		
		public EntitySystemMock(Array<Integer> updates) {
			super();
			
			this.updates = updates;
		}
		
		@Override
		public void update(float deltaTime){
			++updateCalls;
			
			if (updates != null) {
				updates.add(priority);
			}
		}
		
		@Override
		public void addedToEngine(Engine engine){
			++addedCalls;
			
			assertNotNull(engine);
		}
		
		@Override
		public void removedFromEngine(Engine engine){
			++removedCalls;
			
			assertNotNull(engine);
		}
		
		@Override
		public boolean checkProcessing() {
			return active;
		}
		
		public void setActive(boolean active) {
			this.active = active;
		}
	}
	
	private static class EntitySystemMockA extends EntitySystemMock {

		public EntitySystemMockA() {
			super();
		}
		
		public EntitySystemMockA(Array<Integer> updates) {
			super(updates);
		}
	}
	
	private static class EntitySystemMockB extends EntitySystemMock {

		public EntitySystemMockB() {
			super();
		}
		
		public EntitySystemMockB(Array<Integer> updates) {
			super(updates);
		}
	}
	
	@Test
	public void addAndRemoveEntity() {
		Engine engine = new Engine();
		
		EntityListenerMock listenerA = new EntityListenerMock();
		EntityListenerMock listenerB = new EntityListenerMock();
		
		engine.addEntityListener(listenerA);
		engine.addEntityListener(listenerB);

		Entity entity1 = new Entity();
		engine.addEntity(entity1);
		
		assertEquals(1, listenerA.addedCount);
		assertEquals(1, listenerB.addedCount);
		
		engine.removeEntityListener(listenerB);
		
		Entity entity2 = new Entity();
		engine.addEntity(entity2);
		
		assertEquals(2, listenerA.addedCount);
		assertEquals(1, listenerB.addedCount);
		
		engine.addEntityListener(listenerB);
		
		engine.removeAllEntities();
		
		assertEquals(2, listenerA.removedCount);
		assertEquals(2, listenerB.removedCount);
	}

	@Test
	public void addAndRemoveSystem() {
		Engine engine = new Engine();
		EntitySystemMockA systemA = new EntitySystemMockA();
		EntitySystemMockB systemB = new EntitySystemMockB();
		
		assertNull(engine.getSystem(EntitySystemMockA.class));
		assertNull(engine.getSystem(EntitySystemMockB.class));
		
		engine.addSystem(systemA);
		engine.addSystem(systemB);
		
		assertNotNull(engine.getSystem(EntitySystemMockA.class));
		assertNotNull(engine.getSystem(EntitySystemMockB.class));
		assertEquals(1, systemA.addedCalls);
		assertEquals(1, systemB.addedCalls);
		
		engine.removeSystem(systemA);
		engine.removeSystem(systemB);
		
		assertNull(engine.getSystem(EntitySystemMockA.class));
		assertNull(engine.getSystem(EntitySystemMockB.class));
		assertEquals(1, systemA.removedCalls);
		assertEquals(1, systemB.removedCalls);
	}
	
	@Test
	public void systemUpdate() {
		Engine engine = new Engine();
		EntitySystemMock systemA = new EntitySystemMockA();
		EntitySystemMock systemB = new EntitySystemMockB();
		
		engine.addSystem(systemA);
		engine.addSystem(systemB);
		
		int numUpdates = 10;
		
		for (int i = 0; i < numUpdates; ++i) {
			assertEquals(i, systemA.updateCalls);
			assertEquals(i, systemB.updateCalls);
			
			engine.update(deltaTime);
			
			assertEquals(i + 1, systemA.updateCalls);
			assertEquals(i + 1, systemB.updateCalls);
		}
		
		engine.removeSystem(systemB);
		
		for (int i = 0; i < numUpdates; ++i) {
			assertEquals(i + numUpdates, systemA.updateCalls);
			assertEquals(numUpdates, systemB.updateCalls);
			
			engine.update(deltaTime);
			
			assertEquals(i + 1 + numUpdates, systemA.updateCalls);
			assertEquals(numUpdates, systemB.updateCalls);
		}
	}
	
	@Test
	public void systemUpdateOrder() {
		Array<Integer> updates = new Array<Integer>();
		
		Engine engine = new Engine();
		EntitySystemMock system1 = new EntitySystemMockA(updates);
		EntitySystemMock system2 = new EntitySystemMockB(updates);
		
		system1.priority = 2;
		system2.priority = 1;
		
		engine.addSystem(system1);
		engine.addSystem(system2);
		
		engine.update(deltaTime);
		
		int previous = Integer.MIN_VALUE;
		
		for (Integer value : updates) {
			assertTrue(value >= previous);
			previous = value;
		}
	}
	
	@Test
	public void ignoreSystem() {
		Engine engine = new Engine();
		EntitySystemMock system = new EntitySystemMock();
		
		engine.addSystem(system);
		
		int numUpdates = 10;
		
		for (int i = 0; i < numUpdates; ++i) {
			system.setActive(i % 2 == 0);
			engine.update(deltaTime);
			assertEquals(i / 2 + 1, system.updateCalls);
		}
	}

	@Test
	public void entitiesForFamily() {
		Engine engine = new Engine();
		
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		ImmutableArray<Entity> familyEntities = engine.getEntitiesFor(family);
		
		assertEquals(0, familyEntities.size());
		
		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();
		
		entity1.add(new ComponentA());
		entity1.add(new ComponentB());
		
		entity2.add(new ComponentA());
		entity2.add(new ComponentC());
		
		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());
		
		engine.addEntity(entity1);
		engine.addEntity(entity2);
		engine.addEntity(entity3);
		engine.addEntity(entity4);
		
		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
	}
	
	@Test
	public void entityForFamilyWithRemoval() {
		// Test for issue #13
		Engine engine = new Engine();
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		
		engine.addEntity(entity);
		
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.getFor(ComponentA.class));
		
		assertEquals(1, entities.size());
		assertTrue(entities.contains(entity, true));
		
		engine.removeEntity(entity);
		
		ImmutableArray<Entity> entitiesAfter = engine.getEntitiesFor(Family.getFor(ComponentA.class));
		
		assertEquals(0, entities.size());
		assertFalse(entities.contains(entity, true));
	}
	
	@Test
	public void entitiesForFamilyAfter() {
		Engine engine = new Engine();
		
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		ImmutableArray<Entity> familyEntities = engine.getEntitiesFor(family);
		
		assertEquals(0, familyEntities.size());
		
		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();
		
		engine.addEntity(entity1);
		engine.addEntity(entity2);
		engine.addEntity(entity3);
		engine.addEntity(entity4);
		
		entity1.add(new ComponentA());
		entity1.add(new ComponentB());
		
		entity2.add(new ComponentA());
		entity2.add(new ComponentC());
		
		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());
		
		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
	}
	
	@Test
	public void entitiesForFamilyWithRemoval() {
		Engine engine = new Engine();
		
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		ImmutableArray<Entity> familyEntities = engine.getEntitiesFor(family);
		
		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();
		
		engine.addEntity(entity1);
		engine.addEntity(entity2);
		engine.addEntity(entity3);
		engine.addEntity(entity4);
		
		entity1.add(new ComponentA());
		entity1.add(new ComponentB());
		
		entity2.add(new ComponentA());
		entity2.add(new ComponentC());
		
		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());
		
		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
		
		entity1.remove(ComponentA.class);
		engine.removeEntity(entity3);
		
		assertEquals(1, familyEntities.size());
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity1, true));
		assertFalse(familyEntities.contains(entity3, true));
		assertFalse(familyEntities.contains(entity2, true));
	}
}
