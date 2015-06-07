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

import com.badlogic.ashley.core.Family.Builder;
import org.junit.Test;

import com.badlogic.ashley.systems.IteratingSystem;

@SuppressWarnings("unchecked")
public class FamilyTests {

	private static class ComponentA implements Component {
	}

	private static class ComponentB implements Component {
	}

	private static class ComponentC implements Component {
	}

	private static class ComponentD implements Component {
	}

	private static class ComponentE implements Component {
	}

	private static class ComponentF implements Component {
	}

	static class TestSystemA extends IteratingSystem {

		public TestSystemA (String name) {
			super(Family.all(ComponentA.class).get());
		}

		@Override
		public void processEntity (Entity e, float d) {
		}
	}

	static class TestSystemB extends IteratingSystem {

		public TestSystemB (String name) {
			super(Family.all(ComponentB.class).get());
		}

		@Override
		public void processEntity (Entity e, float d) {
		}
	}

	@Test
	public void validFamily () {
		assertNotNull(Family.all().get());
		assertNotNull(Family.all(ComponentA.class).get());
		assertNotNull(Family.all(ComponentB.class).get());
		assertNotNull(Family.all(ComponentC.class).get());
		assertNotNull(Family.all(ComponentA.class, ComponentB.class).get());
		assertNotNull(Family.all(ComponentA.class, ComponentC.class).get());
		assertNotNull(Family.all(ComponentB.class, ComponentA.class).get());
		assertNotNull(Family.all(ComponentB.class, ComponentC.class).get());
		assertNotNull(Family.all(ComponentC.class, ComponentA.class).get());
		assertNotNull(Family.all(ComponentC.class, ComponentB.class).get());
		assertNotNull(Family.all(ComponentA.class, ComponentB.class, ComponentC.class).get());
		assertNotNull(Family.all(ComponentA.class, ComponentB.class).get().one(ComponentC.class, ComponentD.class)
			.exclude(ComponentE.class, ComponentF.class).get());
	}

	@Test
	public void sameFamily () {
		Family family1 = Family.all(ComponentA.class).get();
		Family family2 = Family.all(ComponentA.class).get();
		Family family3 = Family.all(ComponentA.class, ComponentB.class).get();
		Family family4 = Family.all(ComponentA.class, ComponentB.class).get();
		Family family5 = Family.all(ComponentA.class, ComponentB.class, ComponentC.class).get();
		Family family6 = Family.all(ComponentA.class, ComponentB.class, ComponentC.class).get();
		Family family7 = Family.all(ComponentA.class, ComponentB.class).one(ComponentC.class, ComponentD.class)
			.exclude(ComponentE.class, ComponentF.class).get();
		Family family8 = Family.all(ComponentA.class, ComponentB.class).one(ComponentC.class, ComponentD.class)
			.exclude(ComponentE.class, ComponentF.class).get();
		Family family9 = Family.all().get();
		Family family10 = Family.all().get();

		assertTrue(family1.equals(family2));
		assertTrue(family2.equals(family1));
		assertTrue(family3.equals(family4));
		assertTrue(family4.equals(family3));
		assertTrue(family5.equals(family6));
		assertTrue(family6.equals(family5));
		assertTrue(family7.equals(family8));
		assertTrue(family8.equals(family7));
		assertTrue(family9.equals(family10));

		assertEquals(family1.getIndex(), family2.getIndex());
		assertEquals(family3.getIndex(), family4.getIndex());
		assertEquals(family5.getIndex(), family6.getIndex());
		assertEquals(family7.getIndex(), family8.getIndex());
		assertEquals(family9.getIndex(), family10.getIndex());
	}

	@Test
	public void differentFamily () {
		Family family1 = Family.all(ComponentA.class).get();
		Family family2 = Family.all(ComponentB.class).get();
		Family family3 = Family.all(ComponentC.class).get();
		Family family4 = Family.all(ComponentA.class, ComponentB.class).get();
		Family family5 = Family.all(ComponentA.class, ComponentC.class).get();
		Family family6 = Family.all(ComponentB.class, ComponentA.class).get();
		Family family7 = Family.all(ComponentB.class, ComponentC.class).get();
		Family family8 = Family.all(ComponentC.class, ComponentA.class).get();
		Family family9 = Family.all(ComponentC.class, ComponentB.class).get();
		Family family10 = Family.all(ComponentA.class, ComponentB.class, ComponentC.class).get();
		Family family11 = Family.all(ComponentA.class, ComponentB.class).one(ComponentC.class, ComponentD.class)
			.exclude(ComponentE.class, ComponentF.class).get();
		Family family12 = Family.all(ComponentC.class, ComponentD.class).one(ComponentE.class, ComponentF.class)
			.exclude(ComponentA.class, ComponentB.class).get();
		Family family13 = Family.all().get();

		assertFalse(family1.equals(family2));
		assertFalse(family1.equals(family3));
		assertFalse(family1.equals(family4));
		assertFalse(family1.equals(family5));
		assertFalse(family1.equals(family6));
		assertFalse(family1.equals(family7));
		assertFalse(family1.equals(family8));
		assertFalse(family1.equals(family9));
		assertFalse(family1.equals(family10));
		assertFalse(family1.equals(family11));
		assertFalse(family1.equals(family12));
		assertFalse(family1.equals(family13));

		assertFalse(family10.equals(family1));
		assertFalse(family10.equals(family2));
		assertFalse(family10.equals(family3));
		assertFalse(family10.equals(family4));
		assertFalse(family10.equals(family5));
		assertFalse(family10.equals(family6));
		assertFalse(family10.equals(family7));
		assertFalse(family10.equals(family8));
		assertFalse(family10.equals(family9));
		assertFalse(family11.equals(family12));
		assertFalse(family10.equals(family13));

		assertNotEquals(family1.getIndex(), family2.getIndex());
		assertNotEquals(family1.getIndex(), family3.getIndex());
		assertNotEquals(family1.getIndex(), family4.getIndex());
		assertNotEquals(family1.getIndex(), family5.getIndex());
		assertNotEquals(family1.getIndex(), family6.getIndex());
		assertNotEquals(family1.getIndex(), family7.getIndex());
		assertNotEquals(family1.getIndex(), family8.getIndex());
		assertNotEquals(family1.getIndex(), family9.getIndex());
		assertNotEquals(family1.getIndex(), family10.getIndex());
		assertNotEquals(family11.getIndex(), family12.getIndex());
		assertNotEquals(family1.getIndex(), family13.getIndex());
	}

	@Test
	public void familyEqualityFiltering () {
		Family family1 = Family.all(ComponentA.class).one(ComponentB.class).exclude(ComponentC.class).get();
		Family family2 = Family.all(ComponentB.class).one(ComponentC.class).exclude(ComponentA.class).get();
		Family family3 = Family.all(ComponentC.class).one(ComponentA.class).exclude(ComponentB.class).get();
		Family family4 = Family.all(ComponentA.class).one(ComponentB.class).exclude(ComponentC.class).get();
		Family family5 = Family.all(ComponentB.class).one(ComponentC.class).exclude(ComponentA.class).get();
		Family family6 = Family.all(ComponentC.class).one(ComponentA.class).exclude(ComponentB.class).get();

		assertTrue(family1.equals(family4));
		assertTrue(family2.equals(family5));
		assertTrue(family3.equals(family6));
		assertFalse(family1.equals(family2));
		assertFalse(family1.equals(family3));
	}

	@Test
	public void entityMatch () {
		Family family = Family.all(ComponentA.class, ComponentB.class).get();

		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());

		assertTrue(family.matches(entity));

		entity.add(new ComponentC());

		assertTrue(family.matches(entity));
	}

	@Test
	public void entityMismatch () {
		Family family = Family.all(ComponentA.class, ComponentC.class).get();

		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());

		assertFalse(family.matches(entity));

		entity.remove(ComponentB.class);

		assertFalse(family.matches(entity));
	}

	@Test
	public void entityMatchThenMismatch () {
		Family family = Family.all(ComponentA.class, ComponentB.class).get();

		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());

		assertTrue(family.matches(entity));

		entity.remove(ComponentA.class);

		assertFalse(family.matches(entity));
	}

	@Test
	public void entityMismatchThenMatch () {
		Family family = Family.all(ComponentA.class, ComponentB.class).get();

		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentC());

		assertFalse(family.matches(entity));

		entity.add(new ComponentB());

		assertTrue(family.matches(entity));
	}

	@Test
	public void testEmptyFamily() {
		Family family = Family.all().get();
		Entity entity = new Entity();
		assertTrue(family.matches(entity));
	}

	@Test
	public void familyFiltering () {
		Family family1 = Family.all(ComponentA.class, ComponentB.class).one(ComponentC.class, ComponentD.class)
			.exclude(ComponentE.class, ComponentF.class).get();

		Family family2 = Family.all(ComponentC.class, ComponentD.class).one(ComponentA.class, ComponentB.class)
			.exclude(ComponentE.class, ComponentF.class).get();

		Entity entity = new Entity();

		assertFalse(family1.matches(entity));
		assertFalse(family2.matches(entity));

		entity.add(new ComponentA());
		entity.add(new ComponentB());

		assertFalse(family1.matches(entity));
		assertFalse(family2.matches(entity));

		entity.add(new ComponentC());

		assertTrue(family1.matches(entity));
		assertFalse(family2.matches(entity));

		entity.add(new ComponentD());

		assertTrue(family1.matches(entity));
		assertTrue(family2.matches(entity));

		entity.add(new ComponentE());

		assertFalse(family1.matches(entity));
		assertFalse(family2.matches(entity));

		entity.remove(ComponentE.class);

		assertTrue(family1.matches(entity));
		assertTrue(family2.matches(entity));

		entity.remove(ComponentA.class);

		assertFalse(family1.matches(entity));
		assertTrue(family2.matches(entity));
	}

	@Test
	public void matchWithPooledEngine () {
		PooledEngine engine = new PooledEngine();

		engine.addSystem(new TestSystemA("A"));
		engine.addSystem(new TestSystemB("B"));

		Entity e = engine.createEntity();
		e.add(new ComponentB());
		e.add(new ComponentA());
		engine.addEntity(e);

		Family f = Family.all(ComponentB.class).exclude(ComponentA.class).get();

		assertFalse(f.matches(e));

		engine.clearPools();
	}

	@Test
	public void matchWithPooledEngineInverse () {
		PooledEngine engine = new PooledEngine();

		engine.addSystem(new TestSystemA("A"));
		engine.addSystem(new TestSystemB("B"));

		Entity e = engine.createEntity();
		e.add(new ComponentB());
		e.add(new ComponentA());
		engine.addEntity(e);

		Family f = Family.all(ComponentA.class).exclude(ComponentB.class).get();

		assertFalse(f.matches(e));
		engine.clearPools();
	}

	@Test
	public void matchWithoutSystems () {
		PooledEngine engine = new PooledEngine();

		Entity e = engine.createEntity();
		e.add(new ComponentB());
		e.add(new ComponentA());
		engine.addEntity(e);

		Family f = Family.all(ComponentB.class).exclude(ComponentA.class).get();

		assertFalse(f.matches(e));
		engine.clearPools();
	}

	@Test
	public void matchWithComplexBuilding () {
		Family family = Family.all(ComponentB.class).one(ComponentA.class).exclude(ComponentC.class).get();
		Entity entity = new Entity().add(new ComponentA());
		assertFalse(family.matches(entity));
		entity.add(new ComponentB());
		assertTrue(family.matches(entity));
		entity.add(new ComponentC());
		assertFalse(family.matches(entity));
	}

}
