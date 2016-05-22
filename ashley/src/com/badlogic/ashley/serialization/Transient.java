package com.badlogic.ashley.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes tagged as Transient won't be serialized.
 *
 * @author David Saltares
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
    Class[] ignore() default {};
}
