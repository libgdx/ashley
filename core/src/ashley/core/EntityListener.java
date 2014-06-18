package ashley.core;

public interface EntityListener {
	/**
	 * Called whenever an entity is added to Engine
	 * 
	 * @param entity
	 */
	public void entityAdded(Entity entity);
	
	/**
	 * Called whenever an entity is removed from Engine
	 * 
	 * @param entity
	 */
	public void entityRemoved(Entity entity);
}
