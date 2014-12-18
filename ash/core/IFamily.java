package ash.core;

import java.util.Collection;

/**
 *
 * The interface for classes that are used to manage NodeLists (set as the familyClass property 
 * in the Engine object). Most developers don't need to use this since the default implementation
 * is used by default and suits most needs.
 * 
 * @author Erik Borgers
 */

interface IFamily {

	/**
	 * returns the type of Node (the Class) managed by this class
	 * 
	 * @return
	 */
	public Class<? extends Node> getNodeClass();

	/**
	 * Returns the NodeList managed by this class. This should be a reference that remains valid always since it is
	 * retained and reused by Systems that use the list. i.e. never recreate the list, always modify it in place.
	 */
	public NodeList getNodeList();

	/**
	 * An entity has been added to the game. Test its components for inclusion of a new Node in the families NodeList.
	 * Returns added Node or null if none. 
	 */
	public Node newEntity(Entity entity);

	/**
	 * An entity has been removed from the game. If it's in this family's NodeList it Node should be removed. Returns
	 * removed Node or null
	 */
	public Node removeEntity(Entity entity);

	/**
	 * A component has been added to an entity. Test whether the entity's inclusion in this family's NodeList should be
	 * modified. Returns added Node (or null if not added to the family)
	 */
	public Node componentAddedToEntity(Entity entity, Class<? extends Component> klass);

	/**
	 * A component has been removed from an entity. Test whether the entity's inclusion in this family's NodeList should
	 * be modified. Returns possibly removed Node (or null if inclusion not changed)
	 */
	public Node componentRemovedFromEntity(Entity entity, Class<? extends Component> klass);

	/**
	 * The family is about to be discarded. Clean up all properties as necessary. Usually, you will want to empty the
	 * NodeList at this time.
	 */
	public void cleanUp();

	/**
	 * Keeps track of the Systems that use this family for updating
	 * 
	 * @param system
	 */
	public void registerSystem(EntitySystem system);

	/**
	 * Keeps track of the Systems that use this family for updating
	 * 
	 * @param system
	 */
	public void unregisterSystem(EntitySystem system);

	/**
	 * return the Systems that are watching this family
	 * 
	 * @param system
	 * @return
	 */
	public Collection<EntitySystem> isBeingWatched();

	/**
	 * Erik: signal the family that the update cycle of the engine is about to end (to do some cleanup)
	 */
	public void updated();

	/** Erik: added to dynamically create IFamily classes in Engine with non-default IFamily
	 *  
	 * @param nodeClass
	 */
	void setNodeClass(Class<? extends Node> nodeClass);

	/** Erik: added to dynamically create IFamily classes in Engine with non-default IFamily
	 *  
	 * @param engine
	 */
	void setEngine(Engine engine);
}
