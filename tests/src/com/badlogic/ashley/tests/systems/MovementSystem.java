package com.badlogic.ashley.tests.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;

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
