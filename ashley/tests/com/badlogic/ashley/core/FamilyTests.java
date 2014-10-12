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

import org.junit.Test;

import com.badlogic.ashley.systems.IteratingSystem;

@SuppressWarnings("unchecked")
public class FamilyTests {
	
	private static class ComponentA extends Component {}
	private static class ComponentB extends Component {}
	private static class ComponentC extends Component {}
	private static class ComponentD extends Component {}
	private static class ComponentE extends Component {}
	private static class ComponentF extends Component {}
	
	static class TestSystemA extends IteratingSystem {

        public TestSystemA(String name) {
            super(Family.getFor(ComponentType.getBitsFor(ComponentA.class),
                    ComponentType.getBitsFor(), ComponentType.getBitsFor()));
        }

        @Override
        public void processEntity(Entity e, float d) {
        }
    }

    static class TestSystemB extends IteratingSystem {

        public TestSystemB(String name) {
            super(Family.getFor(ComponentB.class));
        }

        @Override
        public void processEntity(Entity e, float d) {
        }
    }
	
	@Test
	public void validFamily() {
		assertNotNull(Family.getFor(ComponentA.class));
		assertNotNull(Family.getFor(ComponentB.class));
		assertNotNull(Family.getFor(ComponentC.class));
		assertNotNull(Family.getFor(ComponentA.class, ComponentB.class));
		assertNotNull(Family.getFor(ComponentA.class, ComponentC.class));
		assertNotNull(Family.getFor(ComponentB.class, ComponentA.class));
		assertNotNull(Family.getFor(ComponentB.class, ComponentC.class));
		assertNotNull(Family.getFor(ComponentC.class, ComponentA.class));
		assertNotNull(Family.getFor(ComponentC.class, ComponentB.class));
		assertNotNull(Family.getFor(ComponentA.class, ComponentB.class, ComponentC.class));
		assertNotNull(Family.getFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
										  ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
										  ComponentType.getBitsFor(ComponentE.class, ComponentF.class)));
	}
	
	@Test
	public void sameFamily() {
		Family family1 = Family.getFor(ComponentA.class);
		Family family2 = Family.getFor(ComponentA.class);
		Family family3 = Family.getFor(ComponentA.class, ComponentB.class);
		Family family4 = Family.getFor(ComponentA.class, ComponentB.class);
		Family family5 = Family.getFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family6 = Family.getFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family7 = Family.getFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		Family family8 = Family.getFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		
		assertTrue(family1.equals(family2));
		assertTrue(family2.equals(family1));
		assertTrue(family3.equals(family4));
		assertTrue(family4.equals(family3));
		assertTrue(family5.equals(family6));
		assertTrue(family6.equals(family5));
		assertTrue(family7.equals(family8));
		assertTrue(family8.equals(family7));
		
		assertEquals(family1.getIndex(), family2.getIndex());
		assertEquals(family3.getIndex(), family4.getIndex());
		assertEquals(family5.getIndex(), family6.getIndex());
		assertEquals(family7.getIndex(), family8.getIndex());
	}
	
	@Test
	public void differentFamily() {
		Family family1 = Family.getFor(ComponentA.class);
		Family family2 = Family.getFor(ComponentB.class);
		Family family3 = Family.getFor(ComponentC.class);
		Family family4 = Family.getFor(ComponentA.class, ComponentB.class);
		Family family5 = Family.getFor(ComponentA.class, ComponentC.class);
		Family family6 = Family.getFor(ComponentB.class, ComponentA.class);
		Family family7 = Family.getFor(ComponentB.class, ComponentC.class);
		Family family8 = Family.getFor(ComponentC.class, ComponentA.class);
		Family family9 = Family.getFor(ComponentC.class, ComponentB.class);
		Family family10 = Family.getFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family11 = Family.getFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											  ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											  ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		Family family12 = Family.getFor(ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											  ComponentType.getBitsFor(ComponentE.class, ComponentF.class),
											  ComponentType.getBitsFor(ComponentA.class, ComponentB.class));
		
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
	}
	
	@Test
	public void familyEqualityFiltering() {
		Family family1 = Family.getFor(ComponentType.getBitsFor(ComponentA.class),
									   ComponentType.getBitsFor(ComponentB.class),
									   ComponentType.getBitsFor(ComponentC.class));
		
		Family family2 = Family.getFor(ComponentType.getBitsFor(ComponentB.class),
									   ComponentType.getBitsFor(ComponentC.class),
									   ComponentType.getBitsFor(ComponentA.class));
		
		Family family3 = Family.getFor(ComponentType.getBitsFor(ComponentC.class),
									   ComponentType.getBitsFor(ComponentA.class),
									   ComponentType.getBitsFor(ComponentB.class));
		
		Family family4 = Family.getFor(ComponentType.getBitsFor(ComponentA.class),
									   ComponentType.getBitsFor(ComponentB.class),
									   ComponentType.getBitsFor(ComponentC.class));

		Family family5 = Family.getFor(ComponentType.getBitsFor(ComponentB.class),
									   ComponentType.getBitsFor(ComponentC.class),
									   ComponentType.getBitsFor(ComponentA.class));
		
		Family family6 = Family.getFor(ComponentType.getBitsFor(ComponentC.class),
									   ComponentType.getBitsFor(ComponentA.class),
									   ComponentType.getBitsFor(ComponentB.class));
		
		assertTrue(family1.equals(family4));
		assertTrue(family2.equals(family5));
		assertTrue(family3.equals(family6));
		assertFalse(family1.equals(family2));
		assertFalse(family1.equals(family3));
	}
	
	@Test
	public void entityMatch() {
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
		
		entity.add(new ComponentC());
		
		assertTrue(family.matches(entity));
	}
	
	@Test
	public void entityMismatch() {
		Family family = Family.getFor(ComponentA.class, ComponentC.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertFalse(family.matches(entity));
		
		entity.remove(ComponentB.class);
		
		assertFalse(family.matches(entity));
	}
	
	@Test
	public void entityMatchThenMismatch() {
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
		
		entity.remove(ComponentA.class);
		
		assertFalse(family.matches(entity));
	}
	
	@Test
	public void entityMismatchThenMatch() {
		Family family = Family.getFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentC());
		
		assertFalse(family.matches(entity));
		
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
	}
	
	@Test
	public void familyFiltering() {
		Family family1 = Family.getFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		
		Family family2 = Family.getFor(ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		
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
	public void matchWithPooledEngine() {
        PooledEngine engine = new PooledEngine();

        engine.addSystem(new TestSystemA("A"));
        engine.addSystem(new TestSystemB("B"));

        Entity e = engine.createEntity();
        e.add(new ComponentB());
        e.add(new ComponentA());
        engine.addEntity(e);

        Family f = Family.getFor(
                ComponentType.getBitsFor(ComponentB.class),
                ComponentType.getBitsFor(),
                ComponentType.getBitsFor(ComponentA.class));

        assertFalse(f.matches(e));

        engine.clearPools();
    }

	@Test
    public void matchWithPooledEngineInverse() {
        PooledEngine engine = new PooledEngine();

        engine.addSystem(new TestSystemA("A"));
        engine.addSystem(new TestSystemB("B"));

        Entity e = engine.createEntity();
        e.add(new ComponentB());
        e.add(new ComponentA());
        engine.addEntity(e);


        Family f = Family.getFor(
                ComponentType.getBitsFor(ComponentA.class),
                ComponentType.getBitsFor(),
                ComponentType.getBitsFor(ComponentB.class));

        assertFalse(f.matches(e));
        engine.clearPools();
    }

	@Test
    public void matchWithoutSystems() {
        PooledEngine engine = new PooledEngine();

        Entity e = engine.createEntity();
        e.add(new ComponentB());
        e.add(new ComponentA());
        engine.addEntity(e);

        Family f = Family.getFor(
                ComponentType.getBitsFor(ComponentB.class),
                ComponentType.getBitsFor(),
                ComponentType.getBitsFor(ComponentA.class));

        assertFalse(f.matches(e));
        engine.clearPools();
    }

}
