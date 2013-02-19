package ashley.tests;

import ashley.core.Engine;
import ashley.core.EntitySystem;

public class SystemPriorityTest {
	public static void main(String[] args){
		Engine engine = new Engine();
		
		engine.addSystem(new SystemA(10));
		engine.addSystem(new SystemB(5));
		engine.addSystem(new SystemA(2));
		
		engine.update(0);
	}
	
	public static class SystemA extends EntitySystem {
		public SystemA(int priority) {
			super(priority);
		}

		@Override
		public void update(float deltaTime) {
			System.out.println("SystemA");
		}
		
	}
	
	public static class SystemB extends EntitySystem {
		public SystemB(int priority) {
			super(priority);
		}
		
		@Override
		public void update(float deltaTime) {
			System.out.println("SystemB");
		}
	}
}
