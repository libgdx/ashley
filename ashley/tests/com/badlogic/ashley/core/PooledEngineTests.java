package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;

public class PooledEngineTests {
	private float deltaTime = 0.16f;
	
	public static class PositionComponent extends Component {
		public float x = 0.0f;
		public float y = 0.0f;
	}
	
	public static class MyPositionListener implements EntityListener {
		public static ComponentMapper<PositionComponent> positionMapper = ComponentMapper
				.getFor(PositionComponent.class);

		int counter = 0;

		@Override
		public void entityAdded(Entity entity) {

		}

		@Override
		public void entityRemoved(Entity entity) {
			PositionComponent position = positionMapper.get(entity);
			assertNotNull(position);
		}
	}

	public static class CombinedSystem extends EntitySystem {
		private Engine engine;
		private ImmutableArray<Entity> allEntities;
		private int counter = 0;
		
		public CombinedSystem(Engine engine) {
			this.engine = engine;
		}

		@Override
		public void addedToEngine(Engine engine) {
			allEntities = engine.getEntitiesFor(Family
					.getFor(PositionComponent.class));
		}

		@Override
		public void update(float deltaTime) {
			if (counter >= 6 && counter <= 8) {
				engine.removeEntity(allEntities.get(2));
			}
			counter++;
		}
	}
	
	public static class ComponentCounterListener implements Listener<Entity> {
		public int totalCalls = 0;
		
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			totalCalls++;
		}
	}
	
	@Test
	public void entityRemovalListenerOrder() {
		PooledEngine engine = new PooledEngine();

        CombinedSystem combinedSystem = new CombinedSystem(engine);

        engine.addSystem(combinedSystem);
        engine.addEntityListener(Family
                .getFor(PositionComponent.class), new MyPositionListener());


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
	public void resetEntityCorrectly() {
		PooledEngine engine = new PooledEngine();
		
		ComponentCounterListener addedListener = new ComponentCounterListener();
		ComponentCounterListener removedListener = new ComponentCounterListener();
		
		Entity[] entities = new Entity[10];
		final int totalEntities = 10;
		
		for(int i = 0; i < totalEntities; i++) {
			entities[i] = engine.createEntity();
			entities[i].componentAdded.add(addedListener);
			entities[i].componentRemoved.add(removedListener);
			entities[i].add(engine.createComponent(PositionComponent.class));
			engine.addEntity(entities[i]);
			
			assertNotNull(entities[i].componentOperationHandler);
			assertEquals(1, entities[i].getComponents().size());
		}
		
		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(0, removedListener.totalCalls);
		
		engine.removeAllEntities();
		
		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);
		
		for(int i = 0; i < totalEntities; i++) {
			assertEquals(0, entities[i].flags);
			assertNull(entities[i].componentOperationHandler);
			assertEquals(0, entities[i].getComponents().size());
		}
	}
}
