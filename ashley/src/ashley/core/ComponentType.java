package ashley.core;

import ashley.utils.ObjectMap;

/**
 * A ComponentType is used to uniquely identify a Component sub-class by assigning them an index. This is used
 * for various creating bit masks for fast comparison. See {@link Family} and {@link Entity}.
 * 
 * You cannot instantiate a ComponentType. They can only be accessed via ComponentType.getIndexFor(ComponentClass). Each
 * component class will always return the same instance of ComponentType.
 * 
 * @author Stefan Bachmann
 */
public class ComponentType {
	/** Hashmap to keep track of all Component subclasses hashed by their Class */
	private static ObjectMap<Class<? extends Component>, ComponentType> componentTypes = new ObjectMap<Class<? extends Component>, ComponentType>();
	private static int typeIndex = 0;
	
	/** This ComponentType's unique index */
	private final int index;
	
	private ComponentType(){
		index = typeIndex++;
	}
	
	/**
	 * Returns this ComponentType's unique index
	 */
	public int getIndex(){
		return index;
	}
	
    /**
     * Returns the ComponentType instance for the specified Component Class.
     * @param componentType The Component class
     * @return A ComponentType matching the Component Class
     */
    public static ComponentType getTypeFor(Class<? extends Component> componentType) {
        ComponentType type = componentTypes.get(componentType);

        if (type == null) {
        	type = new ComponentType();
	    	componentTypes.put(componentType, type);
        }

        return type;
    }

    /**
     * Returns the unique ComponentType index for a specific Component Class. Basically a quikc helper
     * method. The same could be done via getTypeFor().
     * @param componentType The Component class
     * @return The index for the specified Component Class
     */
    public static int getIndexFor(Class<? extends Component> componentType) {
    	return getTypeFor(componentType).getIndex();
    }
}
