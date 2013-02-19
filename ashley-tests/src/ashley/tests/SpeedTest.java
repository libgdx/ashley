package ashley.tests;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;
import ashley.tests.systems.MovementSystem;
import ashley.tests.utils.Timer;
import ashley.utils.Array;

public class SpeedTest {
	public static void main(String[] args){
		Timer timer = new Timer();
		Array<Entity> entities = new Array<>();
		
		Engine engine = new Engine();
		
		engine.addSystem(new MovementSystem());
		
		timer.start("entities");
		
		for(int i=0; i<10000; i++){
			Entity entity = new Entity();
			entity.add(new MovementComponent(10, 10));
			entity.add(new PositionComponent(0, 0));
			
			entities.add(entity);
		}
		
		System.out.println("Entities added time: " + timer.stop("entities") + "ms");
	}
}
