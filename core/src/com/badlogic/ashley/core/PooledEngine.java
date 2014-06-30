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

import com.badlogic.ashley.utils.Pool;
import com.badlogic.ashley.utils.Pools;
import com.badlogic.ashley.utils.Pool.Poolable;

/**
 * Engine derived class adding Entity and Component pooling. This improves performance in environments
 * where creating/deleting entities is frequent as it greatly reduces memory allocation.
 * 
 * - Create entities using {@link #createEntity()}
 * - Create components using {@link #createComponent(Class)}
 * - Components should implement the {@link Poolable} interface when in need to reset its state upon removal
 * 
 * @author David Saltares
 */
public class PooledEngine extends Engine {
	
	private EntityPool entityPool;
	private Pools componentPools;
	
	public PooledEngine() {
		super();
		
		entityPool = new EntityPool();
		componentPools = new Pools();
	}
	
	/**
	 * Retrieves a clean entity from the Engine pool. In order to add it to the world, use
	 * {@link #addEntity(Entity)}.
	 * 
	 * @return clean entity from pool.
	 */
	public Entity createEntity() {
		return entityPool.obtain();
	}
	
	/**
	 * Remove an entity from this Engine
	 * @param entity The Entity to remove
	 */
	@Override
	public void removeEntity(Entity entity){
		super.removeEntity(entity);
		
		if (PooledEntity.class.isAssignableFrom(entity.getClass())) {
			PooledEntity pooledEntity = PooledEntity.class.cast(entity);
			entityPool.free(pooledEntity);
		}
	}
	
	/**
	 * Retrieves a new component from the Engine pool. It will be placed back in the
	 * pool whenever it's removed from an entity or the entity itself it's removed.
	 * 
	 * @param componentType type of the component to create
	 * @return obtains an available pooled component of the required type
	 */
	public <T extends Component> T createComponent(Class<T> componentType) {
		return componentPools.obtain(componentType);
	}
	
	private class PooledEntity extends Entity implements Poolable {
		
		@Override
		public Component remove(Class<? extends Component> componentType){
			Component component = super.remove(componentType);
			componentPools.free(component);
			return component;
		}

		@Override
		public void reset() {
			removeAll();
			flags = 0;
		}
	}
	
	private class EntityPool extends Pool<PooledEntity> {

		@Override
		protected PooledEntity newObject() {
			return new PooledEntity();
		}
	}
}
