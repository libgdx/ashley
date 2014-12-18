package ash.fsm;

import ash.core.EntitySystem;

/**
 * 
 * @author Erik Borgers
 *
 */

public interface ISystemProvider {

	/**
	 * return a System instance, either created or just past. 
	 * 
	 * @return
	 */
	public EntitySystem getSystem();

	/**
	 * Returns an identifier that is used to determine whether two system providers will
	 * return the equivalent systems.
	 * 
	 * <p>If an entity is changing state and the state it is leaving and the state is is 
	 * entering have systems of the same type, then the identifiers (Erik: classes!) of the system
	 * providers are compared. If the two identifiers are the same then the system
	 * is not removed. If they are different, the system from the old state is removed
	 * and a system for the new state is added.</p>
	 * 
	 * @return The class of the System
	 */
	public Class<? extends EntitySystem> getIdentifier();

	/**
	 * The priority at which the System should be added to the Engine
	 */
	public int getPriority();

	public void setPriority(int value);
}
