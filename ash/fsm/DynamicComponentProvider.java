package ash.fsm;

import ash.core.Component;

/**
 * This component provider calls a function to get the component instance. The function must
 * return a single component of the appropriate type.
 * 
 * @author Erik Borgers
 */
public class DynamicComponentProvider implements IComponentProvider {
	private IComponentCreator _closure;

	/**
	 * Constructor
	 * 
	 * @param closure The function that will return the component instance when called.
	 */
	public DynamicComponentProvider(IComponentCreator closure) {
		_closure = closure;
	}

	/**
	 * Used to request a component from this provider
	 * 
	 * @return The instance returned by calling the function
	 */
	public Component getComponent() {
		return _closure.create();
	}

	@Override
	public Class<? extends Component> getIdentifier() {
		return _closure.getIdentifier();
	}

}