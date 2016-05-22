package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;

public class FixedTimeStepSystem<T extends EntitySystem & Interpolable> {
	private final T system;
	private final float timeStep;
	private float accumulator = 0.0f;
	
	public FixedTimeStepSystem(T system, float timeStep) {
		this.system = system;
		this.timeStep = timeStep;
	}
	
	public void update(float deltaTime) {
		accumulator += deltaTime;
		while (accumulator > timeStep) {
			system.update(timeStep);
			accumulator -= timeStep;
		}
		
		system.interpolate(deltaTime, timeStep, accumulator);
	}
}
