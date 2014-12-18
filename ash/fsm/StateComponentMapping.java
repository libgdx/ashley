package ash.fsm;

import ash.core.Component;

/**
 * Used by the EntityState class to create the mappings of components to providers via a fluent interface.
 * 
 * @author Erik Borgers
 */
class StateComponentMapping {
	private Class<? extends Component> componentType;
	private EntityState creatingState;
	private IComponentProvider provider;

	/**
	 * Used internally, the constructor creates a component mapping. The constructor
	 * creates a ComponentTypeProvider as the default mapping, which will be replaced
	 * by more specific mappings if other methods are called.
	 *
	 * @param creatingState The EntityState that the mapping will belong to
	 * @param type The component type for the mapping
	 */
	public StateComponentMapping(EntityState creatingState, Class<? extends Component> type) {
		this.creatingState = creatingState;
		componentType = type;
		withType(type);
	}

	/**
	 * Creates a mapping for the component type to a specific component instance. A
	 * ComponentInstanceProvider is used for the mapping.
	 *
	 * @param component The component instance to use for the mapping
	 * @return This ComponentMapping, so more modifications can be applied
	 */
	public StateComponentMapping withInstance(Component component) {
		setProvider(new ComponentInstanceProvider(component));
		return this;
	}

	/**
	 * Creates a mapping for the component type to new instances of the provided type.
	 * The type should be the same as or extend the type for this mapping. A ComponentTypeProvider
	 * is used for the mapping.
	 *
	 * @param type The type of components to be created by this mapping
	 * @return This ComponentMapping, so more modifications can be applied
	 */
	public StateComponentMapping withType(Class<? extends Component> type) {
		setProvider(new ComponentTypeProvider(type));
		return this;
	}

	/**
	 * Creates a mapping for the component type to a single instance of the provided type.
	 * The instance is not created until it is first requested. The type should be the same
	 * as or extend the type for this mapping. A ComponentSingletonProvider is used for
	 * the mapping.
	 *
	 * @param The type of the single instance to be created. 
	 * 
	 * @return This ComponentMapping, so more modifications can be applied
	 */
	public StateComponentMapping withSingleton(Class<? extends Component> type) {
		if (type == null) // TODO kan dat?
		{
			type = componentType;
		}
		setProvider(new ComponentSingletonProvider(type));
		return this;
	}

	/**
	 * Creates a mapping for the component type to a single instance of the provided type.
	 * The instance is not created until it is first requested. The type should be the same
	 * as or extend the type for this mapping. A ComponentSingletonProvider is used for
	 * the mapping.
	 *
	 * The type of the mapping is used.
	 * 
	 * @return This ComponentMapping, so more modifications can be applied
	 */
	public StateComponentMapping withSingleton() {
		return withSingleton(componentType);
	}

	/**
	 * Creates a mapping for the component type to a method call. A
	 * DynamicComponentProvider is used for the mapping.
	 *
	 * @param method The method to return the component instance
	 * @return This ComponentMapping, so more modifications can be applied
	 */
	public StateComponentMapping withMethod(IComponentCreator creator) {
		setProvider(new DynamicComponentProvider(creator));
		return this;
	}

	/**
	 * Creates a mapping for the component type to any ComponentProvider.
	 *
	 * @param provider The component provider to use.
	 * @return This ComponentMapping, so more modifications can be applied.
	 */
	public StateComponentMapping withProvider(IComponentProvider provider) {
		setProvider(provider);
		return this;
	}

	/**
	 * Maps through to the add method of the EntityState that this mapping belongs to
	 * so that a fluent interface can be used when configuring entity states.
	 *
	 * @param type The type of component to add a mapping to the state for
	 * @return The new ComponentMapping for that type
	 */
	public StateComponentMapping add(Class<? extends Component> type) {
		return creatingState.add(type);
	}

	private void setProvider(IComponentProvider provider) {
		this.provider = provider;
		creatingState.set(componentType, provider);
	}

}
