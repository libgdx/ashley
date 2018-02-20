
package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PooledEngineTests {
	private float deltaTime = 0.16f;

	private final ComponentMapper<PoolableComponent> poolableMapper = ComponentMapper.getFor(PoolableComponent.class);

	public static class ComponentA implements Component {
		public ComponentA(){}
	}

	public static class PoolableComponent implements Component, Poolable {
		boolean reset = true;
		@Override
		public void reset() {
			reset = true;
		}
	}

	public static class PositionComponent implements Component {
		public float x = 0.0f;
		public float y = 0.0f;
	}

	public static class MyPositionListener implements EntityListener {
		public static ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

		int counter = 0;

		@Override
		public void entityAdded (Entity entity) {

		}

		@Override
		public void entityRemoved (Entity entity) {
			PositionComponent position = positionMapper.get(entity);
			assertNotNull(position);
		}
	}

	public static class CombinedSystem extends EntitySystem {
		private ImmutableArray<Entity> allEntities;
		private int counter = 0;

		@Override
		public void addedToEngine (Engine engine) {
			allEntities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
		}

		@Override
		public void update (float deltaTime) {
			if (counter >= 6 && counter <= 8) {
				getEngine().removeEntity(allEntities.get(2));
			}
			counter++;
		}
	}

	public static class ComponentCounterListener implements Listener<Entity> {
		public int totalCalls = 0;

		@Override
		public void receive (Signal<Entity> signal, Entity object) {
			totalCalls++;
		}
	}
	
	private static class PooledComponentSpy implements Component, Poolable {
		public boolean recycled = false;
		
		
		@Override
		public void reset () {
			recycled = true;
		}
	}

	@Test
	public void entityRemovalListenerOrder () {
		PooledEngine engine = new PooledEngine();

		CombinedSystem combinedSystem = new CombinedSystem();

		engine.addSystem(combinedSystem);
		engine.addEntityListener(Family.all(PositionComponent.class).get(), new MyPositionListener());

		for (int i = 0; i < 10; i++) {
			Entity entity = engine.createEntity();
			entity.add(engine.createComponent(PositionComponent.class));
			engine.addEntity(entity);
		}

		assertEquals(10, combinedSystem.allEntities.size());

		for (int i = 0; i < 10; i++) {
			engine.update(deltaTime);
		}

		engine.removeAllEntities();
	}

	@Test
	public void resetEntityCorrectly () {
		PooledEngine engine = new PooledEngine();

		ComponentCounterListener addedListener = new ComponentCounterListener();
		ComponentCounterListener removedListener = new ComponentCounterListener();

		// force the engine to create a Family so family bits get set
		ImmutableArray<Entity> familyEntities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());

		Entity[] entities = new Entity[10];
		final int totalEntities = 10;

		for (int i = 0; i < totalEntities; i++) {
			entities[i] = engine.createEntity();

			entities[i].flags = 5;

			entities[i].componentAdded.add(addedListener);
			entities[i].componentRemoved.add(removedListener);

			entities[i].add(engine.createComponent(PositionComponent.class));
			engine.addEntity(entities[i]);

			assertNotNull(entities[i].componentOperationHandler);
			assertEquals(1, entities[i].getComponents().size());
			assertFalse(entities[i].getFamilyBits().isEmpty());
			assertTrue(familyEntities.contains(entities[i], true));
		}

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(0, removedListener.totalCalls);

		engine.removeAllEntities();

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);

		for (int i = 0; i < totalEntities; i++) {
			assertFalse(entities[i].removing);
			assertEquals(0, entities[i].flags);
			assertNull(entities[i].componentOperationHandler);
			assertEquals(0, entities[i].getComponents().size());
			assertTrue(entities[i].getFamilyBits().isEmpty());
			assertFalse(familyEntities.contains(entities[i], true));
			
			entities[i].componentAdded.dispatch(entities[i]);
			entities[i].componentRemoved.dispatch(entities[i]);
		}

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);
	}

	@Test
	public void recycleEntity () {
		int numEntities = 5;
		PooledEngine engine = new PooledEngine(numEntities, 100, 0, 100);
		Array<Entity> entities = new Array<Entity>();

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			assertFalse(entity.removing);
			assertEquals(0, entity.flags);
			engine.addEntity(entity);
			entities.add(entity);
			entity.flags = 1;
		}

		for (Entity entity : entities) {
			engine.removeEntity(entity);
			assertEquals(0, entity.flags);
			assertFalse(entity.removing);
		}

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			assertEquals(0, entity.flags);
			assertFalse(entity.removing);
			assertTrue(entities.contains(entity, true));
		}
	}

	@Test
	public void removeEntityTwice () {
		PooledEngine engine = new PooledEngine();

		for (int i = 0; i < 100; ++i) {
			Array<Entity> entities = new Array<Entity>();

			for (int j = 0; j < 100; ++j) {
				Entity entity = engine.createEntity();
				engine.addEntity(entity);
				assertEquals(0, entity.flags);
				entity.flags = 1;
				entities.add(entity);
			}

			for (Entity entity : entities) {
				engine.removeEntity(entity);
				engine.removeEntity(entity);
			}
		}
	}
	
	@Test
	public void recycleComponent() {
		int maxEntities = 10;
		int maxComponents = 10;
		PooledEngine engine = new PooledEngine(maxEntities, maxEntities, maxComponents, maxComponents);
		
		for (int i = 0; i < maxComponents; ++i) {
			Entity e = engine.createEntity();
			PooledComponentSpy c = engine.createComponent(PooledComponentSpy.class);
			
			assertEquals(false, c.recycled);
			
			e.add(c);
			
			engine.addEntity(e);
		}
		
		engine.removeAllEntities();
		
		for (int i = 0; i < maxComponents; ++i) {
			Entity e = engine.createEntity();
			PooledComponentSpy c = engine.createComponent(PooledComponentSpy.class);
			
			assertEquals(true, c.recycled);
			
			e.add(c);
		}
		
		engine.removeAllEntities();
	}

	@Test
	public void createNewComponent () {
		PooledEngine engine = new PooledEngine();
		ComponentA componentA = engine.createComponent(ComponentA.class);

		assertNotNull(componentA);
	}

	@Test
	public void addSameComponentShouldResetAndReturnOldComponentToPool () {
		PooledEngine engine = new PooledEngine();

		PoolableComponent component1 = engine.createComponent(PoolableComponent.class);
		component1.reset = false;
		PoolableComponent component2 = engine.createComponent(PoolableComponent.class);
		component2.reset = false;

		Entity entity = engine.createEntity();
		entity.add(component1);
		entity.add(component2);

		assertEquals(1, entity.getComponents().size());
		assertTrue(poolableMapper.has(entity));
		assertNotEquals(component1, poolableMapper.get(entity));
		assertEquals(component2, poolableMapper.get(entity));

		assertTrue(component1.reset);
	}

	@Test
	public void removeComponentReturnsItToThePoolExactlyOnce() {
		PooledEngine engine = new PooledEngine();

		PoolableComponent removedComponent = engine.createComponent(PoolableComponent.class);

		Entity entity = engine.createEntity();
		entity.add(removedComponent);

		entity.remove(PoolableComponent.class);

		PoolableComponent newComponent1 = engine.createComponent(PoolableComponent.class);
		PoolableComponent newComponent2 = engine.createComponent(PoolableComponent.class);

		assertNotEquals(newComponent1, newComponent2);
	}
}
