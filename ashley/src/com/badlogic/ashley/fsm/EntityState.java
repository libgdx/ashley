package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;

public class EntityState {

    ObjectMap<Class<? extends Component>, IComponentProvider> providers = new ObjectMap<Class<? extends Component>, IComponentProvider>();

    public <T extends Component> StateComponentMapping add(Class<T> type) {
        return new StateComponentMapping(this, type);
    }

    public <T extends Component> IComponentProvider get(Class<T> type) {
        return providers.get(type);
    }

    public <T extends Component> boolean has(Class<T> type) {
        return providers.get(type) != null;
    }
}
