package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableIntMap;
import com.badlogic.ashley.utils.ImmutableIntMap.Keys;

/**
 * A simple EntitySystem that iterates over each entity and calls processEntity() for each entity every time
 * the EntitySystem is updated. This is really just a convenience class as most systems iterate over a list
 * of entities.
 * 
 * @author Stefan Bachmann
 */
public abstract class IteratingSystem extends EntitySystem {
	/** The family describing this systems entities */
	private Family family;
	/** The entities used by this system */
	private ImmutableIntMap<Entity> entities;
	
	/**
	 * Instantiates a system that will iterate over the entities described by the Family.
	 * @param family The family of entities iterated over in this System
	 */
	public IteratingSystem(Family family){
		this(family, 0);
	}
	
	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a 
	 * specific priority.
	 * @param family The family of entities iterated over in this System
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public IteratingSystem(Family family, int priority){
		super(priority);
		
		this.family = family;
	}
		
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(family);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		entities = null;
	}

	@Override
	public void update(float deltaTime) {
		Keys keys = entities.immutableKeys();
		
		while(keys.hasNext()){
			processEntity(entities.get(keys.next()), deltaTime);
		}
	}

	/**
	 * This method is called on every entity on every update call of the EntitySystem. Override this to implement
	 * your system's specific processing.
	 * @param entity The current Entity being processed
	 * @param deltaTime The delta time between the last and current frame
	 */
	public abstract void processEntity(Entity entity, float deltaTime);
}
