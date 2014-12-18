package ash.fsm;

import ash.core.EntitySystem;

/**
 * This is an Interface that you can implement to be used in a DynamicComponentProvider
 * 
 * @author Erik Borgers
 *
 */

public interface ISystemCreator {

	public EntitySystem create();

	/** the class of the EntitySystem that will be created  */
	public Class<? extends EntitySystem> getIdentifier();

}
