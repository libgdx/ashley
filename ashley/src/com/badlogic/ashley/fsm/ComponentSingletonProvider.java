package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

public class ComponentSingletonProvider<T extends Component> implements IComponentProvider {

    private Class<T> componentType;
    private T instance;

    public ComponentSingletonProvider(Class<T> componentType) {
        this.componentType = componentType;
    }

    @Override
    public T getComponent() {
        if (instance == null) {
            try {
                instance = componentType.newInstance();
            } catch (InstantiationException e) {
                throwInstantiateException();
            } catch (IllegalAccessException e) {
                throwInstantiateException();
            }
        }
        return instance;
    }

    private T throwInstantiateException() {
        throw new RuntimeException("[Ashley:ComponentSingletonProvider] Could not instantiate " +
                "a component of type: " + componentType + ", class and default constructor should be visible");
    }

    @Override
    public T identifier() {
        return getComponent();
    }
}
