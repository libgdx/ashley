package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

/**
 * An {@link EntitySystem} that is meant to reduce temporal aliasing when using a fixed timestep for physics
 * See http://gafferongames.com/game-physics/fix-your-timestep/ for explanation.
 */
public interface InterpolatingSystem {

	/**
	 * Will be called when {@link Engine#interpolate(float, float, float)} is called.
	 * @param delta	current delta time, sometimes necessary for some rendering
	 * @param physicsStep the physics step used in the simulation, should be constant
	 * @param inAccumulator what is left in the accumulator after performing possible physics step, should be < physicsStep
	 */
	void interpolate(float delta, float physicsStep, float inAccumulator);
}
