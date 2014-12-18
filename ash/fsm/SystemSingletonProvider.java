package ash.fsm;

import ash.core.EntitySystem;
import ash.tools.Print;

/**
 * This System provider always returns the same instance of the System. The instance
 * is created when first required and is of the type passed in to the constructor.
 * 
 * @author Erik Borgers
 */
public class SystemSingletonProvider implements ISystemProvider {
	private Class<? extends EntitySystem> type;
	private EntitySystem instance;
	private int systemPriority;

	/**
	 * Constructor
	 *
	 * @param type The type of the single System instance
	 */
	public SystemSingletonProvider(Class<? extends EntitySystem> type) {
		this.type = type;
		this.instance = null;
	}

	/**
	 * Used to request (create) a EntitySystem from this provider. This is the same for all calls
	 *
	 * @return The single instance
	 */
	public EntitySystem getSystem() {
		if (instance == null) {
			try {
				instance = type.newInstance();
			} catch (Exception e) {
				Print.fatal("Couln not create EntitySystem of type " + type.getName(), e);
			}
		}
		return instance;
	}

	/**
	 * The priority at which the System should be added to the Engine
	 */
	public int getPriority() {
		return systemPriority;
	}

	/**
	 * @private
	 */
	public void setPriority(int value) {
		systemPriority = value;
	}

	@Override
	public Class<? extends EntitySystem> getIdentifier() {
		return getSystem().getClass();
	}

}
