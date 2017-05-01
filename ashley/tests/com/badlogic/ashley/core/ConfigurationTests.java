package com.badlogic.ashley.core;

import static org.junit.Assert.*;
import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

public class ConfigurationTests {

	@Test
	public void addAndRemoveConfigurations() {
		//Setup
		Engine engine = new Engine();
		ConfigurationImpl conf = new ConfigurationImpl();
		//Test add
		engine.addConfiguration(ConfigurationImpl.class, conf);
		//Check it's been added
		assertTrue(engine.getConfiguration(ConfigurationImpl.class) == conf);
		//Check OnAdded has been called
		assertTrue(conf.addCount==1);
		//Test removal by key
		engine.removeConfiguration(ConfigurationImpl.class);
		//Check it's been removed
		assertTrue(engine.getConfiguration(ConfigurationImpl.class) == null);
		//Check OnRemoved has been called
		assertTrue(conf.removeCount==1);
		//Setup
		engine.addConfiguration(ConfigurationImpl.class, conf);
		//Test removal by value
		engine.removeConfiguration(conf);
		//Check it's been removed
		assertTrue(engine.getConfiguration(ConfigurationImpl.class) == null);
		//Check OnRemoved has been called
		assertTrue(conf.removeCount==2);
	}

	@Test
	public void clearConfigurations() {
		//Setup
		Engine engine = new Engine();
		ConfigurationImpl conf = new ConfigurationImpl();
		engine.addConfiguration(ConfigurationImpl.class, conf);
		//Test clear
		engine.clearConfigurations();
		//Check it's been removed
		assertTrue(engine.getConfiguration(ConfigurationImpl.class) == null);
		//Check OnRemoved has been called
		assertTrue(conf.removeCount==1);
	}

	@Test
	public void testListeners() {
		//Setup
		Engine engine = new Engine();
		ConfigurationImpl conf = new ConfigurationImpl();
		ConfigurationListenerImpl addListener=new ConfigurationListenerImpl();
		ConfigurationListenerImpl removeListener=new ConfigurationListenerImpl();
		engine.configurationAdded.add(addListener);
		engine.configurationRemoved.add(removeListener);
		//Add/Remove configuration
		engine.addConfiguration(ConfigurationImpl.class, conf);
		engine.removeConfiguration(ConfigurationImpl.class);
		//Check the listeners have been called
		assertTrue(addListener.callCount==1);
		assertTrue(removeListener.callCount==1);
	}
	
	static class ConfigurationListenerImpl implements Listener<Object>{
		int callCount=0;
		@Override
		public void receive(Signal<Object> signal, Object object) {
			callCount++;
			
		}}

	static class ConfigurationImpl implements Configuration {

		int addCount = 0;
		int removeCount = 0;

		@Override
		public boolean onAdd(Engine engine) {
			addCount++;
			return false;
		}

		@Override
		public boolean onRemove(Engine engine) {
			removeCount++;
			return false;
		}

	}
}
