package com.badlogic.ashley.serialization;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntitySerializerTests {

    public static class TestComponentA implements Component {
        public int value;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestComponentA that = (TestComponentA) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    public static class TestComponentB implements Component {
        public String value;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestComponentB that = (TestComponentB) o;

            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    @Transient
    public static class TransientComponent implements Component {

    }

    private final String serializedEntity = "{components:{com.badlogic.ashley.serialization.EntitySerializerTests$TestComponentA:{value:5},com.badlogic.ashley.serialization.EntitySerializerTests$TestComponentB:{value:test}}}";
    private final String serializedWithTags = "{components:{TestComponentA:{value:5},TestComponentB:{value:test}}}";

    @Test
    public void write() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);

        TestComponentA c1 = new TestComponentA();
        TestComponentB c2 = new TestComponentB();
        TransientComponent c3 = new TransientComponent();

        c1.value = 5;
        c2.value = "test";

        Entity entity = new Entity();
        entity.add(c1);
        entity.add(c2);
        entity.add(c3);

        assertEquals(serializedEntity, json.toJson(entity));
    }

    @Test
    public void read() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);

        Entity entity = json.fromJson(Entity.class, serializedEntity);

        assertNotNull(entity);
        assertEquals(2, entity.getComponents().size());
        assertEquals(5, entity.getComponent(TestComponentA.class).value);
        assertEquals("test", entity.getComponent(TestComponentB.class).value);
    }

    @Test
    public void readComponentTags() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);
        json.addClassTag("TestComponentA", TestComponentA.class);
        json.addClassTag("TestComponentB", TestComponentB.class);


        Entity entity = json.fromJson(Entity.class, serializedWithTags);

        assertNotNull(entity);
        assertEquals(2, entity.getComponents().size());
        assertEquals(5, entity.getComponent(TestComponentA.class).value);
        assertEquals("test", entity.getComponent(TestComponentB.class).value);
    }

    @Test
    public void writeComponentTags() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);
        json.addClassTag("TestComponentA", TestComponentA.class);
        json.addClassTag("TestComponentB", TestComponentB.class);

        TestComponentA c1 = new TestComponentA();
        TestComponentB c2 = new TestComponentB();
        TransientComponent c3 = new TransientComponent();

        c1.value = 5;
        c2.value = "test";

        Entity entity = new Entity();
        entity.add(c1);
        entity.add(c2);
        entity.add(c3);

        assertEquals(serializedWithTags, json.toJson(entity));
    }

    @Test
    public void writeAndRead() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);

        TestComponentA c1 = new TestComponentA();
        TestComponentB c2 = new TestComponentB();
        TransientComponent c3 = new TransientComponent();

        c1.value = 5;
        c2.value = "test";

        Entity entity = new Entity();
        entity.add(c1);
        entity.add(c2);
        entity.add(c3);

        String serialized = json.toJson(entity);

        Entity newEntity = json.fromJson(Entity.class, serialized);
        assertNotNull(newEntity);
        assertEquals(2, newEntity.getComponents().size());
        assertEquals(entity.getComponent(TestComponentA.class), newEntity.getComponent(TestComponentA.class));
        assertEquals(entity.getComponent(TestComponentB.class), newEntity.getComponent(TestComponentB.class));
    }

    @Test
    public void readComponentNotFound() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);

        String serialized = "{components:{com.whatever.InvalidComponent:{}}}";
        Entity entity = json.fromJson(Entity.class, serialized);
        assertNull(entity);
    }

    @Test(expected = SerializationException.class)
    public void readInvalidJson() {
        EntitySerializer serializer = new EntitySerializer();

        Json json = new Json();
        json.setSerializer(Entity.class, serializer);

        String serialized = "{";
        Entity entity = json.fromJson(Entity.class, serialized);
    }
}