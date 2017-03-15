package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

public interface IComponentProvider<T extends Component> {

    public T getComponent();

    public Object identifier();
}
