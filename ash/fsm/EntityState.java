package ash.fsm;

import java.util.HashMap;
import java.util.Map;

import ash.core.Component;

/**
 * Represents a state for an EntityStateMachine. The state contains any number of ComponentProviders which
 * are used to add components to the entity when this state is entered.
 * 
 * @author Erik Borgers
 */
public class EntityState {
	/**
	 * @private
	 */
	final private Map<Class<? extends Component>, IComponentProvider> providers; // contains the Component(s). The key is the class of the component

	public EntityState() {
		providers = new HashMap<Class<? extends Component>, IComponentProvider>();
	}

	/**
	 * Add a new ComponentMapping to this state. The mapping is a utility class that is used to
	 * map a component type to the provider that provides the component.
	 * 
	 * @param type The type of component to be mapped
	 * @return The component mapping to use when setting the provider for the component
	 */
	public StateComponentMapping add(Class<? extends Component> type) {
		return new StateComponentMapping(this, type);
	}

	/**
	 * Get the ComponentProvider for a particular component type.
	 * 
	 * @param type The type of component to get the provider for
	 * @return The ComponentProvider
	 */
	public IComponentProvider get(Class<? extends Component> type) {
		return getProviders().get(type);
	}

	/**
	 * Get the ComponentProvider for a particular component type.
	 * 
	 * @param type The type of component to get the provider for
	 * @return The ComponentProvider
	 */
	public void set(Class<? extends Component> type, IComponentProvider providor) {
		getProviders().put(type, providor);
	}

	/**
	 * To determine whether this state has a provider for a specific component type.
	 * 
	 * @param type The type of component to look for a provider for
	 * @return true if there is a provider for the given type, false otherwise
	 */
	public boolean has(Class<? extends Component> type) {
		return getProviders().containsKey(type);
	}

	public Map<Class<? extends Component>, IComponentProvider> getProviders() {
		return providers;
	}

}
