package ash.core;

import java.lang.reflect.Field;
import java.util.Map;

import ash.tools.Print;

/**
 * The base class for a node.
 * 
 * <p>A node is a set of different components that are required by a system.
 * A system can request a collection of nodes from the engine. Subsequently the Engine object creates
 * a node for every entity that has all of the components in the node class and adds these nodes
 * to the list obtained by the system. The engine keeps the list up to date as entities are added
 * to and removed from the engine and as the components on entities change.</p>
 * 
 * @author Erik Borgers
 */

public class Node extends ListElement {
	/**
	 * The entity that owns this Node (there can be only one. Components can be shared).
	 */
	private Entity entity;

	// public Component[] components;

	/**
	 * 
	 *
	 *
	 Erik: 
	 
	 In Ash there seems to be an array (although not decared) for all component classes in the node
	 <code>
	 for ( componentClass in components )
				{
					node[components[componentClass]] = entity.get( componentClass ); // a bit mysterious!!!
				}
	 </code>
				
	 in this implementation, we store the (pointers to) components in the Node attributes by reflection
	 */

	public Node() {
		super();
		entity = null;
	}

	/**
	 * makes all Component fields point to the Components of the Entity
	 * also remembers the Entity to which this node refers
	 */
	void setEntity(Entity entity) {
		if (this.entity != null)
			Print.fatal("Node already used by Entity " + entity.getName());
		this.entity = entity;

		// now make all component fields point to the corresponding components in the Entity
		Field[] nodeFields = this.getClass().getDeclaredFields();
		for (int i = 0; i < nodeFields.length; i++) {

			Class<?> fieldClass = nodeFields[i].getType(); // fields[i].getDeclaringClass();

			Component c = entity.get(fieldClass);
			try {
				nodeFields[i].set(this, c);
			} catch (IllegalArgumentException e) {
				Print.fatal(e, "illegal argument for node field " + nodeFields[i].getName());
			} catch (IllegalAccessException e) {
				Print.fatal(e, "illegal access to node field " + nodeFields[i].getName());
			}

		}
	}

	public Entity getEntity() {
		return entity;
	}

	/**
	 * called by the Pool so that the Node can be reused
	 */
	@Override
	public void releaseResources() {
		entity = null;
	}

	/** 
	 * Retrieves all components in this Node. The array is a copy
	 *  
	 * @return
	 */
	public Component[] getAll() {
		return entity.getAll();
	}

	/**
	 * Erik:
	 * Get all components in the Node. Returns the internal Map
	 * 
	 * @return The internal Map containing all the components that are part of the Node.
	 */
	public Map<Class<? extends Component>, Component> getComponents() {
		return entity.getComponents();
	}
}
