package ash.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ash.core.Engine;
import ash.test.mockups.MockBulletsComponent;
import ash.test.mockups.MockFireSystem;
import ash.test.mockups.MockGunEntity;
import ash.test.mockups.MockPhysicsSystem;
import ash.test.mockups.MockSpaceShipEntity;

/**
 * 
 * @author Erik Borgers
 *
 */
public class EngineTest {

	public Engine engine;

	@Before
	public void createEntity() throws Exception {
		engine = new Engine();
	}

	@After
	public void clearEntity() throws Exception {
		engine = null;
	}

	@Test
	public void AddEntityTest() {
		MockSpaceShipEntity s1 = new MockSpaceShipEntity(this, "MyShip1");
		assertEquals("failed to add ship", 1, engine.getEntities().size());
		MockSpaceShipEntity s2 = new MockSpaceShipEntity(this, "MyShip2");
		MockGunEntity g = new MockGunEntity(this, s1); // s1 has a gun, s2 does not
		assertEquals("failed to add", 3, engine.getEntities().size());
	}

	@Test
	public void RemoveEntityTest() {
		MockSpaceShipEntity s1 = new MockSpaceShipEntity(this, "MyShip1");
		MockSpaceShipEntity s2 = new MockSpaceShipEntity(this, "MyShip2");
		MockGunEntity g = new MockGunEntity(this, s1); // s1 has a gun, s2 does not
		assertEquals("failed to add", 3, engine.getEntities().size());
		engine.removeEntity(s2);
		assertEquals("failed to remove", 2, engine.getEntities().size());
		engine.removeEntity(s1);
		// you can remove the spaceship 1. The gun will still point to the entity (although the spaceship will no longer be updated and the components/nodes are gone, are they not?)
		assertEquals("failed to remove", 1, engine.getEntities().size());
		engine.removeAllEntities();
		assertEquals("failed to remove all", 0, engine.getEntities().size());
	}

	@Test
	public void AddSystemTest() {
		engine.addSystem(new MockPhysicsSystem(), 1);
		engine.addSystem(new MockFireSystem(), 2);

		MockSpaceShipEntity s1 = new MockSpaceShipEntity(this, "MyShip1");
		assertEquals("failed to add ship", 1, engine.getEntities().size());
		MockSpaceShipEntity s2 = new MockSpaceShipEntity(this, "MyShip2");
		MockGunEntity g = new MockGunEntity(this, s1); // s1 has a gun, s2 does not
		assertEquals("failed to add", 3, engine.getEntities().size());
	}

	@Test
	public void RemoveSystemTest() {
		MockPhysicsSystem p;
		engine.addSystem(p = new MockPhysicsSystem(), 1);
		engine.addSystem(new MockFireSystem(), 1);
		engine.removeSystem(p);
		boolean didThrow = false;
		try {
			engine.removeSystem(p);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("excepion should have been thrown", true, didThrow);
		engine.clear();
	}

	@Test
	public void nodeAddTest() {
		MockPhysicsSystem sys = new MockPhysicsSystem();
		MockFireSystem fire = new MockFireSystem();
		engine.addSystem(sys, 1);
		engine.addSystem(fire, 2);
		MockSpaceShipEntity s1 = new MockSpaceShipEntity(this, "MyShip1");
		assertEquals("failed to add ship", 1, engine.getEntities().size());
		assertEquals("wrong nrs of node added for physics system ", 1, sys.size());
		assertEquals("wrong nrs of node added for firesystem ", 0, fire.size());
		engine.update(0.1f);
	}

	@Test
	public void updateBulletTest() {
		MockPhysicsSystem sys = new MockPhysicsSystem();
		MockFireSystem fire = new MockFireSystem();
		engine.addSystem(sys, 1);
		engine.addSystem(fire, 2);
		MockSpaceShipEntity s1 = new MockSpaceShipEntity(this, "MyShip1");
		assertEquals("failed to add ship", 1, engine.getEntities().size());

		// the physics system should now contain a Node
		assertEquals("no node added for ship 1", 1, sys.size());
		MockSpaceShipEntity s2 = new MockSpaceShipEntity(this, "MyShip2");
		assertEquals("no node added for ship 2", 2, sys.size());
		MockGunEntity g = new MockGunEntity(this, s1); // s1 has a gun, s2 does not
		assertEquals("failed to add", 3, engine.getEntities().size());
		assertEquals("physics node should not be added for gun", 2, sys.size());

		// the fire system should now contain a Node
		assertEquals("wrong nr of nodes added for fire system", 1, fire.size());

		// now fire!	
		engine.update(0.1f);
		MockBulletsComponent b = (MockBulletsComponent) g.get(MockBulletsComponent.class);
		assertEquals("wrong nr of bullets left", 4, b.bulletNrs);
		engine.update(0.1f);
		assertEquals("wrong nr of bullets left", 3, b.bulletNrs);
	}

	//@Test
	public void doubleFieldTest() {
		// create a test that throws an error when a Node or Entity is used with 2 times the same component in it
	}

}
