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

/**
 * Provides super fast {@link Component} retrieval from {@Link Entity} objects.
 * @param <T> the class type of the {@link Component}.
 * @author David Saltares
 */
public final class ComponentMapper<T extends Component> {
	private final ComponentType componentType;

	/**
	 * @param componentClass Component class to be retrieved by the mapper.
	 * @return New instance that provides fast access to the {@link Component} of the specified class.
	 */
	public static <T extends Component> ComponentMapper<T> getFor (Class<T> componentClass) {
		return new ComponentMapper<T>(componentClass);
	}

	/** @return The {@link Component} of the specified class belonging to entity. */
	public T get (Entity entity) {
		return entity.getComponent(componentType);
	}

	/** @return Whether or not entity has the component of the specified class. */
	public boolean has (Entity entity) {
		return entity.hasComponent(componentType);
	}

	private ComponentMapper (Class<T> componentClass) {
		componentType = ComponentType.getFor(componentClass);
	}
}
