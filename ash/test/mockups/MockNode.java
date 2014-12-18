package ash.test.mockups;

import ash.core.Node;

public class MockNode extends Node {
	public int pos;

	public MockNode() {
		this(0);
	}

	public MockNode(int value) {
		pos = value;
	}
}