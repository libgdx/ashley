package com.badlogic.ashley.core;


import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

class EntityManager {
	private EntityListener listener;
	private Array<Entity> entities = new Array<Entity>(false, 16);
	private ObjectSet<Entity> entitySet = new ObjectSet<Entity>();
	private ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
	private Array<EntityOperation> pendingOperations = new Array<EntityOperation>(false, 16);
	private EntityOperationPool entityOperationPool = new EntityOperationPool();
	
	public EntityManager(EntityListener listener) {
		this.listener = listener;
	}
	
	public void addEntity(Entity entity){
		addEntity(entity, false);
	}
	
	public void addEntity(Entity entity, boolean delayed){
		if (delayed) {
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Add;
			pendingOperations.add(operation);
		}
		else {
			addEntityInternal(entity);
		}
	}
	
	public void removeEntity(Entity entity){
		removeEntity(entity, false);
	}
	
	public void removeEntity(Entity entity, boolean delayed){
		if (delayed) {
			if(entity.scheduledForRemoval) {
				return;
			}
			entity.scheduledForRemoval = true;
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Remove;
			pendingOperations.add(operation);
		}
		else {
			removeEntityInternal(entity);
		}
	}
	
	public void removeAllEntities() {
		removeAllEntities(false);
	}
	
	public void removeAllEntities(boolean delayed) {
		if (delayed) {
			for(Entity entity: entities) {
				entity.scheduledForRemoval = true;
			}
			EntityOperation operation = entityOperationPool.obtain();
			operation.type = EntityOperation.Type.RemoveAll;
			pendingOperations.add(operation);
		}
		else {
			while(entities.size > 0) {
				removeEntity(entities.first(), false);
			}
		}
	}
	
	public ImmutableArray<Entity> getEntities() {
		return immutableEntities;
	}
	
	public void processPendingOperations() {
		for (int i = 0; i < pendingOperations.size; ++i) {
			EntityOperation operation = pendingOperations.get(i); 

			switch(operation.type) {
				case Add: addEntityInternal(operation.entity); break;
				case Remove: removeEntityInternal(operation.entity); break;
				case RemoveAll:
					while(entities.size > 0) {
						removeEntityInternal(entities.first());
					}
					break;
				default:
					throw new AssertionError("Unexpected EntityOperation type");
			}

			entityOperationPool.free(operation);
		}
		
		pendingOperations.clear();
	}
	
	protected void removeEntityInternal(Entity entity) {
		entity.scheduledForRemoval = false;
		entity.removing = true;
		entities.removeValue(entity, true);
		entitySet.remove(entity);
		
		listener.entityRemoved(entity);
		entity.removing = false;
	}

	protected void addEntityInternal(Entity entity) {
		if (entitySet.contains(entity)) {
			throw new IllegalArgumentException("Entity is already registered " + entity);
		}

		entities.add(entity);
		entitySet.add(entity);

		listener.entityAdded(entity);
	}
	
	private static class EntityOperation implements Pool.Poolable {
		public enum Type {
			Add,
			Remove,
			RemoveAll
		}

		public Type type;
		public Entity entity;

		@Override
		public void reset() {
			entity = null;
		}
	}

	private static class EntityOperationPool extends Pool<EntityOperation> {
		@Override
		protected EntityOperation newObject() {
			return new EntityOperation();
		}
	}
}
