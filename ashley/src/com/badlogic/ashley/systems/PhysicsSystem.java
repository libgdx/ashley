package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

/**
 * An {@link EntitySystem} that is meant for operations with a fixed timestep, like physics.
 * See http://gafferongames.com/game-physics/fix-your-timestep/ for explanation.
 */
public interface PhysicsSystem {

	/**
	 * Will be called when {@link Engine#updatePhysics(float)} is called.
	 * @param physicsStep the physics step to use in the simulation, not the delta time, should be constant
	 */
	void updatePhysics(float physicsStep);
}
