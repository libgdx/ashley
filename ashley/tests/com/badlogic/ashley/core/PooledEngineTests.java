package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

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
		
		Entity entity = engine.createEntity();
		entity.add(engine.createComponent(PositionComponent.class));
		engine.addEntity(entity);
		
		assertEquals(entity.componentAdded.countListeners(), 1);
		assertEquals(entity.componentRemoved.countListeners(), 1);
		assertNotEquals(entity.componentOperationHandler, null);
		
		engine.removeAllEntities();
		
		assertEquals(entity.componentAdded.countListeners(), 0);
		assertEquals(entity.componentRemoved.countListeners(), 0);
		assertEquals(entity.componentOperationHandler, null);
	}
}
