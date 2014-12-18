package ash.fsm;

import ash.core.EntitySystem;

/**
 * This System provider always returns the same instance of the component. The system
 * is passed to the provider at initialisation.
 * 
 * @author Erik Borgers
 * 
 */
public class SystemInstanceProvider implements ISystemProvider {

	private EntitySystem instance;
	private int systemPriority;

	/**
	 * Constructor
	 *
	 * @param instance The instance to return whenever a System is requested.
	 */
	public SystemInstanceProvider(EntitySystem instance) {
		this.instance = instance;
		this.systemPriority = 0;
	}

	public EntitySystem getSystem() {
		return instance;
	}

	public int getPriority() {
		return systemPriority;
	}

	public void setPriority(int value) {
		systemPriority = value;
	}

	@Override
	public Class<? extends EntitySystem> getIdentifier() {
		return instance.getClass();
	}

}
