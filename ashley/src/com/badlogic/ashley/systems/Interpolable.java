package com.badlogic.ashley.systems;

public interface Interpolable {
	public void interpolate(float delta, float physicsStep, float inAccumulator);
}