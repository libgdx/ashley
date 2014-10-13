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

import com.badlogic.ashley.core.Engine.ComponentOperationHandler;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.Bag;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;

/**
 * Simple containers of {@link Component}s that give them "data". The component's data
 * is then processed by {@link EntitySystem}s.
 * 
 * @author Stefan Bachmann
 */
public class Entity {
	private static long nextId;
	
	/** A flag that can be used to bit mask this entity. Up to the user to manage. */
	public int flags;
	/** Will dispatch an event when a component is added. */
	public final Signal<Entity> componentAdded;
	/** Will dispatch an event when a component is removed. */
	public final Signal<Entity> componentRemoved;
	
	/** Unique entity id */
	protected Long uuid;
	/** A collection that holds all the components indexed by their {@link ComponentType} index */
	private Bag<Component> components;
	/** An auxiliary array for user access to all the components of an entity */
	private Array<Component> componentsArray;
	/** A wrapper around componentsArray so users cannot modify it */
	private ImmutableArray<Component> immutableComponentsArray;
	/** A Bits describing all the components in this entity. For quick matching. */
	private Bits componentBits;
	/** A Bits describing all the systems this entity was matched with. */
	private Bits familyBits;
	
	ComponentOperationHandler componentOperationHandler;
	
	/**
	 * Creates an empty Entity.
	 */
	public Entity(){
		components = new Bag<Component>();
		componentsArray = new Array<Component>(false, 16);
		immutableComponentsArray = new ImmutableArray<Component>(componentsArray);
		componentBits = new Bits();
		familyBits = new Bits();
		flags = 0;
		
		uuid = new Long(nextId++);
		
		componentAdded = new Signal<Entity>();
		componentRemoved = new Signal<Entity>();
	}
	
	/**
	 * @return The Entity's unique id.
	 */
	public long getId(){
		return uuid;
	}
	
	/**
	 * Adds a {@link Component} to this Entity. If a {@link Component} of the same type already exists, it'll be replaced.
	 * @return The Entity for easy chaining
	 */
	public Entity add(Component component){
		if (componentOperationHandler != null) {
			componentOperationHandler.add(this, component);
		}
		else {
			addInternal(component);
		}
		return this;
	}
	
	/**
	 * Removes the {@link Component} of the specified type. Since there is only ever one component of one type, we
	 * don't need an instance reference.
	 * @return The removed {@link Component}, or null if the Entity did no contain such a component.
	 */
	public Component remove(Class<? extends Component> componentClass){
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		Component removeComponent = components.get(componentTypeIndex);
		
		if (componentOperationHandler != null) {
			componentOperationHandler.remove(this, componentClass);
		}
		else {
			removeInternal(componentClass);
		}
		
		return removeComponent;
	}
	
	/**
	 * Removes all the {@link Component}'s from the Entity.
	 */
	public void removeAll() {
		while(componentsArray.size > 0) {
			removeInternal(componentsArray.get(0).getClass());
		}
	}
	
	/**
	 * @return immutable collection with all the Entity {@link Component}s.
	 */
	public ImmutableArray<Component> getComponents() {
		return immutableComponentsArray;
	}
	
	/**
	 * Retrieve a component from this {@link Entity} by class.
	 * 
	 * <em>Note:</em> the preferred way of retrieving {@link Component}s is using {@link ComponentMapper}s. This method
	 * is provided for convenience; using a ComponentMapper provides O(1) access to components while this method
	 * provides only O(logn).
	 * 
	 * @param componentClass the class of the component to be retrieved.
	 * @return the instance of the specified {@link Component} attached to this {@link Entity}, or null if no such {@link Component} exists.
	 */
	public <T extends Component> T getComponent(Class<T> componentClass){
		return getComponent(ComponentType.getFor(componentClass));
	}
	
	/**
	 * Internal use.
	 * 
	 * @return The {@link Component} object for the specified class, null if the Entity does not have any components for that class.
	 */
	@SuppressWarnings("unchecked")
	<T extends Component> T getComponent(ComponentType componentType) {
		int componentTypeIndex = componentType.getIndex();
		
		if (componentTypeIndex < components.getCapacity()) {
			return (T)components.get(componentType.getIndex());
		}
		else {
			return null;
		}
	}

	/**
	 * Internal use.
	 * 
	 * @return Whether or not the Entity has a {@link Component} for the specified class.
	 */
	boolean hasComponent(ComponentType componentType) {
		return componentBits.get(componentType.getIndex());
	}
	
	/**
	 * Internal use.
	 * 
	 * @return This Entity's component bits, describing all the {@link Component}s it contains.
	 */
	Bits getComponentBits(){
		return componentBits;
	}
	
	/**
	 * @return This Entity's {@link Family} bits, describing all the {@link EntitySystem}s it currently is being processed by.
	 */
	Bits getFamilyBits(){
		return familyBits;
	}
	
	Entity addInternal(Component component){
		Class<? extends Component> componentClass = component.getClass();
		
		for (int i = 0; i < componentsArray.size; ++i) {
			if (componentsArray.get(i).getClass() == componentClass) {
				componentsArray.removeIndex(i);
				break;
			}
		}
		
		int componentTypeIndex = ComponentType.getIndexFor(component.getClass()); 
		
		components.set(componentTypeIndex, component);
		componentsArray.add(component);
		
		componentBits.set(componentTypeIndex);
		
		componentAdded.dispatch(this);
		return this;
	}

	Component removeInternal(Class<? extends Component> componentClass){
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		Component removeComponent = components.get(componentTypeIndex);
		
		if(removeComponent != null){
			components.set(componentTypeIndex, null);
			componentsArray.removeValue(removeComponent, true);
			componentBits.clear(componentTypeIndex);
			
			componentRemoved.dispatch(this);
		}
		
		return removeComponent;
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
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
        return uuid.equals(other.uuid);
    }
}
