package ashley.tests.systems;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;

public class MovementSystem extends IteratingSystem {
	PositionComponent position;
	MovementComponent movement;
	
	public MovementSystem() {
		super(Family.getFamilyFor(PositionComponent.class, MovementComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		position = entity.getComponent(PositionComponent.class);
		movement = entity.getComponent(MovementComponent.class);
		
		position.x += movement.velocityX * deltaTime;
		position.y += movement.velocityY * deltaTime;
	}
}
