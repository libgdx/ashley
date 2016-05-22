package com.badlogic.ashley.serialization;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import org.junit.Test;

import static org.junit.Assert.*;


public class TagResolverTests {
    public static class SomeClass{}

    @Test
    public void getClassNoTag() throws ReflectionException {
        Json json = new Json();
        Class type = TagResolver.getClass(json, SomeClass.class.getName());
        assertEquals(SomeClass.class, type);
    }

    @Test
    public void getClassTag() throws ReflectionException {
        Json json = new Json();
        json.addClassTag("SomeClass", SomeClass.class);
        Class type = TagResolver.getClass(json, "SomeClass");
        assertEquals(SomeClass.class, type);
    }

    @Test(expected = ReflectionException.class)
    public void getClassNotFound() throws ReflectionException {
        Json json = new Json();
        Class type = TagResolver.getClass(json, "Whatever");
    }

    @Test
    public void getTagNoTag() {
        Json json = new Json();
        String tag = TagResolver.getTag(json, SomeClass.class);
        assertEquals(SomeClass.class.getName(), tag);
    }

    @Test
    public void getTagTag() {
        Json json = new Json();
        json.addClassTag("SomeClass", SomeClass.class);
        String tag = TagResolver.getTag(json, SomeClass.class);
        assertEquals("SomeClass", tag);
    }
}