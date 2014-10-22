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
import org.junit.Assert;
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
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                int expResult = 2;
                int result = instance.lastIndexOf(1, identity);
                assertEquals(expResult, result);
        }

        /**
         * Test of peek method, of class ImmutableArray.
         */
        @Test
        public void testPeek() {
                System.out.println("peek");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Object expResult = 4;
                Object result = instance.peek();
                assertEquals(expResult, result);
        }

        /**
         * Test of first method, of class ImmutableArray.
         */
        @Test
        public void testFirst() {
                System.out.println("first");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Object expResult = 0;
                Object result = instance.first();
                assertEquals(expResult, result);
        }

        /**
         * Test of random method, of class ImmutableArray.
         */
        @Test
        public void testRandom() {
                System.out.println("random");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Object expResult = null;
                Object result = instance.random();
                Assert.assertNotNull(result);
                intArr = new Integer[0];
                instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                result = instance.random();
                Assert.assertNull(result);
        }

        /**
         * Test of toArray method, of class ImmutableArray.
         */
        @Test
        public void testToArray_0args() {
                System.out.println("toArray");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Object[] expResult = {0, 1, 1, 3, 4};
                Object[] result = instance.toArray();
                assertArrayEquals(expResult, result);
        }

        /**
         * Test of toArray method, of class ImmutableArray.
         */
        @Test
        public void testToArray_Class() {
                System.out.println("toArray");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Number[] expResult = {0, 1, 1, 3, 4};
                Object[] result = instance.toArray(Number.class);
                assertArrayEquals(expResult, result);
        }

        /**
         * Test of equals method, of class ImmutableArray.
         */
        @Test
        public void testEquals() {
                System.out.println("equals");
                Integer[] intArr1 = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance1 = new ImmutableArray<Integer>(new Array<Integer>(intArr1));
                ImmutableArray<Integer> instance2 = new ImmutableArray<Integer>(new Array<Integer>(intArr1));
                boolean expResult = true;
                boolean result = instance1.equals(instance2);
                assertEquals(expResult, result);
                Integer[] intArr2 = {0, 1};
                instance2 = new ImmutableArray<Integer>(new Array<Integer>(intArr2));
                expResult = false;
                result = instance1.equals(instance2);
                assertEquals(expResult, result);
                Integer something = 3;
                assertEquals(false, instance1.equals(something));
        }

        /**
         * Test of toString method, of class ImmutableArray.
         */
        @Test

        public void testToString_0args() {
                System.out.println("toString");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                String expResult = "[0, 1, 1, 3, 4]";
                String result = instance.toString();
                assertEquals(expResult, result);
        }

        /**
         * Test of toString method, of class ImmutableArray.
         */
        @Test
        public void testToString_String() {
                System.out.println("toString");
                String separator = "|";
                System.out.println("toString");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                String expResult = "0|1|1|3|4";
                String result = instance.toString(separator);
                assertEquals(expResult, result);
        }

        /**
         * Test of iterator method, of class ImmutableArray.
         */
        @Test
        public void testIterator() {
                System.out.println("iterator");
                Integer[] intArr = {0, 1, 1, 3, 4};
                ImmutableArray<Integer> instance = new ImmutableArray<Integer>(new Array<Integer>(intArr));
                Iterator iterator = instance.iterator();
                int i = 0;
                for (Integer it : instance) {
                        ++i;
                }
                assertEquals(intArr.length, i);
                iterator = instance.iterator();
                try {
                        iterator.remove();
                        fail("Expected expection didn't occure");
                } catch (UnsupportedOperationException e) {

                }
        }

}
