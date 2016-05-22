package com.badlogic.ashley.serialization;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * Utility to resolve classes and tag names using {@link Json}.
 *
 * @author David Saltares
 */
public class TagResolver {
    /**
     * Tries to get the class object by {@link Json} tag, if there
     * is no tag with that name, it fallsback to fully qualified name.
     *
     * @throws ReflectionException
     */
    public static Class getClass(Json json, String name) throws ReflectionException {
        Class type = json.getClass(name);
        return type != null ? type : ClassReflection.forName(name);
    }

    /**
     * Tries to return the tag for a type according to the {@link Json}
     * object. If there is none, it uses the fully qualified name.
     */
    public static String getTag(Json json, Class type) {
        String tag = json.getTag(type);
        return tag != null ? tag : type.getName();
    }
}
