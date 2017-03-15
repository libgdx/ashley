package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class EntityStateTest {
    static class ComponentA extends Component {
    }

    static class SubComponentA extends ComponentA {
    }

    static class ComponentB extends Component {
        int value;
    }

    static class CustomBProvider implements IComponentProvider<ComponentB> {
        ComponentB instance;

        @Override
        public ComponentB getComponent() {
            ComponentB componentB = new ComponentB();
            componentB.value = 100;
            return componentB;
        }

        @Override
        public Class<ComponentB> identifier() {
            return ComponentB.class;
        }
    }

    EntityState state;

    @Before
    public void setUp() throws Exception {
        state = new EntityState();
    }

    @After
    public void tearDown() throws Exception {
        state = null;
    }

    @Test
    public void addWithNoQualifierCreatesTypeProvider() {
        state.add(ComponentA.class);
        IComponentProvider provider = state.providers.get(ComponentA.class);

        assertThat(provider, instanceOf(ComponentTypeProvider.class));
        assertThat(provider.getComponent(), instanceOf(ComponentA.class));
    }

    @Test
    public void addWithTypeQualifierCreatesTypeProvider() {
        state.add(ComponentA.class).withType(SubComponentA.class);
        IComponentProvider provider = state.providers.get(ComponentA.class);

        assertThat(provider, instanceOf(ComponentTypeProvider.class));
        assertThat(provider.getComponent(), instanceOf(SubComponentA.class));
    }

    @Test
    public void addWithInstanceQualifierCreatesInstanceProvider() {
        ComponentA component = new ComponentA();
        state.add(ComponentA.class).withInstance(component);
        IComponentProvider provider = state.providers.get(ComponentA.class);

        assertThat(provider, instanceOf(ComponentInstanceProvider.class));
        assertThat(component, equalTo(provider.getComponent()));
    }

    @Test
    public void addWithSingletonQualifierCreatesSingletonProvider() {

        state.add(ComponentA.class).withSingleton(ComponentA.class);
        IComponentProvider provider = state.providers.get(ComponentA.class);

        assertThat(provider, instanceOf(ComponentSingletonProvider.class));
        assertThat(provider.getComponent(), instanceOf(ComponentA.class));
    }

    @Test
    public void addWithCustomProviderCreatesCustomProvider() {
        CustomBProvider bProvider = new CustomBProvider();

        state.add(ComponentB.class).withProvider(bProvider);

        assertThat(bProvider, instanceOf(CustomBProvider.class));
        assertThat(bProvider.getComponent(), instanceOf(ComponentB.class));
        assertThat(bProvider.getComponent().value, equalTo(100));
    }
}
