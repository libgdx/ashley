package com.badlogic.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;

public class ProfiledSystem<T extends EntitySystem> extends EntitySystem {
	private final EntitySystem system;
	private long lastUpdateTime = 0L;
	
	public ProfiledSystem(T system) {
		this.system = system;
	}
	
	public void update(float deltaTime) {
		long start = System.currentTimeMillis();
		system.update(deltaTime);
		long end = System.currentTimeMillis();
		lastUpdateTime = end - start;
	}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
}
