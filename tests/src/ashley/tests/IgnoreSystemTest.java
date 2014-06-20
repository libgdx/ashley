package ashley.tests;

import ashley.core.EntitySystem;
import ashley.core.PooledEngine;

public class IgnoreSystemTest {

    public static void main(String[] args){
        PooledEngine engine = new PooledEngine();

        CounterSystem counter = new CounterSystem();
        IgnoredSystem ignored = new IgnoredSystem();

        engine.addSystem(counter);
        engine.addSystem(ignored);

        for (int i = 0; i < 10; i++) {
            engine.update(0.25f);
        }
    }

    private static class CounterSystem extends EntitySystem {
        @Override
        public void update(float deltaTime) {
            log("Running " + getClass().getSimpleName());
        }
    }

    private static class IgnoredSystem extends EntitySystem {

        int counter = 0;

        @Override
        public boolean checkProcessing() {
            counter = 1 - counter;
            return counter == 1;
        }

        @Override
        public void update(float deltaTime) {
            log("Running " + getClass().getSimpleName());
        }
    }

    public static void log(String string){
        System.out.println(string);
    }
}
