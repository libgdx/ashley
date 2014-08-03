/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.core;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;


/**
 * A {@link ComponentType} is used to uniquely identify a Component sub-class by assigning them an index. This is used
 * for various creating bit masks for fast comparison. See {@link Family} and {@link Entity}.
 * 
 * You cannot instantiate a {@link ComponentType}. They can only be accessed via {@link #getIndexFor(Class<? extends Component>)}. Each
 * component class will always return the same instance of {@link ComponentType}.
 * 
 * @author Stefan Bachmann
 */
final class ComponentType {
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
    public static ComponentType getFor(Class<? extends Component> componentType) {
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
    	return getFor(componentType).getIndex();
    }
    
    /**
	 * @param componentTypes list of component types
	 * @return Bits representing the collection of components for quick comparison and matching. See {@link Family#getFor(Bits, Bits, Bits)}.
	 */
	public static Bits getBitsFor(Class<? extends Component> ...componentTypes) {
		Bits bits = new Bits();

        int typesLength = componentTypes.length;
        for(int i = 0; i < typesLength; i++){
            bits.set(ComponentType.getIndexFor(componentTypes[i]));
        }
        
        return bits;
	}
    
    @Override
	public int hashCode() {
		return index;
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
