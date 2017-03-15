package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

public class StateComponentMapping {
    private Class<? extends Component> componentType;
    private EntityState creatingState;
    private IComponentProvider provider;

    public StateComponentMapping(EntityState creatingState, Class<? extends Component> type) {
        this.creatingState = creatingState;
        this.componentType = type;
        withType(type);
    }

    public <T extends Component> StateComponentMapping withType(Class<T> type) {
        setProvider(new ComponentTypeProvider<T>(type));
        return this;
    }

    public <T extends Component> StateComponentMapping withInstance(T component) {
        setProvider(new ComponentInstanceProvider<T>(component));
        return this;
    }

    public <T extends Component> StateComponentMapping withSingleton(Class<T> type) {
        if (type == null) {
            type = (Class<T>) componentType;
        }
        setProvider(new ComponentSingletonProvider<T>(type));
        return this;
    }

    public StateComponentMapping withSingleton() {
        return withSingleton(null);
    }

    public <T extends Component> StateComponentMapping withProvider(IComponentProvider<T> provider) {
        setProvider(provider);
        return this;
    }


    private void setProvider(IComponentProvider provider) {
        this.provider = provider;
        creatingState.providers.put(componentType, this.provider);
    }

    public <T extends Component> StateComponentMapping add(Class<T> type) {
        return creatingState.add(type);
    }
}
