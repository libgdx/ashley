package ash.test.mockups;

import ash.core.Component;

public class MockOwner extends Component {
	MockSpaceShip ship;

	public MockOwner(MockSpaceShip s) {
		super();
		this.ship = s;
	}
}