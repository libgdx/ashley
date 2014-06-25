package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.Array;

public class EntityTests {

	private static class ComponentA extends Component {}
	
	private static class ComponentB extends Component {}
	
	private static class EntityListenerMock implements Listener<Entity> {

		public int counter = 0;
		
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			++counter;
			
			assertNotNull(signal);
			assertNotNull(object);
		}
	}
	
	@Test
	public void uniqueIndex() {
		int numEntities = 10000;
		Array<Entity> entities = new Array<Entity>();
		Set<Integer> ids = new HashSet<Integer>();
		
		for (int i = 0; i < numEntities; ++i) {
			Entity entity = new Entity();
			assertFalse(ids.contains(entity.getIndex()));
			ids.add(entity.getIndex());
			entities.add(entity);
		}
	}
	
	@Test
	public void noComponents() {
		Entity entity = new Entity();
		
		assertEquals(0, entity.getComponents().getSize());
		assertTrue(entity.getComponentBits().isEmpty());
		assertNull(entity.getComponent(ComponentA.class));
		assertNull(entity.getComponent(ComponentB.class));
		assertFalse(entity.hasComponent(ComponentA.class));
		assertFalse(entity.hasComponent(ComponentB.class));
	}
	
	@Test
	public void addAndRemoveComponent() {
		Entity entity = new Entity();
		
		entity.add(new ComponentA());
		
		assertEquals(1, entity.getComponents().getSize());
		
		BitSet componentBits = entity.getComponentBits();
		int componentAIndex = ComponentType.getIndexFor(ComponentA.class);
		
		for (int i = 0; i < componentBits.size(); ++i) {
			assertEquals(i == componentAIndex, componentBits.get(i));
		}
		
		assertNotNull(entity.getComponent(ComponentA.class));
		assertNull(entity.getComponent(ComponentB.class));
		assertTrue(entity.hasComponent(ComponentA.class));
		assertFalse(entity.hasComponent(ComponentB.class));
		
		entity.remove(ComponentA.class);
		
		assertEquals(0, entity.getComponents().getSize());
		
		for (int i = 0; i < componentBits.size(); ++i) {
			assertFalse(componentBits.get(i));
		}
		
		assertNull(entity.getComponent(ComponentA.class));
		assertNull(entity.getComponent(ComponentB.class));
		assertFalse(entity.hasComponent(ComponentA.class));
		assertFalse(entity.hasComponent(ComponentB.class));
	}
	
	@Test
	public void addAndRemoveAllComponents() {
		Entity entity = new Entity();
		
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertEquals(2, entity.getComponents().getSize());
		
		BitSet componentBits = entity.getComponentBits();
		int componentAIndex = ComponentType.getIndexFor(ComponentA.class);
		int componentBIndex = ComponentType.getIndexFor(ComponentB.class);
		
		for (int i = 0; i < componentBits.size(); ++i) {
			assertEquals(i == componentAIndex || i == componentBIndex, componentBits.get(i));
		}
		
		assertNotNull(entity.getComponent(ComponentA.class));
		assertNotNull(entity.getComponent(ComponentB.class));
		assertTrue(entity.hasComponent(ComponentA.class));
		assertTrue(entity.hasComponent(ComponentB.class));
		
		entity.removeAll();
		
		assertEquals(0, entity.getComponents().getSize());
		
		for (int i = 0; i < componentBits.size(); ++i) {
			assertFalse(componentBits.get(i));
		}
		
		assertNull(entity.getComponent(ComponentA.class));
		assertNull(entity.getComponent(ComponentB.class));
		assertFalse(entity.hasComponent(ComponentA.class));
		assertFalse(entity.hasComponent(ComponentB.class));
	}
	
	@Test
	public void addSameComponent() {
		Entity entity = new Entity();
		
		ComponentA a1 = new ComponentA();
		ComponentA a2 = new ComponentA();
		
		entity.add(a1);
		entity.add(a2);
		
		assertEquals(1, entity.getComponents().getSize());
		assertTrue(entity.hasComponent(ComponentA.class));
		assertNotEquals(a1, entity.getComponent(ComponentA.class));
		assertEquals(a2, entity.getComponent(ComponentA.class));
	}
	
	@Test
	public void componentListener() {
		EntityListenerMock addedListener = new EntityListenerMock();
		EntityListenerMock removedListener = new EntityListenerMock();
		
		Entity entity = new Entity();
		entity.componentAdded.add(addedListener);
		entity.componentRemoved.add(removedListener);
		
		assertEquals(0, addedListener.counter);
		assertEquals(0, removedListener.counter);
			
		entity.add(new ComponentA());
		
		assertEquals(1, addedListener.counter);
		assertEquals(0, removedListener.counter);
		
		entity.remove(ComponentA.class);
		
		assertEquals(1, addedListener.counter);
		assertEquals(1, removedListener.counter);
		
		entity.add(new ComponentB());
		
		assertEquals(2, addedListener.counter);
		
		entity.remove(ComponentB.class);
		
		assertEquals(2, removedListener.counter);
	}
}
