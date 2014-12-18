package ash.test;

//alteratively define assertEquals as static import
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ash.core.Pool;
import ash.core.Poolable;
import ash.test.mockups.MockHealthNode;
import ash.test.mockups.MockPhysicsNode;

/**
 * 
 * @author Erik Borgers
 *
 */

public class NodePoolTest {

	Pool<MockPhysicsNode> pool;

	@Before
	public void setUp() throws Exception {
		Class classType = MockPhysicsNode.class;
		pool = new Pool<MockPhysicsNode>(classType);
	}

	@After
	public void tearDown() throws Exception {
		pool = null;
	}

	@Test
	public void poolCreationTest() {
		new Pool<MockPhysicsNode>(MockPhysicsNode.class);
	}

	@Test
	public void newInstanceTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class c;
		MockPhysicsNode p = new MockPhysicsNode();
		c = p.getClass();
		c.newInstance();
		c = MockPhysicsNode.class;
		c.newInstance();
		c = Class.forName("ash.test.mockups.MockPhysicsNode");
		c.newInstance();
	}

	@Test
	public void getFirstNodeTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		assertTrue("should not be null", n1 != null);
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		assertFalse("objects should be different", n1 == n2);
	}

	@Test
	public void poolDisposeTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		pool.dispose(n1);
		pool.dispose(n2);
		MockPhysicsNode n3 = (MockPhysicsNode) pool.get();
		assertTrue("objects should be same", ((n1 == n3) || (n2 == n3)));
	}

	@Test
	public void doubleDisposeTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		pool.dispose(n1);
		pool.dispose(n2);
		boolean didThrow = false;
		try {
			pool.dispose(n1);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("no exception is thrown with current implementation", false, didThrow); // not implemented for speed
	}

	@Test
	public void doubleCacheTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		pool.cache(n2);
		pool.cache(n1);
		boolean didThrow = false;
		try {
			pool.cache(n2);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("exception should have not been thrown", false, didThrow); // not implemented for speed
	}

	@Test
	public void doubleDisposeCacheTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		pool.cache(n2);
		pool.cache(n1);
		boolean didThrow = false;
		try {
			pool.dispose(n2);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("exception should not have been thrown", false, didThrow); // not implemented for speed
	}

	@Test
	public void poolCacheTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		pool.cache(n1);
		MockPhysicsNode n2 = (MockPhysicsNode) pool.get();
		assertFalse("objects should be different", n1 == n2);
		pool.releaseCache();
		MockPhysicsNode n3 = (MockPhysicsNode) pool.get();
		assertTrue("objects should be same", n1 == n3);
	}

	@Test
	public void wrongTypeTest() {
		MockPhysicsNode n1 = (MockPhysicsNode) pool.get();
		MockHealthNode h1 = new MockHealthNode();
		boolean didThrow = false;
		try {
			// pool.dispose(h1); would give a programming error
			Poolable p = h1;
			pool.dispose((MockPhysicsNode) p);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("exception should have been thrown", true, didThrow);
	}

}
