package ash.test.mockups;

import ash.core.Entity;
import ash.test.EngineTest;

public class MockSpaceShip extends Entity {

	/**
	 * 
	 */
	private final EngineTest engineTest;

	public MockSpaceShip(EngineTest engineTest, String name) {
		super(name);
		this.engineTest = engineTest;
		this.add(new MockPos(1, 1)).add(new MockV((float) 10.0)).add(new MockAngle(0.0f)).add(new MockHealth());
		this.engineTest.engine.addEntity(this);
	}
}