package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ComponentSingletonProviderTest {
    static class ComponentA extends Component {
    }

    @Test
    public void providerReturnsAnInstanceOfType() {
        ComponentSingletonProvider<ComponentA> provider = new ComponentSingletonProvider<ComponentA>(ComponentA.class);
        assertThat(provider.getComponent(), instanceOf(ComponentA.class));
    }

    @Test
    public void providerReturnsSameInstanceEachTime() {
        ComponentSingletonProvider<ComponentA> provider = new ComponentSingletonProvider<ComponentA>(ComponentA.class);
        assertThat(provider.getComponent(), equalTo(provider.getComponent()));
    }

    @Test
    public void providersWithSameTypeHaveDifferentIdentifier() {
        ComponentSingletonProvider<ComponentA> provider1 = new ComponentSingletonProvider<ComponentA>(ComponentA.class);
        ComponentSingletonProvider<ComponentA> provider2 = new ComponentSingletonProvider<ComponentA>(ComponentA.class);
        assertThat(provider1.identifier(), not(provider2.getComponent()));
    }
}