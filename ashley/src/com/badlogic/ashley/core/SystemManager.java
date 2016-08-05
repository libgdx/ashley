package com.badlogic.ashley.core;

import com.badlogic.ashley.systems.InterpolatingSystem;
import com.badlogic.ashley.systems.PhysicsSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Comparator;

class SystemManager {
	private SystemComparator systemComparator = new SystemComparator();
	private Array<EntitySystem> systems = new Array<EntitySystem>(false, 16);
	private Array<PhysicsSystem> physicsSystems = new Array<PhysicsSystem>(false, 16);
	private Array<InterpolatingSystem> interpolatingSystems = new Array<InterpolatingSystem>(false, 16);
	private ImmutableArray<EntitySystem> immutableSystems = new ImmutableArray<EntitySystem>(systems);
	private ImmutableArray<PhysicsSystem> immutablePhysicsSystems = new ImmutableArray<PhysicsSystem>(physicsSystems);
	private ImmutableArray<InterpolatingSystem> immutableInterpolatingSystems = new ImmutableArray<InterpolatingSystem>(interpolatingSystems);
	private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
	private SystemListener listener;
	
	public SystemManager(SystemListener listener) {
		this.listener = listener;
	}
	
	public void addSystem(EntitySystem system){
		Class<? extends EntitySystem> systemType = system.getClass();		
		EntitySystem oldSytem = getSystem(systemType);
		
		if (oldSytem != null) {
			removeSystem(oldSytem);
		}
		
		systems.add(system);
		systemsByClass.put(systemType, system);		
		systems.sort(systemComparator);
		listener.systemAdded(system);

		if (system instanceof PhysicsSystem) {
			physicsSystems.add((PhysicsSystem) system);
		}
		if (system instanceof InterpolatingSystem) {
			interpolatingSystems.add((InterpolatingSystem) system);
		}
	}
	
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			listener.systemRemoved(system);
		}
		if (system instanceof PhysicsSystem) {
			physicsSystems.removeValue((PhysicsSystem) system, true);
		}
		if (system instanceof InterpolatingSystem) {
			interpolatingSystems.removeValue((InterpolatingSystem) system, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return (T) systemsByClass.get(systemType);
	}
	
	public ImmutableArray<EntitySystem> getSystems() {
		return immutableSystems;
	}

	public ImmutableArray<PhysicsSystem> getPhysicsSystems() {
		return immutablePhysicsSystems;
	}

	public ImmutableArray<InterpolatingSystem> getInterpolatingSystems() {
		return immutableInterpolatingSystems;
	}
	
	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
	
	interface SystemListener {
		void systemAdded(EntitySystem system);
		void systemRemoved(EntitySystem system);
	}
}
