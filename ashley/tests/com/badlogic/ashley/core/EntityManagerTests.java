package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public class EntityManagerTests {

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
	
	@Test
	public void addAndRemoveEntity () {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);

		Entity entity1 = new Entity();
		manager.addEntity(entity1);

		assertEquals(1, listener.addedCount);
		Entity entity2 = new Entity();
		manager.addEntity(entity2);

		assertEquals(2, listener.addedCount);

		manager.removeAllEntities();

		assertEquals(2, listener.removedCount);
	}
	
	@Test
	public void getEntities () {
		int numEntities = 10;
		
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);
		
		Array<Entity> entities = new Array<Entity>();
		
		for (int i = 0; i < numEntities; ++i) {
			Entity entity = new Entity();
			entities.add(entity);
			manager.addEntity(entity);
		}
		
		ImmutableArray<Entity> engineEntities = manager.getEntities();
		
		assertEquals(entities.size, engineEntities.size());
		
		for (int i = 0; i < numEntities; ++i) {
			assertEquals(entities.get(i), engineEntities.get(i));
		}
		
		manager.removeAllEntities();
		
		assertEquals(0, engineEntities.size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addEntityTwice1 () {
		 EntityListenerMock listener = new EntityListenerMock();
		 EntityManager manager = new EntityManager(listener);
	    Entity entity = new Entity();
	    manager.addEntity(entity);
	    manager.addEntity(entity);
	}

	@Test(expected=IllegalArgumentException.class)
	public void addEntityTwice2() {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);
		Entity entity = new Entity();
		manager.addEntity(entity, false);
		manager.addEntity(entity, false);
	}

	@Test(expected=IllegalArgumentException.class)
	public void addEntityTwiceDelayed() {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);

		Entity entity = new Entity();
		manager.addEntity(entity, true);
		manager.addEntity(entity, true);
		manager.processPendingOperations();
	}

	@Test
	public void delayedOperationsOrder() {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);
		
		Entity entityA = new Entity();
		Entity entityB = new Entity();
		
		boolean delayed = true;
		manager.addEntity(entityA);
		manager.addEntity(entityB);
		
		assertEquals(2, manager.getEntities().size());
		
		Entity entityC = new Entity();
		Entity entityD = new Entity();
		manager.removeAllEntities(delayed);
		manager.addEntity(entityC, delayed);
		manager.addEntity(entityD, delayed);
		manager.processPendingOperations();
		
		assertEquals(2, manager.getEntities().size());
		assertNotEquals(-1, manager.getEntities().indexOf(entityC, true));
		assertNotEquals(-1, manager.getEntities().indexOf(entityD, true));
	}

	@Test
	public void removeAndAddEntityDelayed() {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);

		Entity entity = new Entity();
		manager.addEntity(entity, false);     // immediate
		assertEquals(1, manager.getEntities().size());

		manager.removeEntity(entity, true);   // delayed
		assertEquals(1, manager.getEntities().size());

		manager.addEntity(entity, true);      // delayed
		assertEquals(1, manager.getEntities().size());

		manager.processPendingOperations();
		assertEquals(1, manager.getEntities().size());
	}

	@Test
	public void removeAllAndAddEntityDelayed() {
		EntityListenerMock listener = new EntityListenerMock();
		EntityManager manager = new EntityManager(listener);

		Entity entity = new Entity();
		manager.addEntity(entity, false);  // immediate
		assertEquals(1, manager.getEntities().size());

		manager.removeAllEntities(true);   // delayed
		assertEquals(1, manager.getEntities().size());

		manager.addEntity(entity, true);   // delayed
		assertEquals(1, manager.getEntities().size());

		manager.processPendingOperations();
		assertEquals(1, manager.getEntities().size());
	}
}
