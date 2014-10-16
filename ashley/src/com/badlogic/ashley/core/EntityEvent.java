package com.badlogic.ashley.core;

public class EntityEvent {

    private Entity entity;
    private Component component;

    protected EntityEvent() {
    }

    public Entity getEntity() {
	return entity;
    }

    protected void setComponent(Component component) {
	this.component = component;
    }

    public Component getComponent() {
	return component;
    }

    protected void setEntity(Entity entity) {
	this.entity = entity;
    }

    protected void free() {
	entity = null;
	component = null;
    }

}
