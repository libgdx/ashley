package com.badlogic.ashley.serialization;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class EngineSerializerTests {

    public static class TestComponent implements Component {}

    public static class TestSystem extends EntitySystem {}

    @Transient
    public static class TransientSystem extends EntitySystem {}

    public static class TestComponentFilter implements EntityFilter {
        private final Family family = Family.all(TestComponent.class).get();
        @Override
        public boolean filter(Entity entity) {
            return family.matches(entity);
        }
    }

    public static class TestSystemSerializer implements Json.Serializer<TestSystem> {

        @Override
        public void write(Json json, TestSystem object, Class knownType) {

        }

        @Override
        public TestSystem read(Json json, JsonValue jsonData, Class type) {
            return new TestSystem();
        }
    }

    final String emptyEngineJson = "{entities:[],systems:{}}";
    final String oneEntityEngineJson = "{entities:[{components:{TestComponent:{}}}],systems:{}}";
    final String oneSystemEngineJson = "{entities:[],systems:{TestSystem:{}}}";
    final String engineJson = "{entities:[{components:{TestComponent:{}}}],systems:{TestSystem:{}}}";

    @Test
    public void readEmpty() {
        Json json = new Json();
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());

        Engine engine = json.fromJson(Engine.class, emptyEngineJson);

        assertNotNull(engine);
        assertEquals(0, engine.getEntities().size());
        assertEquals(0, engine.getSystems().size());
    }

    @Test
    public void readEntities() {
        Json json = new Json();
        json.addClassTag("TestComponent", TestComponent.class);
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());

        Engine engine = json.fromJson(Engine.class, oneEntityEngineJson);

        assertNotNull(engine);
        assertEquals(1, engine.getEntities().size());
        assertEquals(0, engine.getSystems().size());

        Entity entity = engine.getEntities().get(0);
        assertNotNull(entity);
        assertEquals(1, entity.getComponents().size());
        assertNotNull(entity.getComponent(TestComponent.class));
    }

    @Test
    public void readSystems() {
        Json json = new Json();
        json.addClassTag("TestSystem", TestSystem.class);
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());

        Engine engine = json.fromJson(Engine.class, oneSystemEngineJson);

        assertNotNull(engine);
        assertEquals(0, engine.getEntities().size());
        assertEquals(1, engine.getSystems().size());

        TestSystem system = engine.getSystem(TestSystem.class);
        assertNotNull(system);
    }

    @Test
    public void writeEmpty() {
        Json json = new Json();
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());

        Engine engine = new Engine();
        String engineJson = json.toJson(engine);

        assertEquals(emptyEngineJson, engineJson);
    }

    @Test
    public void writeEntities() {
        Json json = new Json();
        json.addClassTag("TestComponent", TestComponent.class);
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());

        Engine engine = new Engine();
        Entity entity = new Entity();
        entity.add(new TestComponent());
        engine.addEntity(entity);
        String engineJson = json.toJson(engine);

        assertEquals(oneEntityEngineJson, engineJson);
    }

    @Test
    public void writeEntitiesWithFilter() {
        Json json = new Json();
        json.addClassTag("TestComponent", TestComponent.class);

        EngineSerializer<Engine> serializer = new EngineSerializer<Engine>(new TestComponentFilter());
        json.setSerializer(Engine.class, serializer);

        Engine engine = new Engine();
        Entity entity = new Entity();
        entity.add(new TestComponent());
        engine.addEntity(entity);
        engine.addEntity(new Entity());
        String engineJson = json.toJson(engine);

        assertEquals(oneEntityEngineJson, engineJson);
    }

    @Test
    public void writeSystems() {
        Json json = new Json();
        json.addClassTag("TestSystem", TestSystem.class);
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());
        json.setSerializer(TestSystem.class, new TestSystemSerializer());

        Engine engine = new Engine();
        engine.addSystem(new TestSystem());
        String engineJson = json.toJson(engine);

        assertEquals(oneSystemEngineJson, engineJson);
    }

    @Test
    public void writeSystemsTransient() {
        Json json = new Json();
        json.addClassTag("TestSystem", TestSystem.class);
        json.setSerializer(Engine.class, new EngineSerializer<Engine>());
        json.setSerializer(TestSystem.class, new TestSystemSerializer());

        Engine engine = new Engine();
        engine.addSystem(new TestSystem());
        engine.addSystem(new TransientSystem());
        String engineJson = json.toJson(engine);

        assertEquals(oneSystemEngineJson, engineJson);
    }

    @Test
    public void readPooledEngine() {
        Json json = new Json();
        json.addClassTag("TestComponent", TestComponent.class);
        json.addClassTag("TestSystem", TestSystem.class);
        json.setSerializer(PooledEngine.class, new EngineSerializer<PooledEngine>());
        json.setSerializer(TestSystem.class, new TestSystemSerializer());

        PooledEngine engine = json.fromJson(PooledEngine.class, engineJson);

        assertNotNull(engine);
    }
}