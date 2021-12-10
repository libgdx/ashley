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

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Supports {@link Entity} and {@link Component} pooling. This improves performance in environments where creating/deleting
 * entities is frequent as it greatly reduces memory allocation.
 * <ul>
 * <li>Create entities using {@link #createEntity()}</li>
 * <li>Create components using {@link #createComponent(Class)}</li>
 * <li>Components should implement the {@link Poolable} interface when in need to reset its state upon removal</li>
 * </ul>
 * @author David Saltares
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PooledEngine extends Engine {

	private EntityPool entityPool;
	private ComponentPools componentPools;

	/**
	 * Creates a new PooledEngine with a maximum of 100 entities and 100 components of each type. Use
	 * {@link #PooledEngine(int, int, int, int)} to configure the entity and component pools.
	 */
	public PooledEngine () {
		this(10, 100, 10, 100);
	}

	/**
	 * Creates new PooledEngine with the specified pools size configurations.
	 * @param entityPoolInitialSize initial number of pre-allocated entities.
	 * @param entityPoolMaxSize maximum number of pooled entities.
	 * @param componentPoolInitialSize initial size for each component type pool.
	 * @param componentPoolMaxSize maximum size for each component type pool.
	 */
	public PooledEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
		super();

		entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize);
		componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
	}

	/** @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link Engine}, use {@link #addEntity(Entity)}. @{@link Override {@link Engine#createEntity()}} */
	@Override
	public Entity createEntity () {
		return entityPool.obtain();
	}

	/**
	 * Retrieves a new {@link Component} from the {@link Engine} pool. It will be placed back in the pool whenever it's removed
	 * from an {@link Entity} or the {@link Entity} itself it's removed.
	 * Overrides the default implementation of Engine (creating a new Object)
	 */
	@Override
	public <T extends Component> T createComponent (Class<T> componentType) {
		return componentPools.obtain(componentType);
	}

	/**
	 * Removes all free entities and components from their pools. Although this will likely result in garbage collection, it will
	 * free up memory.
	 */
	public void clearPools () {
		entityPool.clear();
		componentPools.clear();
	}

	@Override
	protected void removeEntityInternal (Entity entity) {
		super.removeEntityInternal(entity);

		if (entity instanceof PooledEntity) {
			entityPool.free((PooledEntity)entity);
		}
	}

	private class PooledEntity extends Entity implements Poolable {
		@Override
		Component removeInternal(Class<? extends Component> componentClass) {
			Component removed = super.removeInternal(componentClass);
			if (removed != null) {
				componentPools.free(removed);
			}

			return removed;
		}

		@Override
		public void reset () {
			removeAll();
			flags = 0;
			componentAdded.removeAllListeners();
			componentRemoved.removeAllListeners();
			scheduledForRemoval = false;
			removing = false;
		}
	}

	private class EntityPool extends Pool<PooledEntity> {

		public EntityPool (int initialSize, int maxSize) {
			super(initialSize, maxSize);
		}

		@Override
		protected PooledEntity newObject () {
			return new PooledEntity();
		}

		/**
		 * Forwarding this call ensures {@link Poolable} {@link Component} instances are returned to their respective
		 * {@link ComponentPools}s even if the {@link EntityPool} is full.
		 */
		@Override
		protected void discard(PooledEntity pooledEntity) {
			pooledEntity.reset();
		}
	}

	private class ComponentPools {
		private ObjectMap<Class<?>, ComponentPool> pools;
		private int initialSize;
		private int maxSize;

		public ComponentPools (int initialSize, int maxSize) {
			this.pools = new ObjectMap<Class<?>, ComponentPool>();
			this.initialSize = initialSize;
			this.maxSize = maxSize;
		}

		public <T extends Component> T obtain (Class<T> type) {
			ComponentPool pool = pools.get(type);

			if (pool == null) {
				pool = new ComponentPool(type, initialSize, maxSize);
				pools.put(type, pool);
			}

			return (T)pool.obtain();
		}

		public void free (Object object) {
			if (object == null) {
				throw new IllegalArgumentException("object cannot be null.");
			}

			ComponentPool pool = pools.get(object.getClass());

			if (pool == null) {
				return; // Ignore freeing an object that was never retained.
			}

			pool.free(object);
		}

		public void freeAll (Array objects) {
			if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

			for (int i = 0, n = objects.size; i < n; i++) {
				Object object = objects.get(i);
				if (object == null) continue;
				free(object);
			}
		}

		public void clear () {
			for (Pool pool : pools.values()) {
				pool.clear();
			}
		}
	}

	private static class ComponentPool<T extends Component> extends ReflectionPool<T> {

		public ComponentPool(Class<T> type, int initialCapacity, int max) {
			super(type, initialCapacity, max);
		}

		/**
		 * Forwarding this call ensures {@link Poolable} {@link Component} instances have their reset method called,
		 * even if the {@link ComponentPool} is full.
		 */
		@Override
		protected void discard(T component) {
			if (component instanceof Poolable) {
				((Poolable) component).reset();
			}
		}
	}

}
