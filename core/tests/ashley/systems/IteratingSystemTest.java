package ashley.systems;

import ashley.core.Component;
import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.Family;
import org.junit.Test;

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
