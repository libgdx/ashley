package ash.test.mockups;

import ash.tools.ListIteratingSystem;
import ash.tools.Print;

public class MockFireSystem extends ListIteratingSystem<MockGunNode> {

	public MockFireSystem() {
		super(); // watch this node type
	}

	@Override
	public void updateNode(MockGunNode node, float time) {
		Print.message(" bullets in gun = " + node.bullets.bulletNrs);
		Print.message(" owner of gun is = " + node.owner);
		Print.message("FireSystem ticked a node at time " + time + ", bullets left for ship "
				+ node.owner.ship.getName() + node.bullets.bulletNrs--);
	}
}