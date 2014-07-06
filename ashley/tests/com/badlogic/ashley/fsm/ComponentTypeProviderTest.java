package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ComponentTypeProviderTest {
    static class ComponentA extends Component {
    }

    @Test
    public void providerReturnsAnInstanceOfType() {
        ComponentTypeProvider<ComponentA> provider = new ComponentTypeProvider<ComponentA>(ComponentA.class);
        assertThat(provider.getComponent(), instanceOf(ComponentA.class));
    }

    @Test
    public void providerReturnsNewInstanceEachTime() {
        ComponentTypeProvider<ComponentA> provider = new ComponentTypeProvider<ComponentA>(ComponentA.class);
        assertThat(provider.getComponent(), not(provider.getComponent()));
    }

    @Test
    public void providersWithSameTypeHaveSameIdentifier() {
        ComponentTypeProvider<ComponentA> provider1 = new ComponentTypeProvider<ComponentA>(ComponentA.class);
        ComponentTypeProvider<ComponentA> provider2 = new ComponentTypeProvider<ComponentA>(ComponentA.class);
        assertThat(provider1.identifier(), equalTo(provider2.identifier()));
    }

}