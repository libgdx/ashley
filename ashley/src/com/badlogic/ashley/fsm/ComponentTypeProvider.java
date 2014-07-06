package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

public class ComponentTypeProvider<T extends Component> implements IComponentProvider {
    private Class<T> componentType;

    public ComponentTypeProvider(Class<T> componentType) {
        this.componentType = componentType;
    }

    @Override
    public T getComponent() {
        try {
            return componentType.newInstance();
        } catch (InstantiationException e) {
            throwInstantiateException();
        } catch (IllegalAccessException e) {
            throwInstantiateException();
        }
        return null;
    }

    private T throwInstantiateException() {
        throw new RuntimeException("[Ashley:ComponentTypeProvider] Could not instantiate " +
                "a component of type: " + componentType + ", class and default constructor should be visible");
    }

    @Override
    public Class<T> identifier() {
        return componentType;
    }
}
