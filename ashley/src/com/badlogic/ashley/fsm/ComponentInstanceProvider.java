package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

public class ComponentInstanceProvider<T extends Component> implements IComponentProvider {
    private T instance;

    public ComponentInstanceProvider(T instance) {
        this.instance = instance;
    }

    @Override
    public T getComponent() {
        return instance;
    }

    @Override
    public T identifier() {
        return instance;
    }

}
