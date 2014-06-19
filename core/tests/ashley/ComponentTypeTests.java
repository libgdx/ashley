package ashley;

import static org.junit.Assert.*;

import org.junit.Test;

import ashley.core.Component;
import ashley.core.ComponentType;

public class ComponentTypeTests {

	private static class ComponentA extends Component {
		
	}
	
	private static class ComponentB extends Component {
		
	}
	
	@Test
	public void validComponentType() {
		assertNotNull(ComponentType.getTypeFor(ComponentA.class));
		assertNotNull(ComponentType.getTypeFor(ComponentB.class));
	}
	
	@Test
	public void sameComponentType() {
		ComponentType componentType1 = ComponentType.getTypeFor(ComponentA.class);
		ComponentType componentType2 = ComponentType.getTypeFor(ComponentA.class);
		
		assertEquals(true, componentType1.equals(componentType2));
		assertEquals(true, componentType2.equals(componentType1));
		assertEquals(componentType1.getIndex(), componentType2.getIndex());
		assertEquals(componentType1.getIndex(), ComponentType.getIndexFor(ComponentA.class));
		assertEquals(componentType2.getIndex(), ComponentType.getIndexFor(ComponentA.class));
	}
	
	@Test
	public void differentComponentType() {
		ComponentType componentType1 = ComponentType.getTypeFor(ComponentA.class);
		ComponentType componentType2 = ComponentType.getTypeFor(ComponentB.class);
		
		assertEquals(false, componentType1.equals(componentType2));
		assertEquals(false, componentType2.equals(componentType1));
		assertNotEquals(componentType1.getIndex(), componentType2.getIndex());
		assertNotEquals(componentType1.getIndex(), ComponentType.getIndexFor(ComponentB.class));
		assertNotEquals(componentType2.getIndex(), ComponentType.getIndexFor(ComponentA.class));
	}
}
