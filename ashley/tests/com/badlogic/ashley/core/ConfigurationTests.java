package com.badlogic.ashley.core;

import static org.junit.Assert.*;
import org.junit.Test;

public class ConfigurationTests {

	@Test
	public void addAndRemoveConfigurations(){
		Engine engine=new Engine();
		ConfigurationImpl conf=new ConfigurationImpl();
		engine.addConfiguration(ConfigurationImpl.class,conf);
		assertTrue(conf.added);
		assertTrue(engine.getConfiguration(ConfigurationImpl.class)==conf);
		engine.removeConfiguration(ConfigurationImpl.class);
		assertTrue(conf.removed);
		assertTrue(engine.getConfiguration(ConfigurationImpl.class)==null);
	}
	
	@Test
	public void clearConfigurations(){
			Engine engine=new Engine();
			ConfigurationImpl conf=new ConfigurationImpl();
			engine.addConfiguration(ConfigurationImpl.class,conf);
			assertTrue(conf.added);
			engine.clearConfigurations();
			assertTrue(conf.removed);
			assertTrue(engine.getConfiguration(ConfigurationImpl.class)==null);
	}

	static class ConfigurationImpl implements Configuration {

		boolean added = false;
		boolean removed = false;

		@Override
		public boolean onAdd(Engine engine) {
			added = true;
			return false;
		}

		@Override
		public boolean onRemove(Engine engine) {
			removed = true;
			return false;
		}

	}
}
