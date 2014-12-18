package ash.test;

// Alternatively define assertEquals as static import
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ash.core.Node;
import ash.core.NodeList;
import ash.test.mockups.MockNode;
import ash.tools.Print;

/**
 * 
 * @author Erik Borgers
 *
 */

public class NodeListTest {

	private NodeList nodes;

	@Before
	public void createEntity() throws Exception {
		nodes = new NodeList();
	}

	@After
	public void clearEntity() throws Exception {
		nodes = null;
	}

	@Test(timeout = 1000)
	public void AddNodeTest() {
		for (int i = 0; i < 5; ++i) {
			MockNode node = new MockNode(i);
			nodes.add(node);
		}
		assertEquals("list incorrect length", 5, nodes.getSize());
	}

	public void printList1() {
		int i = 0;
		for (Node p : nodes) {
			Print.message("Node " + i + " has pos " + ((MockNode) p).pos + " and prio " + p.getPriority());
		}
		Print.message("");
	}

	@Test(timeout = 1000)
	public void AddNodeWithPriorityTest() {
		for (int i = 1; i < 6; i++) {
			MockNode node = new MockNode(i);
			nodes.add(node, 2 * i);
		}
		MockNode node = new MockNode(5);
		nodes.add(node, 5); // so priorities are 10, 8, 6, 5, 4, 2
		assertEquals("list incorrect length", 6, nodes.getSize());
		MockNode n = (MockNode) nodes.getHead();
		assertEquals("element wrong priority", 10, n.getPriority());
		n = (MockNode) n.getNext();
		assertEquals("element wrong priority", 8, n.getPriority());
		n = (MockNode) n.getNext();
		assertEquals("element wrong priority", 6, n.getPriority());
		n = (MockNode) n.getNext();
		assertEquals("element wrong priority", 5, n.getPriority());
		n = (MockNode) n.getNext();
		assertEquals("element wrong priority", 4, n.getPriority());
		n = (MockNode) n.getNext();
		assertEquals("element wrong priority", 2, n.getPriority());
	}

	@Test(timeout = 1000)
	public void ForTest() {
		for (int i = 1; i < 4; i++) { // 1+2+3 
			MockNode node = new MockNode(i);
			nodes.add(node);
		}
		MockNode e = (MockNode) nodes.getHead();
		assertEquals("should be 1", 1, e.pos);
		e = (MockNode) e.getNext();
		assertEquals("should be 2", 2, e.pos);
		e = (MockNode) e.getNext();
		assertEquals("should be 3", 3, e.pos);
		e = (MockNode) e.getNext(); // should return null
		assertEquals("should be null pointer", null, e);
	}

	@Test(timeout = 1000)
	public void ItteratorForTest() {
		for (int i = 1; i < 5; i++) { // 1+2+3+4 
			MockNode node = new MockNode(i);
			nodes.add(node);
		}
		int sum = 0;
		int j = 0;
		for (Node n : nodes) {
			assertTrue("unexpected null pointer in element index" + j, n != null);
			MockNode mn = (MockNode) n;
			sum += mn.pos;
			j++;
		}
		assertEquals("wrong number of itterations", 4, j);
		assertEquals("sum incorrect over nodes", 10, sum);
	}

	@Test(expected = IllegalStateException.class)
	public void ItteratorExceptionTest() {
		for (int i = 1; i < 5; i++) { // 1+2+3+4 
			MockNode node = new MockNode(i);
			nodes.add(node);
		}
		int j = 1;
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext()) {
			if (j == 2) {
				iter.remove();
				iter.remove(); // should throw exception as expected
			}
			j++;
		}
	}

	@Test(timeout = 1000)
	public void ItteratorWhileTest() {
		for (int i = 1; i < 5; i++) { // 1+2+3+4 
			MockNode node = new MockNode(i);
			nodes.add(node);
		}
		int j = 1;
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext()) {
			iter.next();
			if (j == 2)
				iter.remove();
			j++;
		} // should be 1+3+4 now

		int sum = 0;
		for (Node n : nodes) {
			MockNode mn = (MockNode) n;
			sum += mn.pos;
		}
		assertEquals("sum incorrect over nodes", 8, sum);
	}

	@Test
	public void DeleteNodeTest() {
		MockNode r = null;
		for (int i = 1; i < 6; ++i) {
			MockNode node = new MockNode(i);
			nodes.add(node);
			if (i == 3)
				r = node; // remember a node to delete
		}
		assertEquals("list incorrect length", 5, nodes.getSize());
		nodes.remove(r);
		assertEquals("list incorrect length", 4, nodes.getSize());

		// now try to remove an element NOT on the list
		MockNode node = new MockNode(6);
		boolean didThrow = false;
		try {
			nodes.remove(node);
		} catch (Exception e1) {
			didThrow = true;
		}
		assertEquals("excepion should have been thrown", true, didThrow);
	}

	/////

}
