package ashley;

import static org.junit.Assert.*;

import org.junit.Test;

import ashley.core.Component;
import ashley.core.Entity;
import ashley.core.Family;

public class FamilyTests {
	
	private static class ComponentA extends Component {
		
	}
	
	private static class ComponentB extends Component {
			
	}
	
	private static class ComponentC extends Component {
		
	}
	
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
	}
	
	@Test
	public void sameFamily() {
		Family family1 = Family.getFamilyFor(ComponentA.class);
		Family family2 = Family.getFamilyFor(ComponentA.class);
		Family family3 = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		Family family4 = Family.getFamilyFor(ComponentA.class, ComponentB.class);
		Family family5 = Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class);
		Family family6 = Family.getFamilyFor(ComponentA.class, ComponentB.class, ComponentC.class);
		
		assertTrue(family1.equals(family2));
		assertTrue(family2.equals(family1));
		assertTrue(family3.equals(family4));
		assertTrue(family4.equals(family3));
		assertTrue(family5.equals(family6));
		assertTrue(family6.equals(family5));
		
		assertEquals(family1.getFamilyIndex(), family2.getFamilyIndex());
		assertEquals(family3.getFamilyIndex(), family4.getFamilyIndex());
		assertEquals(family5.getFamilyIndex(), family6.getFamilyIndex());
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
		
		assertFalse(family1.equals(family2));
		assertFalse(family1.equals(family3));
		assertFalse(family1.equals(family4));
		assertFalse(family1.equals(family5));
		assertFalse(family1.equals(family6));
		assertFalse(family1.equals(family7));
		assertFalse(family1.equals(family8));
		assertFalse(family1.equals(family9));
		assertFalse(family1.equals(family10));
		
		assertFalse(family10.equals(family1));
		assertFalse(family10.equals(family2));
		assertFalse(family10.equals(family3));
		assertFalse(family10.equals(family4));
		assertFalse(family10.equals(family5));
		assertFalse(family10.equals(family6));
		assertFalse(family10.equals(family7));
		assertFalse(family10.equals(family8));
		assertFalse(family10.equals(family9));
		
		assertNotEquals(family1.getFamilyIndex(), family2.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family3.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family4.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family5.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family6.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family7.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family8.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family9.getFamilyIndex());
		assertNotEquals(family1.getFamilyIndex(), family10.getFamilyIndex());
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
}
