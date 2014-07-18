/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.systems;

import org.junit.Test;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import static org.junit.Assert.*;

public class IteratingSystemTest {
    private static class ComponentA extends Component {}
    private static class ComponentB extends Component {}
    private static class ComponentC extends Component {}

    private static class IteratingSystemMock extends IteratingSystem {
        public int numUpdates;

        public IteratingSystemMock(Family family) {
            super(family);
        }

        @Override
        public void processEntity(Entity entity, float deltaTime) {
             ++numUpdates;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldIterateEntitiesWithCorrectFamily(){
        final float delta = 0.15f;
        final Engine engine = new Engine();

        final Family family = Family.getFamilyFor(ComponentA.class, ComponentB.class);
        final IteratingSystemMock system = new IteratingSystemMock(family);
        final Entity e = new Entity();

        engine.addSystem(system);
        engine.addEntity(e);

        //When entity has ComponentA
        e.add(new ComponentA());
        engine.update(delta);

        assertEquals(0, system.numUpdates);

        //When entity has ComponentA and ComponentB
        system.numUpdates = 0;
        e.add(new ComponentB());
        engine.update(delta);

        assertEquals(1, system.numUpdates);

        //When entity has ComponentA, ComponentB and ComponentC
        system.numUpdates = 0;
        e.add(new ComponentC());
        engine.update(delta);

        assertEquals(1, system.numUpdates);

        //When entity has ComponentB and ComponentC
        system.numUpdates = 0;
        e.remove(ComponentA.class);
        e.add(new ComponentC());
        engine.update(delta);

        assertEquals(0, system.numUpdates);
    }
}
