package com.badlogic.ashley.systems;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class ProfiledSystemTest {
	
	private class SystemA extends EntitySystem {
		public void update(float deltaTime) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	@Test
	public void profiledSystemTest () {
		Engine engine = new Engine();
		ProfiledSystem<SystemA> system = new ProfiledSystem<SystemA>(new SystemA());
		
		engine.addSystem(system);
		engine.update(0.0f);
		assertTrue(system.getLastUpdateTime() > 0L);
	}

}
