package ash.fsm;

import ash.core.Component;
import ash.tools.Print;

/**
 * This component provider always returns a new instance of a component. An instance
 * is created when requested and is of the type passed in to the constructor.
 * 
 * @author Erik Borgers
 */
public class ComponentTypeProvider implements IComponentProvider {
	private Class<? extends Component> componentType;

	/**
	 * Constructor
	 * 
	 * @param type The type of the instances to be created
	 */
	public ComponentTypeProvider(Class<? extends Component> type) {
		this.componentType = type;
	}

	/**
	 * Used to request a component from this provider
	 * 
	 * @return A new instance of the type provided in the constructor
	 */
	public Component getComponent() {
		try {
			return componentType.newInstance();
		} catch (Exception e) {
			Print.fatal("Couln not create Component of type " + componentType.getName(), e);
		}
		return null;
	}

	@Override
	public Class<? extends Component> getIdentifier() {
		return componentType;
	}

}
