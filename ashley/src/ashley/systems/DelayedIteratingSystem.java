package ashley.systems;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.utils.IntMap;
import ashley.utils.IntMap.Keys;

/**
 * A delayed system that only updates once the delay has been reached.
 *
 * @author Matthew A. Johnston (warmwaffles)
 */
public abstract class DelayedIteratingSystem extends EntitySystem {
	/** The length of the delay */
	private float delay;
	/** The time accumulated */
	private float accumulator;
	/** The family describing this systems entities */
	private Family family;
	/** The entities used by this system */
	private IntMap<Entity> entities;

	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a
	 * specific delay.
	 * @param family
	 * @param delay
	 */
	public DelayedIteratingSystem(Family family, float delay) {
		this(family, delay, 0);
	}

	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a 
	 * specific delay and priority.
	 * @param family The family of entities iterated over in this System
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public DelayedIteratingSystem(Family family, float delay, int priority){
		super(priority);

		this.family = family;
		this.delay = delay;
		this.accumulator = 0.0f;
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
		if(accumulator < delay) {
			accumulator += deltaTime;
			return;
		}

		Keys keys = entities.keys();

		beforeUpdate(deltaTime);
		while(keys.hasNext){
			processEntity(entities.get(keys.next()), deltaTime);
		}
		afterUpdate(deltaTime);

		accumulator = 0.0f;
	}

	/**
	 * This method is called on every entity on every update call of the EntitySystem. Override this to implement
	 * your system's specific processeing.
	 * @param entity The current Entity being processed
	 * @param deltaTime The delta time between the last and current update
	 */
	public abstract void processEntity(Entity entity, float deltaTime);

	/**
	 * Called before the entities are processed
	 * @param deltaTime The delta time between the last and current update
	 */
	public void beforeUpdate(float deltaTime) {
	}
	
	/**
	 * Called after the entities are processed
	 * @param deltaTime The delta time between the last and current update
	 */
	public void afterUpdate(float deltaTime) {
	}
}
