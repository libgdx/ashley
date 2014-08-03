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

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;


/**
 * Supports {@link Entity} and {@link Component} pooling. This improves performance in environments
 * where creating/deleting entities is frequent as it greatly reduces memory allocation.
 * 
 * <ul>
 * <li>Create entities using {@link #createEntity()}</li>
 * <li>Create components using {@link #createComponent(Class)}</li>
 * <li>Components should implement the {@link Poolable} interface when in need to reset its state upon removal</li>
 * </ul>
 * 
 * @author David Saltares
 */
public class PooledEngine extends Engine {
	
	private EntityPool entityPool;
	
	public PooledEngine() {
		super();
		
		entityPool = new EntityPool();
	}
	
	/**
	 * @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link Engine}, use {@link #addEntity(Entity)}.
	 */
	public Entity createEntity() {
		return entityPool.obtain();
	}
	
	/**
	 * Removes an {@link Entity} from this {@link Engine}
	 */
	@Override
	public void removeEntity(Entity entity){
		super.removeEntity(entity);
		
		if (ClassReflection.isAssignableFrom(PooledEntity.class, entity.getClass())) {
			PooledEntity pooledEntity = (PooledEntity) entity;
			entityPool.free(pooledEntity);
		}
	}
	
	/**
	 * Retrieves a new {@link Component} from the {@link Engine} pool. It will be placed back in the
	 * pool whenever it's removed from an {@link Entity} or the {@link Entity} itself it's removed.
	 */
	public <T extends Component> T createComponent(Class<T> componentType) {
		return Pools.obtain(componentType);
	}
	
	private class PooledEntity extends Entity implements Poolable {
		
		@Override
		public Component remove(Class<? extends Component> componentType){
			Component component = super.remove(componentType);
			Pools.free(component);
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
