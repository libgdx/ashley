/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badlogic.ashley.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jopa
 */
public class BagTest {

        public BagTest() {
        }

        @BeforeClass
        public static void setUpClass() {
        }

        @AfterClass
        public static void tearDownClass() {
        }

        @Before
        public void setUp() {
        }

        @After
        public void tearDown() {
        }

        /**
         * Test of remove method, of class Bag.
         */
        @Test
        public void testRemove_int() {
                System.out.println("remove");
                int index = 0;
                Bag instance = new Bag();
                Object expResult = null;
                Object result = instance.remove(index);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of removeLast method, of class Bag.
         */
        @Test
        public void testRemoveLast() {
                System.out.println("removeLast");
                Bag instance = new Bag();
                Object expResult = null;
                Object result = instance.removeLast();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of remove method, of class Bag.
         */
        @Test
        public void testRemove_Object() {
                System.out.println("remove");
                Object e = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.remove(e);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of contains method, of class Bag.
         */
        @Test
        public void testContains() {
                System.out.println("contains");
                Object e = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.contains(e);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of get method, of class Bag.
         */
        @Test
        public void testGet() {
                System.out.println("get");
                int index = 0;
                Bag instance = new Bag();
                Object expResult = null;
                Object result = instance.get(index);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of size method, of class Bag.
         */
        @Test
        public void testSize() {
                System.out.println("size");
                Bag instance = new Bag();
                int expResult = 0;
                int result = instance.size();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of getCapacity method, of class Bag.
         */
        @Test
        public void testGetCapacity() {
                System.out.println("getCapacity");
                Bag instance = new Bag();
                int expResult = 0;
                int result = instance.getCapacity();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of isIndexWithinBounds method, of class Bag.
         */
        @Test
        public void testIsIndexWithinBounds() {
                System.out.println("isIndexWithinBounds");
                int index = 0;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.isIndexWithinBounds(index);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of isEmpty method, of class Bag.
         */
        @Test
        public void testIsEmpty() {
                System.out.println("isEmpty");
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.isEmpty();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of add method, of class Bag.
         */
        @Test
        public void testAdd() {
                System.out.println("add");
                Object e = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.add(e);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of set method, of class Bag.
         */
        @Test
        public void testSet() {
                System.out.println("set");
                int index = 0;
                Object e = null;
                Bag instance = new Bag();
                Object expResult = null;
                Object result = instance.set(index, e);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of clear method, of class Bag.
         */
        @Test
        public void testClear() {
                System.out.println("clear");
                Bag instance = new Bag();
                instance.clear();
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of iterator method, of class Bag.
         */
        @Test
        public void testIterator() {
                System.out.println("iterator");
                Bag instance = new Bag();
                Iterator expResult = null;
                Iterator result = instance.iterator();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toArray method, of class Bag.
         */
        @Test
        public void testToArray_0args() {
                System.out.println("toArray");
                Bag instance = new Bag();
                Object[] expResult = null;
                Object[] result = instance.toArray();
                assertArrayEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toArray method, of class Bag.
         */
        @Test
        public void testToArray_GenericType() {
                System.out.println("toArray");
                Object[] a = null;
                Bag instance = new Bag();
                Object[] expResult = null;
                Object[] result = instance.toArray(a);
                assertArrayEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of containsAll method, of class Bag.
         */
        @Test
        public void testContainsAll() {
                System.out.println("containsAll");
                Collection c = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.containsAll(c);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of addAll method, of class Bag.
         */
        @Test
        public void testAddAll() {
                System.out.println("addAll");
                Collection c = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.addAll(c);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of removeAll method, of class Bag.
         */
        @Test
        public void testRemoveAll() {
                System.out.println("removeAll");
                Collection c = null;
                Bag instance = new Bag();
                boolean expResult = false;
                boolean result = instance.removeAll(c);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of retainAll method, of class Bag.
         */
        @Test
        public void testRetainAll() {
                System.out.println("retainAll");
                Collection c = null;
                Bag<Integer> instance = new Bag<Integer>();
                ArrayList<Integer> retainList = new ArrayList<Integer>();
                retainList.add(2);
                instance.addAll(1, 2, 2, 3, 4, 5);
                instance.retainAll(retainList);
                boolean expResult = false;
                boolean result = instance.retainAll(c);
                assertEquals(2, instance.size());
        }

}
