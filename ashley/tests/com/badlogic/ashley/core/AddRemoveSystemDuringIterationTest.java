package com.badlogic.ashley.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 3 systems add at engine start with priority 1, 5, 10
 * System 5 add system with priority 4 during first iteration by system 5
 *
 */
public class AddRemoveSystemDuringIterationTest {
    Engine engine;
    List<Integer> systemCallOrder; //updateIndex -> list by priority
    boolean firstUpdateDone = false;

    private void addSystemUpdateCall(EntitySystem entitySystem) {
        systemCallOrder.add(entitySystem.priority);
    }

    @Before
    public void setUp() {
        engine = new Engine();
        systemCallOrder = new ArrayList<>();
        firstUpdateDone = false;
    }

    @Test
    public void addSystemDuringIterationTest() {
        engine = new Engine();
        EntitySystem system1 = new NamedSystem(1) {
        };
        EntitySystem system10 = new NamedSystem(10) {
        };
        final EntitySystem system4 = new NamedSystem(4) {  //System added during update
        };
        EntitySystem system5 = new NamedSystem(5) {

            @Override
            public void update(float deltaTime) {
                super.update(deltaTime);
                if (!firstUpdateDone) {
                    getEngine().addSystem(system4);
                    firstUpdateDone = true;
                }
            }
        };

        engine.addSystem(system1);
        engine.addSystem(system10);
        engine.addSystem(system5);

        engine.update(1); //it add system 45 add system 4 during iteration

        List<Integer> expected = Arrays.asList(1, 5, 10); //system 4 should not be called
        Assert.assertArrayEquals(expected.toArray(), systemCallOrder.toArray()); // result 1, 5, 5, 10

        systemCallOrder.clear();
        engine.update(1);
        expected = Arrays.asList(1, 4, 5, 10); //second update ok
        Assert.assertArrayEquals(expected.toArray(), systemCallOrder.toArray());

    }

    private class NamedSystem extends EntitySystem {

        public NamedSystem(int priority) {
            super(priority);
        }

        @Override
        public void update(float deltaTime) {
            addSystemUpdateCall(this);
        }
    }
}
