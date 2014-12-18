package ash.test.mockups;

import ash.core.Node;

public class MockGunNode extends Node {
	public MockOwnerComponent owner; // gun is mounted on ...
	public MockBulletsComponent bullets; // number of bullets left
}