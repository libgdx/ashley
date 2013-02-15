package ashley.core;

import java.util.BitSet;

import ashley.signals.Signal;
import ashley.utils.ObjectMap;

/**
 * Entities are simple containers. They can hold components that give them "data". The component's data
 * is then in return process by systems.
 * 
 * An entity can only hold one instance of a component type. So you can't add two PositionComponents to the
 * same entity. Sorry.
 * 
 * @author Stefan Bachmann
 */
public class Entity {
	/** The hashmap that holds all the components hashed via their class type */
	private ObjectMap<Class<? extends Component>, Component> components;
	/** A bitset describing all the components in this entity. For quick matching. */
	private BitSet componentBits;
	/** A bitset describing all the systems this entity was matched with. */
	private BitSet familyBits;
	
	/** Will dispatch an event when a component is added. */
	public Signal<Entity> componentAdded;
	/** Will dispatch an event when a component is removed. */
	public Signal<Entity> componentRemoved;
	
	/**
	 * Creates an empty Entity.
	 */
	public Entity(){
		components = new ObjectMap<Class<? extends Component>, Component>();
		componentBits = new BitSet();
		familyBits = new BitSet();
		
		componentAdded = new Signal<>();
		componentRemoved = new Signal<>();
	}
	
	/**
	 * Add a component to this Entity. If a component of the same type already exists, it'll be replaced.
	 * @param component The component to add
	 * @return The entity for easy chaining
	 */
	public Entity add(Component component){
		components.put(component.getClass(), component);
		
		componentBits.set(ComponentType.getIndexFor(component.getClass()));
		
		componentAdded.dispatch(this);
		return this;
	}
	
	/**
	 * Removes the component of the specified type. Since there is only ever one component of one type, we
	 * don't need an instance reference.
	 * @param componentType The Component to remove
	 * @return The removed component, or null if the Entity did no contain such a component
	 */
	public Component remove(Class<? extends Component> componentType){
		Component removeComponent = components.get(componentType, null);
		
		if(removeComponent != null){
			componentBits.clear(ComponentType.getIndexFor(componentType));
			
			componentRemoved.dispatch(this);
		}
		
		return removeComponent;
	}
	
	/**
	 * Quick and dirty component retrieval
	 * @param componentType The Component class to retrieve
	 * @return The Component
	 */
	public <T extends Component> T getComponent(Class<T> componentType){
		return componentType.cast(components.get(componentType));
	}
	
	/**
	 * Returns this Entity's component bits, describing all the components it contains
	 */
	public BitSet getComponentBits(){
		return componentBits;
	}
	
	/**
	 * Returns this Entity's family bits, describing all the systems it currently is being processed with
	 */
	public BitSet getFamilyBits(){
		return familyBits;
	}
}
