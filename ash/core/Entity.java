package ash.core;

import java.util.HashMap;
import java.util.Map;

/**
 * An entity is composed from components. As such, it is essentially a collection object for components. Sometimes, the
 * entities in a game will mirror the actual characters and objects in the game, but this is not necessary.
 * 
 * <p>
 * Components are simple value objects that contain data relevant to the entity. Entities with similar functionality
 * will have instances of the same components. So we might have a position component
 * </p>
 * 
 * <p>
 * <code>public class PositionComponent
 * {
 *   public var x : Number;
 *   public var y : Number;
 * }</code>
 * </p>
 * 
 * <p>
 * All entities that have a position in the game world, will have an instance of the position component. Systems operate
 * on entities based on the components they have.
 * </p>
 * 
 * @author Erik Borgers
 */
public class Entity extends ListElement {

	/**
	 * Give the entity a name. This can help with debugging and with serialising the entity.
	 */
	private String name;
	private static int nameCount = 0;

	/**
	 * This signal is dispatched when a component is added to the entity.
	 */
	// public var componentAdded : Signal2; no callbacks implemented
	/**
	 * This signal is dispatched when a component is removed from the entity. 
	 */
	// public var componentRemoved : Signal2; no callbacks implemented
	/**
	 * Dispatched when the name of the entity changes. Used internally by the engine to track entities based on their names.
	 */
	// 		internal var nameChanged : Signal2; no callbacks implemented

	private final Map<Class<? extends Component>, Component> components; // contains the Component(s). The key is the class of the component

	public Entity() {
		this("_entity" + (++nameCount));
	}

	public Entity(String name) {
		super();
		this.name = name;
		components = new HashMap<Class<? extends Component>, Component>(); // better beformance then a HasTable when non synchronised
	}

	@Override
	public void releaseResources() {
		this.name = null;
		components.clear();
	}

	public String getName() {
		return name;
	}

	/**
	 * Add a component to the entity.
	 * 
	 * @param component The component object to add.
	 * @param componentClass The class of the component. This is only necessary if the component
	 * extends another component class and you want the framework to treat the component as of 
	 * the base class type. If not set, the class type is determined directly from the component.
	 * 
	 * @return A reference to the entity. This enables the chaining of calls to add, to make
	 * creating and configuring entities cleaner. e.g.
	 * 
	 * <code> Entity entity = new Entity()
	 *     .add( new Position( 100, 200 )
	 *     .add( new Display( new PlayerClip() );</code>
	 *
	 * Note that an Entity can have only one instance of a certain Component Class!
	 * 
	 * Note: this does NOT create new Nodes in Systems as a side effect. 
	 * You must call engine.componentAddedToEntity() for this
	 * 
	 * </code>
	 * 
	 * @param component
	 * @return
	 */
	public Entity add(Component component) {
		return add(component.getClass(), component);
	}

	/**
	 * Faster way to add a component to the Entity. 
	 * 
	 * NOTE: This call does NOT create Nodes. This must be done with a seperate call to engine.componentAddedToEntity
	 * 
	 * @param klass
	 * @param component
	 * @return
	 */
	public Entity add(Class<? extends Component> klass, Component component) {
		if (has(klass)) {
			remove(klass);
		}
		// component.setEntity(this);
		components.put(klass, component);
		// We do NOT create new Nodes for the Families/Systems. 
		// This is left to the programmer by calling engine.componentAddedToEntity
		// The reason is that most of the time, Components are added only at construction time like: 
		// e = new MyEntity().add( new Position(0,0) ).add( new X() );
		// engine.add( a );
		// 
		return this;
	}

	/**
	 * remove a Component from an entity
	 * 
	 * note: this does NOT remove Nodes in Systems as a side effect!
	 * 
	 * You must call engine.componentRemovedFromEntity() for this
	 * 
	 * @param componentClass The class of the component to be removed.
	 * @return the component, or null if the component doesn't exist in the entity
	 */
	public Component remove(Class<? extends Component> klass) {
		if (has(klass)) {
			Component component = components.remove(klass);
			// component.setEntity(null);
			return component;
		}
		return null;
	}

	/**
	 * return true if the Entity has this Component
	 * 
	 * @param klass
	 * @return
	 */
	public boolean has(Class<? extends Component> klass) {
		return components.containsKey(klass);
	}

	/**
	 * Return the Component given the class of the Component
	 * 
	 * @param componentClass The class of the component requested.
	 * @return The component, or null if none was found.
	 */
	public Component get(Class<?> componentClass) {

		return components.get(componentClass);
	}

	/**
	 * Get all components from the entity. Returns an created array (copy)
	 * 
	 * @return An array containing all the components that are part of the entity.
	 */
	public Component[] getAll() {
		return components.values().toArray(new Component[0]);
	}

	/**
	 * Erik: Get all components from the entity. Returns the internal Map
	 * 
	 * @return The internal Map containing all the components that are part of the entity.
	 */
	public Map<Class<? extends Component>, Component> getComponents() {
		return components;
	}

}
