package com.badlogic.ashley.serialization;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David Saltares
 */
public class TransientCheckerTests {

    @Transient
    public static class TransientTest {}

    public static class NonTransientTest {}

    @Test
    public void noTransient() {
        TransientChecker checker = new TransientChecker();
        assertFalse(checker.isTransient(NonTransientTest.class));
    }

    @Test
    public void transientTest() {
        TransientChecker checker = new TransientChecker();
        assertTrue(checker.isTransient(TransientTest.class));
    }
}