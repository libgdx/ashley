package ash.test.mockups;

import ash.core.Component;

public class MockOwnerComponent extends Component {
	MockSpaceShipEntity ship;

	public MockOwnerComponent(MockSpaceShipEntity s) {
		super();
		this.ship = s;
	}
}