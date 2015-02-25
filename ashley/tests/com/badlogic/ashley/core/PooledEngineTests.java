
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

	public static class PositionComponent extends Component {
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
		private Engine engine;
		private ImmutableArray<Entity> allEntities;
		private int counter = 0;

		public CombinedSystem (Engine engine) {
			this.engine = engine;
		}

		@Override
		public void addedToEngine (Engine engine) {
			allEntities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
		}

		@Override
		public void update (float deltaTime) {
			if (counter >= 6 && counter <= 8) {
				engine.removeEntity(allEntities.get(2));
			}
			counter++;
		}
	}

	public static class ComponentCounterListener implements Listener<EntityEvent> {
		public int totalCalls = 0;

		@Override
		public void receive(Signal<EntityEvent> signal, EntityEvent event) {
			totalCalls++;
		}
	}

	private static class RemoveEntityTwiceSystem extends EntitySystem {
		private ImmutableArray<Entity> entities;
		private PooledEngine engine;

		@Override
		public void addedToEngine (Engine engine) {
			entities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
			this.engine = (PooledEngine)engine;
		}

		@Override
		public void update (float deltaTime) {
			Entity entity;
			for (int i = 0; i < 10; i++) {
				entity = engine.createEntity();
				assertEquals(0, entity.flags);
				entity.flags = 1;
				entity.add(engine.createComponent(PositionComponent.class));
				engine.addEntity(entity);
			}
			for (int i = 0; i < entities.size(); ++i) {
				entity = entities.get(i);
				engine.removeEntity(entity);
				engine.removeEntity(entity);
			}
		}
	}
	
	private static class PooledComponentSpy extends Component implements Poolable {
		public boolean recycled = false;
		
		
		@Override
		public void reset () {
			recycled = true;
		}
	}

	@Test
	public void entityRemovalListenerOrder () {
		PooledEngine engine = new PooledEngine();

		CombinedSystem combinedSystem = new CombinedSystem(engine);

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
		EntityEvent event = new EntityEvent();

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
			assertEquals(0, entities[i].flags);
			assertNull(entities[i].componentOperationHandler);
			assertEquals(0, entities[i].getComponents().size());
			assertTrue(entities[i].getFamilyBits().isEmpty());
			assertFalse(familyEntities.contains(entities[i], true));
			assertEquals(0L, entities[i].getId());
	
			event.setEntity(entities[i]);
			entities[i].componentAdded.dispatch(event);
			entities[i].componentRemoved.dispatch(event);
		}

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);
	}

	@Test
	public void recycleEntity () {
		PooledEngine engine = new PooledEngine();

		int numEntities = 200;
		Array<Entity> entities = new Array<Entity>();

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			engine.addEntity(entity);
			entities.add(entity);

			assertNotEquals(0L, entity.getId());
		}

		for (Entity entity : entities) {
			engine.removeEntity(entity);
			assertEquals(0L, entity.getId());
		}

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			engine.addEntity(entity);
			entities.add(entity);

			assertNotEquals(0L, entity.getId());
		}
	}

	@Test
	public void removeEntityTwice () {
		PooledEngine engine = new PooledEngine();
		engine.addSystem(new RemoveEntityTwiceSystem());

		for (int j = 0; j < 2; j++) {
			engine.update(0);
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
}
