package ash.core;

import ash.tools.Print;

/**
 * The base class for a System.
 * 
 * <p>
 * A System is part of the core functionality of the game. After a system is added to the engine, its update method will
 * be called on every frame of the engine. When the system is removed from the engine, the update method is no longer
 * called.
 * </p>
 * 
 * <p>
 * The aggregate of all Systems in the engine is the functionality of the game, with the update methods of those systems
 * collectively constituting the engine update loop. Systems generally operate on node lists - collections of nodes.
 * Each node contains the components from an entity in the engine that match the node.
 * </p>
 * 
 * @author Erik Borgers
 */

public abstract class EntitySystem extends ListElement {

	/**
	 * Used internally to hold the priority of this system within the system list. This is used to order the systems so
	 * they are updated in the correct order.
	 */
	private Class<? extends Node> nodeClass = null; // the Class of Node(s) iterated by this System, if any

	/**
	 * create a system. System with a high priority is called before a lower one 
	 * the setting of a priority is done when adding the system to the engine
	 * 
	 * @param klass NodeClass. Erik: null means "no class"
	 * @param priority // NO, done when adding the System
	 */

	public EntitySystem(Class<? extends Node> klass) {
		super();
		setNodeClass(klass); // null means no Node Class to watch
	}

	/**
	 * alternative constructor without setting the NodeClass to watch at constructor time
	 */
	public EntitySystem() {
		super();
	}

	@Override
	public void releaseResources() {
		nodeClass = null;
	}

	/**
	 * sets the Node Class handled by this EntitySystem
	 * 
	 * @return
	 */
	public final void setNodeClass(Class<? extends Node> nodeClass) {
		if (this.nodeClass != null)
			Print.fatal("Node Class already set for EntitySystem");
		this.nodeClass = nodeClass;
	}

	/**
	 * 
	 * Called by engine just after the System is added to the engine, but before any calls to the update method. 
	 * 
	 * Override this method to add your own functionality, eg initializing fonts used by the system, attaching internal Node lists
	 * etc.
	 * 
	 * Note: Do not call directly but add a System using the Engine object
	 * 
	 * @param engine
	 *            The engine the system was added to.
	 */
	public void addToEngine(Engine engine) {

	}

	/**
	 * 
	 * Called by engine just after the System is removed from the engine, after all calls to the update method. 
	 * 
	 * Override this method to add your own functionality, eg cleaning up fonts used by the system.
	 *
	 * Note: Do not call directly but remove a System using the Engine object
	 * 
	 * @param engine
	 *            The engine the system was removed from.
	 */
	public void removeFromEngine(Engine engine) {
		//this.engine = null;
	}

	/**
	 * 
	 * After the System is added to the engine, this method is called every frame until the system is removed from the
	 * engine. Override this method to add your own functionality.
	 * 
	 * <p>
	 * If you need to perform an action outside of the update loop (e.g. you need to change the systems in the engine
	 * and you don't want to do it while they're updating) add a listener to the engine's updateComplete signal to be
	 * notified when the update loop completes.
	 * </p>
	 * 
	 * @param time          The duration, in seconds, of the frame.
	 */
	public abstract void update(float time);

	/**
	 * returns the Node Class handled by this EntitySystem
	 * 
	 * @return
	 */
	public final Class<? extends Node> getNodeClass() {
		return nodeClass;
	}

	/**
	 * called when the Node is created and added to the System. Note that a Node can be shared between Systems, so this call can be done multiple times
	 * 
	 * @param newNode
	 */
	public void nodeAdded(Node newNode) {
	}

	/**
	 * called when the Node is removed from the System. Note that it can be removed from multiple Systems, so this call can be done multiple times!
	 * 
	 * @param removedNode
	 */
	public void nodeRemoved(Node removedNode) {
	}

	/*
	 * @Override(non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 
	public int compareTo(EntitySystem compare) {

		// ascending order
		return this.priority - compare.priority;

		// descending order
		// return compareQuantity - this.quantity;

	}
	*/
}
