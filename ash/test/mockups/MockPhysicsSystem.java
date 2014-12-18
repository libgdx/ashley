package ash.test.mockups;

import ash.tools.ListIteratingSystem;
import ash.tools.Print;

public class MockPhysicsSystem extends ListIteratingSystem<MockPhysicsNode> {

	public MockPhysicsSystem() {
		super(); // watch this node type
	}

	@Override
	public void updateNode(MockPhysicsNode node, float time) {
		Print.message("node.pos = " + node.pos);
		node.pos.x += node.v.speed * time * Math.cos(node.a.rads);
		node.pos.y += node.v.speed * time * Math.sin(node.a.rads);
		Print.message("PhysicsSystem ticked a node at time, " + time + "new pos = " + node.pos.x + "," + node.pos.y);
	}
}