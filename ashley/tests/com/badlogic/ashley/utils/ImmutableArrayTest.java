/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badlogic.ashley.utils;

import com.badlogic.gdx.utils.Array;
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
public class ImmutableArrayTest {

        public ImmutableArrayTest() {
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
         * Test of size method, of class ImmutableArray.
         */
        @Test
        public void testSize() {
                System.out.println("size");
                Integer[] intArr = {0, 1, 2, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                int expResult = 5;
                int result = instance.size();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
        }

        /**
         * Test of get method, of class ImmutableArray.
         */
        @Test

        public void testGet() {
                System.out.println("get");
                int index = 0;
                Integer[] intArr = {0, 1, 2, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Integer expResult = 0;
                Object result = instance.get(index);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
        }

        /**
         * Test of contains method, of class ImmutableArray.
         */
        @Test
        public void testContains() {
                System.out.println("contains");
                boolean expResult = true;
                Integer searchedElement = 0;
                Integer[] intArr = {0, 1, 2, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                boolean result = instance.contains(searchedElement, false);
                assertEquals(expResult, result);
        }

        /**
         * Test of indexOf method, of class ImmutableArray.
         */
        @Test
        public void testIndexOf() {
                System.out.println("indexOf");
                Object value = null;
                boolean identity = false;
                Integer[] intArr = {0, 1, 2, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                int expResult = 1;
                int result = instance.indexOf(1, identity);
                assertEquals(expResult, result);
        }

        /**
         * Test of lastIndexOf method, of class ImmutableArray.
         */
        @Test
        public void testLastIndexOf() {
                System.out.println("lastIndexOf");
                Object value = null;
                boolean identity = false;
                ImmutableArray instance = null;
                int expResult = 0;
                int result = instance.lastIndexOf(value, identity);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of peek method, of class ImmutableArray.
         */
        @Test
        public void testPeek() {
                System.out.println("peek");
                ImmutableArray instance = null;
                Object expResult = null;
                Object result = instance.peek();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of first method, of class ImmutableArray.
         */
        @Test
        public void testFirst() {
                System.out.println("first");
                ImmutableArray instance = null;
                Object expResult = null;
                Object result = instance.first();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of random method, of class ImmutableArray.
         */
        @Test
        public void testRandom() {
                System.out.println("random");
                ImmutableArray instance = null;
                Object expResult = null;
                Object result = instance.random();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toArray method, of class ImmutableArray.
         */
        @Test
        public void testToArray_0args() {
                System.out.println("toArray");
                ImmutableArray instance = null;
                Object[] expResult = null;
                Object[] result = instance.toArray();
                assertArrayEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toArray method, of class ImmutableArray.
         */
        @Test
        public void testToArray_Class() {
                System.out.println("toArray");
                Class type = null;
                ImmutableArray instance = null;
                Object[] expResult = null;
                Object[] result = instance.toArray(type);
                assertArrayEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of equals method, of class ImmutableArray.
         */
        @Test
        public void testEquals() {
                System.out.println("equals");
                Object object = null;
                ImmutableArray instance = null;
                boolean expResult = false;
                boolean result = instance.equals(object);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toString method, of class ImmutableArray.
         */
        @Test
        public void testToString_0args() {
                System.out.println("toString");
                ImmutableArray instance = null;
                String expResult = "";
                String result = instance.toString();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of toString method, of class ImmutableArray.
         */
        @Test
        public void testToString_String() {
                System.out.println("toString");
                String separator = "";
                ImmutableArray instance = null;
                String expResult = "";
                String result = instance.toString(separator);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

        /**
         * Test of iterator method, of class ImmutableArray.
         */
        @Test
        public void testIterator() {
                System.out.println("iterator");
                ImmutableArray instance = null;
                Iterator expResult = null;
                Iterator result = instance.iterator();
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                fail("The test case is a prototype.");
        }

}
