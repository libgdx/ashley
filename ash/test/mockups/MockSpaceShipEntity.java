package ash.test.mockups;

import ash.core.Entity;
import ash.test.EngineTest;

public class MockSpaceShipEntity extends Entity {

	/**
	 * 
	 */
	private final EngineTest engineTest;

	public MockSpaceShipEntity(EngineTest engineTest, String name) {
		super(name);
		this.engineTest = engineTest;
		this.add(new MockPosComponent(1, 1)).add(new MockVComponent((float) 10.0)).add(new MockAngleComponent(0.0f)).add(new MockHealthComponent());
		this.engineTest.engine.addEntity(this);
	}
}