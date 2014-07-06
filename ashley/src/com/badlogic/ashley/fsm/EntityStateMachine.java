package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class EntityStateMachine {
    private ObjectMap<String, EntityState> states;
    private EntityState currentState;
    public Entity entity;

    private ObjectMap<Class<? extends Component>, IComponentProvider> toAdd;

    public EntityStateMachine(Entity entity) {
        this.entity = entity;
        this.states = new ObjectMap<String, EntityState>();

        this.toAdd = new ObjectMap<Class<? extends Component>, IComponentProvider>();
    }

    public EntityState createState(String name) {
        EntityState state = new EntityState();
        states.put(name, state);
        return state;
    }

    public EntityStateMachine addState(String name, EntityState state) {
        states.put(name, state);
        return this;
    }

    public void changeState(String name) {
        EntityState newState = states.get(name);

        if (newState == null) {
            throw new RuntimeException("Entity state " + name + " doesn't exist");
        }

        if (newState == currentState) {
            newState = null;
            return;
        }

        Class<? extends Component> type;

        if (currentState != null) {
            toAdd.clear();

            for (Class<? extends Component> clazz : newState.providers.keys()) {
                type = clazz;
                toAdd.put(type, newState.providers.get(type));
            }

            for (Class<? extends Component> clazz : currentState.providers.keys()) {
                type = clazz;
                IComponentProvider other = toAdd.get(type);

                if (other != null && other.identifier() == currentState.providers.get(type).identifier()) {
                    toAdd.remove(type);
                } else {
                    entity.remove(type);
                }
            }
        } else {
            copy(newState.providers, toAdd);
        }

        for (Class<? extends Component> clazz : toAdd.keys()) {
            type = clazz;
            entity.add(toAdd.get(type).getComponent());
        }

        currentState = newState;
    }

    private void copy(ObjectMap<Class<? extends Component>, IComponentProvider> from, ObjectMap<Class<? extends Component>, IComponentProvider> to) {
        to.clear();
        for (ObjectMap.Entry<Class<? extends Component>, IComponentProvider> e : from.entries()) {
            to.put(e.key, e.value);
        }

    }
}
