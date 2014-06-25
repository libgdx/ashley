package com.badlogic.ashley.core;

import com.badlogic.ashley.utils.ObjectMap;

/**
 * A {@link ComponentType} is used to uniquely identify a Component sub-class by assigning them an index. This is used
 * for various creating bit masks for fast comparison. See {@link Family} and {@link Entity}.
 * 
 * You cannot instantiate a {@link ComponentType}. They can only be accessed via {@link #getIndexFor(Class<? extends Component>)}. Each
 * component class will always return the same instance of {@link ComponentType}.
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
     * Returns the unique ComponentType index for a specific Component Class. Basically a quick helper
     * method. The same could be done via getTypeFor().
     * @param componentType The Component class
     * @return The index for the specified Component Class
     */
    public static int getIndexFor(Class<? extends Component> componentType) {
    	return getTypeFor(componentType).getIndex();
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentType other = (ComponentType) obj;
        return index == other.index;
    }
}
