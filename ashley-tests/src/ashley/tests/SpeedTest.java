package ashley.tests;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;
import ashley.tests.systems.MovementSystem;
import ashley.tests.utils.Timer;
import ashley.utils.Array;

public class SpeedTest {
	public static int NUMBER_ENTITIES = 10000;
	
	public static void main(String[] args){
		Timer timer = new Timer();
		Array<Entity> entities = new Array<>();
		
		Engine engine = new Engine();
		
		engine.addSystem(new MovementSystem());
		
		System.out.println("Number of entities: " + NUMBER_ENTITIES);
		
		/** Adding entities */
		timer.start("entities");
		
		entities.ensureCapacity(NUMBER_ENTITIES);
		
		for(int i=0; i<NUMBER_ENTITIES; i++){
			Entity entity = new Entity();
			engine.addEntity(entity);
			entity.add(new MovementComponent(10, 10));
			entity.add(new PositionComponent(0, 0));
			
			entities.add(entity);
		}
		
		System.out.println("Entities added time: " + timer.stop("entities") + "ms");
		
		/** Removing components */
		timer.start("componentRemoved");
		
		for(Entity e:entities){
			e.remove(PositionComponent.class);
		}
		
		System.out.println("Component removed time: " + timer.stop("componentRemoved") + "ms");
		
		/** Removing entities */
		timer.start("entitiesRemoved");
		
		for(Entity e:entities){
			engine.removeEntity(e);
		}
		
		System.out.println("Entity removed time: " + timer.stop("entitiesRemoved") + "ms");
	}
}
