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

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;

@SuppressWarnings("unchecked")
public class EngineTests {

	private float deltaTime = 0.16f;

	private static class ComponentA implements Component {
	}

	private static class ComponentB implements Component {
	}

	private static class ComponentC implements Component {
	}

	private static class EntityListenerMock implements EntityListener {

		public int addedCount = 0;
		public int removedCount = 0;

		@Override
		public void entityAdded (Entity entity) {
			++addedCount;
			assertNotNull(entity);
		}

		@Override
		public void entityRemoved (Entity entity) {
			++removedCount;
			assertNotNull(entity);
		}
	}

	private static class EntitySystemMock extends EntitySystem {
		public int updateCalls = 0;
		public int addedCalls = 0;
		public int removedCalls = 0;

		private Array<Integer> updates;

		public EntitySystemMock () {
			super();
		}

		public EntitySystemMock (Array<Integer> updates) {
			super();

			this.updates = updates;
		}

		@Override
		public void update (float deltaTime) {
			++updateCalls;

			if (updates != null) {
				updates.add(priority);
			}
		}

		@Override
		public void addedToEngine (Engine engine) {
			++addedCalls;

			assertNotNull(engine);
		}

		@Override
		public void removedFromEngine (Engine engine) {
			++removedCalls;

			assertNotNull(engine);
		}
	}

	private static class EntitySystemMockA extends EntitySystemMock {

		public EntitySystemMockA () {
			super();
		}

		public EntitySystemMockA (Array<Integer> updates) {
			super(updates);
		}
	}

	private static class EntitySystemMockB extends EntitySystemMock {

		public EntitySystemMockB () {
			super();
		}

		public EntitySystemMockB (Array<Integer> updates) {
			super(updates);
		}
	}

	private static class CounterComponent implements Component {
		int counter = 0;
	}

	private static class CounterSystem extends EntitySystem {
		private ImmutableArray<Entity> entities;

		@Override
		public void addedToEngine (Engine engine) {
			entities = engine.getEntitiesFor(Family.all(CounterComponent.class).get());
		}

		@Override
		public void update (float deltaTime) {
			for (int i = 0; i < entities.size(); ++i) {
				if (i % 2 == 0) {
					entities.get(i).getComponent(CounterComponent.class).counter++;
				} else {
					getEngine().removeEntity(entities.get(i));
				}
			}
		}
	}

	@Test
	public void addAndRemoveEntity () {
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
	public void addAndRemoveSystem () {
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
	public void getSystems () {
		Engine engine = new Engine();
		EntitySystemMockA systemA = new EntitySystemMockA();
		EntitySystemMockB systemB = new EntitySystemMockB();

		assertEquals(0, engine.getSystems().size());

		engine.addSystem(systemA);
		engine.addSystem(systemB);

		assertEquals(2, engine.getSystems().size());
	}

	@Test
	public void systemUpdate () {
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
	public void systemUpdateOrder () {
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
	public void entitySystemEngineReference () {
		Engine engine = new Engine();
		EntitySystem system = new EntitySystemMock();
		
		assertNull(system.getEngine());
		engine.addSystem(system);
		assertEquals(engine, system.getEngine());
		engine.removeSystem(system);
		assertNull(system.getEngine());
	}

	@Test
	public void ignoreSystem () {
		Engine engine = new Engine();
		EntitySystemMock system = new EntitySystemMock();

		engine.addSystem(system);

		int numUpdates = 10;

		for (int i = 0; i < numUpdates; ++i) {
			system.setProcessing(i % 2 == 0);
			engine.update(deltaTime);
			assertEquals(i / 2 + 1, system.updateCalls);
		}
	}

	@Test
	public void entitiesForFamily () {
		Engine engine = new Engine();

		Family family = Family.all(ComponentA.class, ComponentB.class).get();
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
	public void entityForFamilyWithRemoval () {
		// Test for issue #13
		Engine engine = new Engine();

		Entity entity = new Entity();
		entity.add(new ComponentA());

		engine.addEntity(entity);

		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(ComponentA.class).get());

		assertEquals(1, entities.size());
		assertTrue(entities.contains(entity, true));

		engine.removeEntity(entity);

		assertEquals(0, entities.size());
		assertFalse(entities.contains(entity, true));
	}

	@Test
	public void entitiesForFamilyAfter () {
		Engine engine = new Engine();

		Family family = Family.all(ComponentA.class, ComponentB.class).get();
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
	public void entitiesForFamilyWithRemoval () {
		Engine engine = new Engine();

		Family family = Family.all(ComponentA.class, ComponentB.class).get();
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

	@Test
	public void entitiesForFamilyWithRemovalAndFiltering () {
		Engine engine = new Engine();

		ImmutableArray<Entity> entitiesWithComponentAOnly = engine.getEntitiesFor(Family.all(ComponentA.class)
			.exclude(ComponentB.class).get());

		ImmutableArray<Entity> entitiesWithComponentB = engine.getEntitiesFor(Family.all(ComponentB.class).get());

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();

		engine.addEntity(entity1);
		engine.addEntity(entity2);

		entity1.add(new ComponentA());

		entity2.add(new ComponentA());
		entity2.add(new ComponentB());

		assertEquals(1, entitiesWithComponentAOnly.size());
		assertEquals(1, entitiesWithComponentB.size());

		entity2.remove(ComponentB.class);

		assertEquals(2, entitiesWithComponentAOnly.size());
		assertEquals(0, entitiesWithComponentB.size());
	}

	@Test
	public void entitySystemRemovalWhileIterating () {
		Engine engine = new Engine();

		engine.addSystem(new CounterSystem());

		for (int i = 0; i < 20; ++i) {
			Entity entity = new Entity();
			entity.add(new CounterComponent());
			engine.addEntity(entity);
		}

		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(CounterComponent.class).get());

		for (int i = 0; i < entities.size(); ++i) {
			assertEquals(0, entities.get(i).getComponent(CounterComponent.class).counter);
		}

		engine.update(deltaTime);

		for (int i = 0; i < entities.size(); ++i) {
			assertEquals(1, entities.get(i).getComponent(CounterComponent.class).counter);
		}
	}

	@Test
	public void familyListener () {
		Engine engine = new Engine();

		EntityListenerMock listenerA = new EntityListenerMock();
		EntityListenerMock listenerB = new EntityListenerMock();

		Family familyA = Family.all(ComponentA.class).get();
		Family familyB = Family.all(ComponentB.class).get();

		engine.addEntityListener(familyA, listenerA);
		engine.addEntityListener(familyB, listenerB);

		Entity entity1 = new Entity();
		engine.addEntity(entity1);

		assertEquals(0, listenerA.addedCount);
		assertEquals(0, listenerB.addedCount);

		Entity entity2 = new Entity();
		engine.addEntity(entity2);

		assertEquals(0, listenerA.addedCount);
		assertEquals(0, listenerB.addedCount);

		entity1.add(new ComponentA());

		assertEquals(1, listenerA.addedCount);
		assertEquals(0, listenerB.addedCount);

		entity2.add(new ComponentB());

		assertEquals(1, listenerA.addedCount);
		assertEquals(1, listenerB.addedCount);

		entity1.remove(ComponentA.class);

		assertEquals(1, listenerA.removedCount);
		assertEquals(0, listenerB.removedCount);

		engine.removeEntity(entity2);

		assertEquals(1, listenerA.removedCount);
		assertEquals(1, listenerB.removedCount);

		engine.removeEntityListener(listenerB);

		engine.addEntity(entity2);

		assertEquals(1, listenerA.addedCount);
		assertEquals(1, listenerB.addedCount);

		entity1.add(new ComponentB());
		entity1.add(new ComponentA());

		assertEquals(2, listenerA.addedCount);
		assertEquals(1, listenerB.addedCount);

		engine.removeAllEntities();

		assertEquals(2, listenerA.removedCount);
		assertEquals(1, listenerB.removedCount);

		engine.addEntityListener(listenerB);
	}

	@Test
	public void createManyEntitiesNoStackOverflow () {
		Engine engine = new Engine();
		engine.addSystem(new CounterSystem());

		for (int i = 0; 15000 > i; i++) {
			Entity e = new Entity();
			e.add(new CounterComponent());
			engine.addEntity(e);
		}

		engine.update(0);
	}
	
	@Test
	public void getEntityById () {
		Engine engine = new Engine();
		Entity entity = new Entity();
		
		assertEquals(0L, entity.getId());
		
		engine.addEntity(entity);
		
		long entityId = entity.getId();
		
		assertNotEquals(0L, entityId);
		
		assertEquals(entity, engine.getEntity(entityId));
		
		engine.removeEntity(entity);
		
		assertEquals(null, engine.getEntity(entityId));
	}
	
	@Test
	public void getEntities () {
		int numEntities = 10;
		
		Engine engine = new Engine();
		
		Array<Entity> entities = new Array<Entity>();
		
		for (int i = 0; i < numEntities; ++i) {
			Entity entity = new Entity();
			entities.add(entity);
			engine.addEntity(entity);
		}
		
		ImmutableArray<Entity> engineEntities = engine.getEntities();
		
		assertEquals(entities.size, engineEntities.size());
		
		for (int i = 0; i < numEntities; ++i) {
			assertEquals(entities.get(i), engineEntities.get(i));
		}
		
		engine.removeAllEntities();
		
		assertEquals(0, engineEntities.size());
	}
}
