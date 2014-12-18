package ash.fsm;

import ash.core.Component;

/**
 * This is an Interface that you can implement to be used in a DynamicComponentProvider
 * 
 * @author Erik Borgers
 *
 */

public interface IComponentCreator {

	public Component create();

	/** the class type returned when create would be called */
	public Class<? extends Component> getIdentifier();

}
