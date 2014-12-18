package ash.fsm;

import java.util.ArrayList;

import ash.core.EntitySystem;

/**
 * Represents a state for a SystemStateMachine. The state contains any number of SystemProviders which
 * are used to add Systems to the Engine when this state is entered.
 * 
 * @author Erik Borgers
 *
 */
public class EngineState {
	final private ArrayList<ISystemProvider> providers;

	public EngineState() {
		providers = new ArrayList<ISystemProvider>();
	}

	/**
	 * Creates a mapping for the System type to a specific System instance. A
	 * SystemInstanceProvider is used for the mapping.
	 *
	 * @param system The System instance to use for the mapping
	 * @return This StateSystemMapping, so more modifications can be applied
	 */
	public StateSystemMapping addInstance(EntitySystem system) {
		return addProvider(new SystemInstanceProvider(system));
	}

	/**
	 * Creates a mapping for the System type to a single instance of the provided type.
	 * The instance is not created until it is first requested. The type should be the same
	 * as or extend the type for this mapping. A SystemSingletonProvider is used for
	 * the mapping.
	 *
	 * @param type The type of the single instance to be created. If omitted, the type of the
	 * mapping is used.
	 * @return This StateSystemMapping, so more modifications can be applied
	 */
	public StateSystemMapping addSingleton(Class type) {
		return addProvider(new SystemSingletonProvider(type));
	}

	/**
	 * Creates a mapping for the System type to a method call.
	 * The method should return a System instance. A DynamicSystemProvider is used for
	 * the mapping.
	 *
	 * @param method The method to provide the System instance.
	 * @return This StateSystemMapping, so more modifications can be applied.
	 */
	public StateSystemMapping addMethod(ISystemCreator method) {
		return addProvider(new DynamicSystemProvider(method));
	}

	/**
	 * Adds any SystemProvider.
	 *
	 * @param provider The component provider to use.
	 * @return This StateSystemMapping, so more modifications can be applied.
	 */
	public StateSystemMapping addProvider(ISystemProvider provider) {
		StateSystemMapping mapping = new StateSystemMapping(this, provider);
		getProviders().add(provider); // original code uses "push": providers.push(provider);
		return mapping;
	}

	public ArrayList<ISystemProvider> getProviders() {
		return providers;
	}

}
