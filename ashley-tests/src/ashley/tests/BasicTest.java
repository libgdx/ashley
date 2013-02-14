package ashley.tests;

import ashley.core.Component;
import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.utils.Array;

public class BasicTest {
	public static void main(String[] args){
		Engine engine = new Engine();
		
		engine.addSystem(new PositionSystem());
		
		for(int i=0; i<10000; i++){
			Entity entity = new Entity();
			entity.add(new PositionComponent(10, 0));
			engine.addEntity(entity);
		}
		
		for(int i=0; i<1000; i++){
			engine.update(0.25f);
		}
	}
	
	public static class PositionComponent extends Component {
		public PositionComponent(float x, float y){
			this.x = x;
			this.y = y;
		}
		
		float x, y;
	}
	
	public static class PositionSystem extends EntitySystem {
		private Array<Entity> entities;

		@Override
		public void addedToEngine(Engine engine) {
			entities = engine.getEntitiesFor(Family.getFamilyFor(PositionComponent.class));
		}

		@Override
		public void update(float deltaTime) {
			for(Entity e:entities){
				PositionComponent p = e.getComponent(PositionComponent.class);
				
				p.x += 1 * deltaTime;
			}
		}
	}
}
