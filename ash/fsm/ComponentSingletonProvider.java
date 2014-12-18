package ash.fsm;

import ash.core.Component;
import ash.tools.Print;

/**
 * This component provider always returns the same instance of the component. The instance
 * is created when first required and is of the type passed in to the constructor.
 * 
 * @author Erik Borgers
 */
public class ComponentSingletonProvider implements IComponentProvider {
	private Class<? extends Component> componentType;
	private Component instance = null;

	/**
	 * Constructor
	 * 
	 * @param type The type of the single instance
	 */
	public ComponentSingletonProvider(Class<? extends Component> type) {
		this.componentType = type;
	}

	@Override
	public Component getComponent() {
		if (instance == null) {
			try {
				instance = componentType.newInstance();
			} catch (Exception e) {
				Print.fatal("Couln not create Component of type " + componentType.getName(), e);
			}
		}
		return instance;
	}

	@Override
	public Class<? extends Component> getIdentifier() {
		return getComponent().getClass();
	}

}
