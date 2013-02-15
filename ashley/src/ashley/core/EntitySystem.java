package ashley.core;

/**
 * EntitySystem is a base class for processing entities and exposes a few basic methods
 * required.
 * 
 * @author Stefan Bachmann
 */
public class EntitySystem {
	
	/**
	 * Called when this EntitySystem is added to an Engine
	 * @param engine The Engine this system was added to
	 */
	public void addedToEngine(Engine engine){
		
	}
	
	/**
	 * Called when this EntitySystem is removed from an Engine
	 * @param engine The Engine the system was removed from
	 */
	public void removedFromEngine(Engine engine){
		
	}

	/**
	 * The update method called every tick
	 * @param deltaTime The time passed since last frame in Seconds
	 */
	public void update(float deltaTime){
		
	}
}
