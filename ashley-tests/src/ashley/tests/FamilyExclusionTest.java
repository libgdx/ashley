package ashley.tests;

import java.io.IOException;

import ashley.core.Component;
import ashley.core.Entity;
import ashley.core.Family;
import ashley.core.PooledEngine;
import ashley.tests.components.CameraComponent;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;
import ashley.tests.components.VisualComponent;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FamilyExclusionTest {
	public static void main(String[] args) throws IOException{
		
		Entity e1 = new Entity();
		e1.add(new MovementComponent(2, 2));
		e1.add(new PositionComponent(1, 1));
		e1.add(new CameraComponent());
		e1.add(new NameComponent("e1"));
		
		Entity e2 = new Entity();
		e2.add(new MovementComponent(2, 2));
		e2.add(new VisualComponent(new TextureRegion()));
		e2.add(new CameraComponent());
		e2.add(new NameComponent("e2"));
		
		Entity e3 = new Entity();
		e3.add(new PositionComponent(2, 2));
		e3.add(new NameComponent("e3"));
		
		PooledEngine engine = new PooledEngine();
		
		engine.addEntity(e1);
		engine.addEntity(e2);
		engine.addEntity(e3);
		
		Family f1 = Family.getFamilyForOne(CameraComponent.class, VisualComponent.class);
		log("Entitites with either Camera or Visual Component ( or both): ");
		
		for ( Entity e : engine.getEntitiesFor(f1).values()){
			log(e.getComponent(NameComponent.class).name);
		}
		
		Family f2 = Family.getFamilyForAll(NameComponent.class, PositionComponent.class);
		log("Entitites with both Name and Position: ");
		
		for ( Entity e : engine.getEntitiesFor(f2).values()){
			log(e.getComponent(NameComponent.class).name);
		}
		
		Family f3 = Family.getFamilyForOne(NameComponent.class, MovementComponent.class).exclude(VisualComponent.class);;
		log("Entitites with Name or Movement, but without Visual Component ");
		
		for ( Entity e : engine.getEntitiesFor(f3).values()){
			log(e.getComponent(NameComponent.class).name);
		}
		
		System.in.read();
	}
	
	
	
	public static void log(String string){
		System.out.println(string);
	}
	
	public static class NameComponent extends Component{
		public String name;
		public NameComponent(String name){
			this.name = name;
		}
	}
}
