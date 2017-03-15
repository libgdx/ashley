package com.badlogic.ashley.fsm;

import com.badlogic.ashley.core.Component;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ComponentInstanceProviderTest {
    static class ComponentA extends Component {
    }

    @Test
    public void providerReturnsTheInstance() {
        ComponentA component = new ComponentA();
        ComponentInstanceProvider<ComponentA> provider = new ComponentInstanceProvider<ComponentA>(component);

        assertThat(provider.getComponent(), sameInstance(component));
    }

    @Test
    public void providersWithSameInstanceHaveSameIdentifier() {
        ComponentA component = new ComponentA();
        ComponentInstanceProvider<ComponentA> provider1 = new ComponentInstanceProvider<ComponentA>(component);
        ComponentInstanceProvider<ComponentA> provider2 = new ComponentInstanceProvider<ComponentA>(component);

        assertThat(provider1.identifier(), equalTo(provider2.identifier()));
    }

    @Test
    public void providersWithDifferentInstanceHaveDifferentIdentifier() {

        ComponentInstanceProvider<ComponentA> provider1 = new ComponentInstanceProvider<ComponentA>(new ComponentA());
        ComponentInstanceProvider<ComponentA> provider2 = new ComponentInstanceProvider<ComponentA>(new ComponentA());

        assertThat(provider1.identifier(), not(equalTo(provider2.identifier())));
    }

}