package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class EntityStateMachineTest {
    class ComponentA extends Component {
    }

    class ComponentB extends Component {
    }

    EntityStateMachine fsm;
    Entity entity;


    @Before
    public void setUp() throws Exception {
        entity = new Entity();
        fsm = new EntityStateMachine(entity);
    }

    @After
    public void tearDown() throws Exception {
        entity = null;
        fsm = null;
    }

    @Test
    public void enterStateAddsStatesComponents() {
        EntityState state = new EntityState();
        ComponentA componentA = new ComponentA();
        ComponentMapper<ComponentA> mapperA = ComponentMapper.getFor(ComponentA.class);

        state.add(ComponentA.class).withInstance(componentA);
        fsm.addState("test", state);
        fsm.changeState("test");

        assertThat(mapperA.get(entity), sameInstance(componentA));
    }

    @Test
    public void enterSecondStateAddsSecondStatesComponents() {
        EntityState state1 = new EntityState();
        ComponentA componentA = new ComponentA();
        state1.add(ComponentA.class).withInstance(componentA);
        fsm.addState("test1", state1);
        fsm.changeState("test1");

        EntityState state2 = new EntityState();
        ComponentB componentB = new ComponentB();
        ComponentMapper<ComponentB> mapperB = ComponentMapper.getFor(ComponentB.class);
        state2.add(ComponentB.class).withInstance(componentB);
        fsm.addState("test2", state2);
        fsm.changeState("test2");

        assertThat(mapperB.get(entity), sameInstance(componentB));
    }

    @Test
    public void enterSecondStateRemovesFirstStatesComponents() {
        EntityState state1 = new EntityState();
        ComponentA componentA = new ComponentA();
        ComponentMapper<ComponentA> mapperA = ComponentMapper.getFor(ComponentA.class);
        state1.add(ComponentA.class).withInstance(componentA);
        fsm.addState("test1", state1);
        fsm.changeState("test1");

        EntityState state2 = new EntityState();
        ComponentB componentB = new ComponentB();
        state2.add(ComponentB.class).withInstance(componentB);
        fsm.addState("test2", state2);
        fsm.changeState("test2");

        assertThat(mapperA.has(entity), is(false));
    }

    @Test
    public void enterSecondStateDoesNotRemoveOverlappingComponents() {
        entity.componentRemoved.add(new Listener<Entity>() {
            @Override
            public void receive(Signal<Entity> signal, Entity object) {
                fail("Should not remove overlapping components");
            }
        });

        EntityState state1 = new EntityState();
        ComponentA componentA = new ComponentA();
        ComponentMapper<ComponentA> mapperA = ComponentMapper.getFor(ComponentA.class);
        state1.add(ComponentA.class).withInstance(componentA);
        fsm.addState("test1", state1);
        fsm.changeState("test1");

        EntityState state2 = new EntityState();
        ComponentB componentB = new ComponentB();
        state2.add(ComponentA.class).withInstance(componentA);
        state2.add(ComponentB.class).withInstance(componentB);
        fsm.addState("test2", state2);
        fsm.changeState("test2");

        assertThat(mapperA.get(entity), sameInstance(componentA));
    }

    @Test
    public void enterSecondStateRemovesDifferentComponentsOfSameType() {

        EntityState state1 = new EntityState();
        ComponentA componentA = new ComponentA();
        ComponentMapper<ComponentA> mapperA = ComponentMapper.getFor(ComponentA.class);
        state1.add(ComponentA.class).withInstance(componentA);
        fsm.addState("test1", state1);
        fsm.changeState("test1");

        EntityState state2 = new EntityState();
        ComponentA componentA2 = new ComponentA();
        ComponentB componentB = new ComponentB();
        state2.add(ComponentA.class).withInstance(componentA2);
        state2.add(ComponentB.class).withInstance(componentB);
        fsm.addState("test2", state2);
        fsm.changeState("test2");

        assertThat(mapperA.get(entity), sameInstance(componentA2));
    }
}