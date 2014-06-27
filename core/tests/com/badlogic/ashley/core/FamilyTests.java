package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class FamilyTests {
	
	private static class ComponentA extends Component {}
	private static class ComponentB extends Component {}
	private static class ComponentC extends Component {}
	private static class ComponentD extends Component {}
	private static class ComponentE extends Component {}
	private static class ComponentF extends Component {}
	
	@Test
	public void validFamily() {
		assertNotNull(Family.getFamilyFor(ComponentA.class));
		assertNotNull(Family.getFamilyFor(ComponentB.class));
		assertNotNull(Family.getFamilyFor(ComponentC.class));
		assertNotNull(Family.getFamilyFor(ComponentA.class, ComponentB.class));
		assertNotNull(Family.getFamilyFor(ComponentA.class, ComponentC.class));
		assertNotNull(Family.getFamilyFor(ComponentB.class, ComponentA.class));
		assertNotNull(Family.getFamilyFor(ComponentB.class, ComponentC.class));
		assertNotNull(Family.getFamilyFor(ComponentC.class, ComponentA.class));
		assertNotNull(Family.getFamilyFor(ComponentC.class, ComponentB.class));
		assertNotNull(Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class));
		assertNotNull(Family.getFamilyFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
										  ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
										  ComponentType.getBitsFor(ComponentE.class, ComponentF.class)));
	}
	
	@Test
	public void sameFamily() {
		Family family1 = Family.getFamilyFor(ComponentA.class);
		Family family2 = Family.getFamilyFor(ComponentA.class);
		Family family3 = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		Family family4 = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		Family family5 = Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family6 = Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family7 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		Family family8 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
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
		
		assertEquals(family1.getFamilyIndex(), family2.getFamilyIndex());
		assertEquals(family3.getFamilyIndex(), family4.getFamilyIndex());
		assertEquals(family5.getFamilyIndex(), family6.getFamilyIndex());
		assertEquals(family7.getFamilyIndex(), family8.getFamilyIndex());
	}
	
	@Test
	public void differentFamily() {
		Family family1 = Family.getFamilyFor(ComponentA.class);
		Family family2 = Family.getFamilyFor(ComponentB.class);
		Family family3 = Family.getFamilyFor(ComponentC.class);
		Family family4 = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		Family family5 = Family.getFamilyFor(ComponentA.class, ComponentC.class);
		Family family6 = Family.getFamilyFor(ComponentB.class, ComponentA.class);
		Family family7 = Family.getFamilyFor(ComponentB.class, ComponentC.class);
		Family family8 = Family.getFamilyFor(ComponentC.class, ComponentA.class);
		Family family9 = Family.getFamilyFor(ComponentC.class, ComponentB.class);
		Family family10 = Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family11 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											  ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											  ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		Family family12 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
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
		
		assertNotEquals(family1.getFamilyIndex(), family2.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family3.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family4.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family5.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family6.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family7.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family8.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family9.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family10.getFamilyIndex());
		assertNotEquals(family11.getFamilyIndex(), family12.getFamilyIndex());
	}
	
	@Test
	public void entityMatch() {
		Family family = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
		
		entity.add(new ComponentC());
		
		assertTrue(family.matches(entity));
	}
	
	@Test
	public void entityMismatch() {
		Family family = Family.getFamilyFor(ComponentA.class, ComponentC.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertFalse(family.matches(entity));
		
		entity.remove(ComponentB.class);
		
		assertFalse(family.matches(entity));
	}
	
	@Test
	public void entityMatchThenMismatch() {
		Family family = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
		
		entity.remove(ComponentA.class);
		
		assertFalse(family.matches(entity));
	}
	
	@Test
	public void entityMismatchThenMatch() {
		Family family = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		
		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentC());
		
		assertFalse(family.matches(entity));
		
		entity.add(new ComponentB());
		
		assertTrue(family.matches(entity));
	}
	
	@Test
	public void familyFiltering() {
		Family family1 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentA.class, ComponentB.class),
											 ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
											 ComponentType.getBitsFor(ComponentE.class, ComponentF.class));
		
		Family family2 = Family.getFamilyFor(ComponentType.getBitsFor(ComponentC.class, ComponentD.class),
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
}
