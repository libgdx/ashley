package com.badlogic.ashley.core;

/**
 * EntitySystem is a base class for processing entities and exposes a few basic methods
 * required.
 *
 * @author Stefan Bachmann
 */
public abstract class EntitySystem {
	/** Use this to set the priority of the system. Lower means it'll get executed first. */
	public int priority;

	/**
	 * Default constructor that will initialise an EntitySystem with priority 0.
	 */
	public EntitySystem(){
		this(0);
	}

	/**
	 * Initialises the EntitySystem with the priority specified
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public EntitySystem(int priority){
		this.priority = priority;
	}

	/**
	 * Called when this EntitySystem is added to an Engine
	 * @param engine The Engine this system was added to
	 */
	public void addedToEngine(Engine engine) {}

	/**
	 * Called when this EntitySystem is removed from an Engine
	 * @param engine The Engine the system was removed from
	 */
	public void removedFromEngine(Engine engine) {}

	/**
	 * The update method called every tick
	 * @param deltaTime The time passed since last frame in Seconds
	 */
	public void update(float deltaTime) {}

    /**
     *
     * @return true if the system should be processed, false if not.
     */
    public boolean checkProcessing() {
        return true;
    }
}
