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

import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;

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
	private static int nextIndex;
	
	/** Unique entity index for fast retrieval */
	private int index;
	
	/** The hashmap that holds all the components hashed via their class type */
	private ObjectMap<Class<? extends Component>, Component> components;
	/** An auxiliary array for quick access to all the components of an entity */
	private Array<Component> componentsArray;
	/** A wrapper around componentsArray so users cannot modify it */
	private ImmutableArray<Component> immutableComponentsArray;
	/** A Bits describing all the components in this entity. For quick matching. */
	private Bits componentBits;
	/** A Bits describing all the systems this entity was matched with. */
	private Bits familyBits;
	/** A flag that can be used to bit mask this entity. Up to the user to manage. */
	public int flags;
	
	/** Will dispatch an event when a component is added. */
	public Signal<Entity> componentAdded;
	/** Will dispatch an event when a component is removed. */
	public Signal<Entity> componentRemoved;
	
	/**
	 * Creates an empty Entity.
	 */
	public Entity(){
		components = new ObjectMap<Class<? extends Component>, Component>();
		componentsArray = new Array<Component>();
		immutableComponentsArray = new ImmutableArray<Component>(componentsArray);
		componentBits = new Bits();
		familyBits = new Bits();
		flags = 0;
		
		index = nextIndex++;
		
		componentAdded = new Signal<Entity>();
		componentRemoved = new Signal<Entity>();
	}
	
	/**
	 * Add a component to this Entity. If a component of the same type already exists, it'll be replaced.
	 * @param component The component to add
	 * @return The entity for easy chaining
	 */
	public Entity add(Component component){
		Class<? extends Component> componentClass = component.getClass();
		
		for (int i = 0; i < componentsArray.size; ++i) {
			if (componentsArray.get(i).getClass() == componentClass) {
				componentsArray.removeIndex(i);
				break;
			}
		}
		
		components.put(component.getClass(), component);
		componentsArray.add(component);
		
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
			components.remove(componentType);
			
			componentsArray.removeValue(removeComponent, true);
			
			componentBits.clear(ComponentType.getIndexFor(componentType));
			
			componentRemoved.dispatch(this);
			
			components.remove(componentType);
		}
		
		return removeComponent;
	}
	
	/**
	 * Removes all the entity components
	 */
	public void removeAll() {
		Keys<Class<? extends Component>> keys = components.keys();
		
		while (keys.hasNext()) {
			remove(keys.next());
			keys = components.keys();
		}
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
	 * Quick way of checking whether an entity has a component or not
	 * @param componentType The Component class to check
	 * @return True if the entity has a Component of that class, False if it doesn't 
	 */
	public boolean hasComponent(Class<? extends Component> componentType) {
		return componentBits.get(ComponentType.getIndexFor(componentType));
	}
	
	/**
	 * @return this Entity's component bits, describing all the components it contains
	 */
	public Bits getComponentBits(){
		return componentBits;
	}
	
	/**
	 * @return immutable array with all the entity components
	 */
	public ImmutableArray<Component> getComponents() {
		return immutableComponentsArray;
	}
	
	/**
	 * @return this Entity's family bits, describing all the systems it currently is being processed with
	 */
	public Bits getFamilyBits(){
		return familyBits;
	}
	
	/**
	 * @return this entity's unique index
	 */
	public int getIndex(){
		return index;
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
		if (!(obj instanceof Entity))
			return false;
		Entity other = (Entity) obj;
        return index == other.index;
    }
}
