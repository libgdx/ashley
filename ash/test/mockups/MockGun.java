package ash.test.mockups;

import ash.core.Entity;
import ash.test.EngineTest;

public class MockGun extends Entity {

	/**
	 * 
	 */
	private final EngineTest engineTest;

	public MockGun(EngineTest engineTest, MockSpaceShip owner) {
		super(); // generate a name
		this.engineTest = engineTest;
		this.add(new MockAngle(0.0f)).add(new MockBullets()).add(new MockOwner(owner)); // angle compared to ship!
		this.engineTest.engine.addEntity(this);
	}
}