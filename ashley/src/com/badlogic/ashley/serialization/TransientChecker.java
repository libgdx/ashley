package com.badlogic.ashley.serialization;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;

/**
 * @author David Saltares
 */
public class TransientChecker {
    private ObjectMap<Class, Boolean> cache = new ObjectMap<Class, Boolean>();


    public boolean isTransient(Class type) {
        if (!cache.containsKey(type)) {
            Annotation annotation = ClassReflection.getAnnotation(type, Transient.class);
            cache.put(type, annotation != null);
        }

        return cache.get(type);
    }
}
