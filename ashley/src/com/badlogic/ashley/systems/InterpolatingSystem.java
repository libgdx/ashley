package com.badlogic.ashley.systems;

public interface InterpolatingSystem {

	void interpolate(float delta, float physicsStep, float inAccumulator);
}
