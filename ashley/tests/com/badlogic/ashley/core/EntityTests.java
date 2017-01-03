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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;

public class EntityTests {

	private static class ComponentA implements Component {
	}

	private static class ComponentB implements Component {
	}

	private static class EntityListenerMock implements Listener<Entity> {

		public int counter = 0;

		@Override
		public void receive (Signal<Entity> signal, Entity object) {
			++counter;

			assertNotNull(signal);
			assertNotNull(object);
		}
	}

	private ComponentMapper<ComponentA> am = ComponentMapper.getFor(ComponentA.class);
	private ComponentMapper<ComponentB> bm = ComponentMapper.getFor(ComponentB.class);

	@Test
	public void addAndReturnComponent(){
		Entity entity = new Entity();
		ComponentA componentA = new ComponentA();
		ComponentB componentB = new ComponentB();

		assertEquals(componentA, entity.addAndReturn(componentA));
		assertEquals(componentB, entity.addAndReturn(componentB));

		assertEquals(2, entity.getComponents().size());
	}

	@Test
	public void noComponents () {
		Entity entity = new Entity();

		assertEquals(0, entity.getComponents().size());
		assertTrue(entity.getComponentBits().isEmpty());
		assertNull(am.get(entity));
		assertNull(bm.get(entity));
		assertFalse(am.has(entity));
		assertFalse(bm.has(entity));
	}

	@Test
	public void addAndRemoveComponent () {
		Entity entity = new Entity();

		entity.add(new ComponentA());

		assertEquals(1, entity.getComponents().size());

		Bits componentBits = entity.getComponentBits();
		int componentAIndex = ComponentType.getIndexFor(ComponentA.class);

		for (int i = 0; i < componentBits.length(); ++i) {
			assertEquals(i == componentAIndex, componentBits.get(i));
		}

		assertNotNull(am.get(entity));
		assertNull(bm.get(entity));
		assertTrue(am.has(entity));
		assertFalse(bm.has(entity));

		entity.remove(ComponentA.class);

		assertEquals(0, entity.getComponents().size());

		for (int i = 0; i < componentBits.length(); ++i) {
			assertFalse(componentBits.get(i));
		}

		assertNull(am.get(entity));
		assertNull(bm.get(entity));
		assertFalse(am.has(entity));
		assertFalse(bm.has(entity));
	}
	
	@Test
	public void removeUnexistingComponent () throws Exception {
		// ensure remove unexisting component work with
		// new component type at default bag limits (64)
		Entity entity = new Entity();
		
		ComponentClassFactory cl = new ComponentClassFactory();
		
		for(int i=0 ; i<65 ; i++){
			Class<? extends Component> type = cl.createComponentType("Component" + i);
			entity.remove(type);
			entity.add(type.newInstance());
		}
	}	

	@Test
	public void addAndRemoveAllComponents () {
		Entity entity = new Entity();

		entity.add(new ComponentA());
		entity.add(new ComponentB());

		assertEquals(2, entity.getComponents().size());

		Bits componentBits = entity.getComponentBits();
		int componentAIndex = ComponentType.getIndexFor(ComponentA.class);
		int componentBIndex = ComponentType.getIndexFor(ComponentB.class);

		for (int i = 0; i < componentBits.length(); ++i) {
			assertEquals(i == componentAIndex || i == componentBIndex, componentBits.get(i));
		}

		assertNotNull(am.get(entity));
		assertNotNull(bm.get(entity));
		assertTrue(am.has(entity));
		assertTrue(bm.has(entity));

		entity.removeAll();

		assertEquals(0, entity.getComponents().size());

		for (int i = 0; i < componentBits.length(); ++i) {
			assertFalse(componentBits.get(i));
		}

		assertNull(am.get(entity));
		assertNull(bm.get(entity));
		assertFalse(am.has(entity));
		assertFalse(bm.has(entity));
	}

	@Test
	public void addSameComponent () {
		Entity entity = new Entity();

		ComponentA a1 = new ComponentA();
		ComponentA a2 = new ComponentA();

		entity.add(a1);
		entity.add(a2);

		assertEquals(1, entity.getComponents().size());
		assertTrue(am.has(entity));
		assertNotEquals(a1, am.get(entity));
		assertEquals(a2, am.get(entity));
	}

	@Test
	public void componentListener () {
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

	@Test
	public void getComponentByClass () {
		ComponentA compA = new ComponentA();
		ComponentB compB = new ComponentB();

		Entity entity = new Entity();
		entity.add(compA).add(compB);

		ComponentA retA = entity.getComponent(ComponentA.class);
		ComponentB retB = entity.getComponent(ComponentB.class);

		assertNotNull(retA);
		assertNotNull(retB);

		assertTrue(retA == compA);
		assertTrue(retB == compB);
	}
}
