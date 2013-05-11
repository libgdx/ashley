package ashley.tests;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;
import ashley.tests.systems.MovementSystem;
import ashley.tests.utils.Timer;
import ashley.utils.Array;

public class SpeedTest {
	public static int NUMBER_ENTITIES = 100000;
	
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
			Entity entity = engine.createEntity();
			
			entity.add(new MovementComponent(10, 10));
			entity.add(new PositionComponent(0, 0));
			
			engine.addEntity(entity);
			
			entities.add(entity);
		}
		
		System.out.println("Entities added time: " + timer.stop("entities") + "ms");
		
		/** Removing components */
		timer.start("componentRemoved");
		
		for(Entity e:entities){
			e.remove(PositionComponent.class);
		}
		
		System.out.println("Component removed time: " + timer.stop("componentRemoved") + "ms");
		
		/** Adding components */
		timer.start("componentAdded");
		
		for(Entity e:entities){
			e.add(new PositionComponent(0, 0));
		}
		
		System.out.println("Component added time: " + timer.stop("componentAdded") + "ms");
		
		/** System processing */
		timer.start("systemProcessing");
		
		engine.update(0);
		
		System.out.println("System processing times " + timer.stop("systemProcessing") + "ms");
		
		/** Removing entities */
		timer.start("entitiesRemoved");
		
		for(Entity e:entities){
			engine.removeEntity(e);
		}
		
		System.out.println("Entity removed time: " + timer.stop("entitiesRemoved") + "ms");
	}
}
