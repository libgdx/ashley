package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

/**
 * User: andres
 * Date: 7/3/14 - 4:55 PM
 */
public interface IComponentProvider<T extends Component> {

    public T getComponent();

    public Object identifier();
}
