package com.badlogic.ashley.core;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import java.util.Comparator;

class SystemManager {
    private SystemComparator systemComparator = new SystemComparator();
    private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    private ImmutableArray<EntitySystem> immutableSystems = new ImmutableArray<EntitySystem>(systems);
    private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
    private SystemListener listener;

    private Array<SystemOperation> pendingOperations = new Array<>();
    private SystemManager.SystemOperationPool systemOperationPool = new SystemManager.SystemOperationPool();

    public SystemManager(SystemListener listener) {
        this.listener = listener;
    }

    public void addSystem(EntitySystem system, boolean delayed) {
        if (delayed) {
            SystemOperation operation = systemOperationPool.obtain();
            operation.type = SystemOperation.Type.Add;
            operation.system = system;
            pendingOperations.add(operation);
        } else {
            Class<? extends EntitySystem> systemType = system.getClass();
            EntitySystem oldSystem = getSystem(systemType);

            if (oldSystem != null) {
                removeSystem(oldSystem, delayed);
            }

            systems.add(system);
            systemsByClass.put(systemType, system);
            systems.sort(systemComparator);
            listener.systemAdded(system);
        }
    }

    public void removeSystem(EntitySystem system, boolean delayed) {
        if (delayed) {
            SystemOperation operation = systemOperationPool.obtain();
            operation.type = SystemOperation.Type.Remove;
            operation.system = system;
        } else {
            if (systems.removeValue(system, true)) {
                systemsByClass.remove(system.getClass());
                listener.systemRemoved(system);
            }
        }
    }

    public void removeAllSystems(boolean delayed) {
        while (systems.size > 0) {
            removeSystem(systems.first(), delayed);
        }
    }

    public void processPendingOperations() {
        for (int i = 0; i < pendingOperations.size; ++i) {
            SystemOperation operation = pendingOperations.get(i);
            switch(operation.type) {
                case Add:
                    addSystem(operation.system, false);
                    break;
                case Remove:
                    removeSystem(operation.system, false);
                    break;
                default:
                    throw new AssertionError("Unexpected EntityOperation type");
            }
            systemOperationPool.free(operation);
        }
        pendingOperations.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    public ImmutableArray<EntitySystem> getSystems() {
        return immutableSystems;
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }

    interface SystemListener {
        void systemAdded(EntitySystem system);

        void systemRemoved(EntitySystem system);
    }


    private static class SystemOperation implements Pool.Poolable {
        public enum Type {
            Add, Remove;
        }

        public Type type;
        public EntitySystem system;

        @Override
        public void reset() {
            system = null;
        }
    }

    private static class SystemOperationPool extends Pool<SystemManager.SystemOperation> {
        @Override
        protected SystemManager.SystemOperation newObject() {
            return new SystemManager.SystemOperation();
        }
    }
}
