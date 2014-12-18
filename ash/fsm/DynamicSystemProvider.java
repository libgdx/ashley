package ash.fsm;

import ash.core.EntitySystem;

/**
 * This System provider returns results of a method call. The method
 * is passed to the provider at initialisation.
 * 
 * @author Erik Borgers
 *
 */
public class DynamicSystemProvider implements ISystemProvider {
	private ISystemCreator method;
	private int systemPriority = 0;

	/**
	 * Constructor
	 *
	 * @param method The method that returns the System instance;
	 */
	public DynamicSystemProvider(ISystemCreator method) {
		this.method = method;
	}

	/**
	 * Used to request (create) a component from this provider by the provided method
	 *
	 * @return The instance of the System
	 */
	public EntitySystem getSystem() {
		return method.create();
	}

	/**
	 * The priority at which the System should be added to the Engine
	 */
	public int getPriority() {
		return systemPriority;
	}

	public void setPriority(int value) {
		systemPriority = value;
	}

	@Override
	public Class<? extends EntitySystem> getIdentifier() {
		return method.getIdentifier();
	}

}
