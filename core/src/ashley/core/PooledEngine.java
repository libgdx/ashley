package ashley.core;

import ashley.utils.Pool;
import ashley.utils.Pools;
import ashley.utils.Pool.Poolable;

/**
 * Engine derived class adding Entity and Component pooling. This improves performance in environments
 * where creating/deleting entities is frequent as it greatly reduces memory allocation.
 * 
 * - Create entities using {@link #createEntity()}
 * - Create components using {@link #createComponent(Class)}
 * - Components should implement the Poolable interface when in need to reset its state upon removal
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
