package ash.fsm;

import ash.core.Component;

/**
 * This component provider always returns the same instance of the component. The instance
 * is passed to the provider at initialisation.
 * 
 * @author Erik Borgers
 */
public class ComponentInstanceProvider implements IComponentProvider {
	private Component instance;

	/**
	 * Constructor
	 * 
	 * @param instance The instance to return whenever a component is requested.
	 */
	public ComponentInstanceProvider(Component instance) {
		this.instance = instance;
	}

	/**
	 * Used to request a component from this provider
	 * 
	 * @return The instance
	 */
	public Component getComponent() {
		return instance;
	}

	@Override
	public Class<? extends Component> getIdentifier() {
		return instance.getClass();
	}

}
