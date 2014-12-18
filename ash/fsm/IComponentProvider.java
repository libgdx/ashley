package ash.fsm;

import ash.core.Component;

/**
 * This is the Interface for component providers (factories for components). 
 * 
 * Component providers are used to supply components for states within an EntityStateMachine. 
 * 
 * Ash includes three standard component providers,
 * ComponentTypeProvider, ComponentInstanceProvider and ComponentSingletonProvider. 
 * 
 * Developers may wish to create more.
 * 
 * @author Erik Borgers
 */
public interface IComponentProvider {
	/**
	 * Used to request (create or just pass) a component from the provider (factory).
	 * 
	 * @return A component for use in the state that the entity is entering
	 */
	public Component getComponent();

	/**
	 * Returns an identifier that is used to determine whether two component providers will
	 * return the equivalent components.
	 * 
	 * <p>If an entity is changing state and the state it is leaving and the state is is 
	 * entering have components of the same type, then the identifiers (Erik: classes!) of the component
	 * providers are compared. If the two identifiers are the same then the component
	 * is not removed. If they are different, the component from the old state is removed
	 * and a component for the new state is added.</p>
	 * 
	 * @return An object
	 */
	public Class<? extends Component> getIdentifier();

}
