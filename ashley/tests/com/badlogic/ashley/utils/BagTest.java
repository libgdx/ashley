/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badlogic.ashley.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Integer expResult = 22;
                Integer result = instance.remove(2);
                assertEquals(expResult, result);
        }

        /**
         * Test of removeLast method, of class Bag.
         */
        @Test
        public void testRemoveLast() {
                System.out.println("removeLast");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Integer expResult = 7;
                Integer result = instance.removeLast();
                assertEquals(expResult, result);
        }

        /**
         * Test of remove method, of class Bag.
         */
        @Test
        public void testRemove_Object() {
                System.out.println("remove");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Integer expResult = 22;
                boolean result = instance.remove(expResult);
                Assert.assertTrue(result);
                Assert.assertFalse(instance.contains(expResult));
                assertEquals(3, instance.size());
        }

        /**
         * Test of contains method, of class Bag.
         */
        @Test
        public void testContains() {
                System.out.println("contains");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Integer expResult = 22;
                Assert.assertTrue(instance.contains(22));
                Assert.assertFalse(instance.contains(0));
                Assert.assertFalse(instance.contains(null));

        }

        /**
         * Test of get method, of class Bag.
         */
        @Test
        public void testGet() {
                System.out.println("get");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Object result = instance.get(2);
                assertEquals(22, result);
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
                instance.add(12);
                instance.add(10);
                assertEquals(2, instance.size());
                instance.remove(12);
                assertEquals(1, instance.size());
                instance.remove(0);
                assertEquals(0, instance.size());
        }

        /**
         * Test of getCapacity method, of class Bag.
         */
        @Test
        public void testGetCapacity() {
                System.out.println("getCapacity");
                Bag<Integer> instance = new Bag<Integer>();
                assertEquals(64, instance.getCapacity());
        }

        /**
         * Test of isIndexWithinBounds method, of class Bag.
         */
        @Test
        public void testIsIndexWithinBounds() {
                System.out.println("isIndexWithinBounds");
                int index = 0;
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(1);
                instance.add(2);
                boolean expResult = true;
                boolean result = instance.isIndexWithinBounds(index);
                Assert.assertTrue(instance.isIndexWithinBounds(0));
                Assert.assertTrue(instance.isIndexWithinBounds(1));
                Assert.assertFalse(instance.isIndexWithinBounds(64));
        }

        /**
         * Test of isEmpty method, of class Bag.
         */
        @Test
        public void testIsEmpty() {
                System.out.println("isEmpty");
                Bag instance = new Bag();
                boolean expResult = true;
                boolean result = instance.isEmpty();
                assertEquals(expResult, result);
                instance.add(1);
                expResult = false;
                result = instance.isEmpty();
                assertEquals(expResult, result);
                instance.remove(1);
                expResult = true;
                result = instance.isEmpty();
                assertEquals(expResult, result);
        }

        /**
         * Test of add method, of class Bag.
         */
        @Test
        public void testAdd() {
                System.out.println("add");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                Assert.assertTrue(instance.contains(10));
                Assert.assertEquals(1, instance.size());
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Assert.assertTrue(instance.contains(10));
                Assert.assertTrue(instance.contains(12));
        }

        /**
         * Test of set method, of class Bag.
         */
        @Test
        public void testSet() {
                System.out.println("set");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Integer replacement = 15;
                instance.set(1, replacement);
                assertEquals(replacement, instance.get(1));
                instance.set(200, replacement);
                assertEquals(replacement, instance.get(200));
        }

        /**
         * Test of clear method, of class Bag.
         */
        @Test
        public void testClear() {
                System.out.println("clear");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                assertEquals(4, instance.size());
                instance.clear();
                assertEquals(0, instance.size());
        }

        /**
         * Test of toArray method, of class Bag.
         */
        @Test
        public void testToArray_0args() {
                System.out.println("toArray");
                Bag instance = new Bag();
                Object[] expResult = new Object[0];
                Object[] result = instance.toArray();
                assertArrayEquals(expResult, result);
        }

        /**
         * Test of toArray method, of class Bag.
         */
        @Test
        public void testToArray_GenericType() {
                System.out.println("toArray");
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                Number[] nbr = new Number[0];
                nbr = instance.toArray(nbr);
                assertEquals(4, nbr.length);
        }

        /**
         * Test of containsAll method, of class Bag.
         */
        @Test
        public void testContainsAll() {
                System.out.println("containsAll");
                List<Integer> list = new ArrayList<Integer>(Arrays.asList(12, 43, 56, 32));
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                instance.addAll(list);
                Assert.assertTrue(instance.containsAll(list));
        }

        /**
         * Test of addAll method, of class Bag.
         */
        @Test
        public void testAddAll() {
                System.out.println("addAll");
                List<Integer> list = new ArrayList<Integer>(Arrays.asList(12, 43, 56, 32));
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                assertEquals(4, instance.size());
                instance.addAll(list);
                assertEquals(8, instance.size());
        }

        /**
         * Test of removeAll method, of class Bag.
         */
        @Test
        public void testRemoveAll() {
                System.out.println("removeAll");
                List<Integer> list = new ArrayList<Integer>(Arrays.asList(12, 43, 56, 32));
                Bag<Integer> instance = new Bag<Integer>();
                instance.add(10);
                instance.add(12);
                instance.add(22);
                instance.add(7);
                assertEquals(4, instance.size());
                instance.addAll(list);
                assertEquals(8, instance.size());
                instance.removeAll(list);
                assertEquals(3, instance.size());
        }

        /**
         * Test of retainAll method, of class Bag.
         */
        @Test
        public void testRetainAll() {
                System.out.println("retainAll");
                Bag<Integer> instance = new Bag<Integer>();
                ArrayList<Integer> retainList = new ArrayList<Integer>();
                retainList.add(2);
                instance.addAll(1, 2, 2, 3, 4, 5);
//                instance.retainAll(retainList);
                boolean expResult = false;
                boolean result = instance.retainAll(retainList);
                assertEquals(2, instance.size());
        }

}
