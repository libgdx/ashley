/**
 * *****************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
package com.badlogic.ashley.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Fast collection similar to Array that grows on demand as elements are
 * accessed. It does not preserve order of elements.
 *
 * Inspired by Artemis Bag.
 */
public class Bag<E> implements Collection<E> {

        private E[] data;
        private int size = 0;

        /**
         * Empty Bag with an initial capacity of 64.
         */
        public Bag() {
                this(64);
        }

        /**
         * Empty Bag with the specified initial capacity.
         *
         * @param capacity the initial capacity of Bag.
         */
        @SuppressWarnings("unchecked")
        public Bag(int capacity) {
                data = (E[]) new Object[capacity];
        }

        /**
         * Removes the element at the specified position in this Bag. Order of
         * elements is not preserved.
         *
         * @param index
         * @return element that was removed from the Bag.
         */
        public E remove(int index) {
                E e = data[index]; // make copy of element to remove so it can be returned
                data[index] = data[--size]; // overwrite item to remove with last element
                data[size] = null; // null last element, so gc can do its work
                return e;
        }

        /**
         * Removes and return the last object in the bag.
         *
         * @return the last object in the bag, null if empty.
         */
        public E removeLast() {
                if (size > 0) {
                        E e = data[--size];
                        data[size] = null;
                        return e;
                }

                return null;
        }

        /**
         * Removes the first occurrence of the specified element from this Bag,
         * if it is present. If the Bag does not contain the element, it is
         * unchanged. It does not preserve order of elements.
         *
         * @param e
         * @return true if the element was removed.
         */
        @Override
        public boolean remove(Object e) {
                for (int i = 0; i < size; i++) {
                        E e2 = data[i];

                        if (e == e2) {
                                data[i] = data[--size]; // overwrite item to remove with last element
                                data[size] = null; // null last element, so gc can do its work
                                return true;
                        }
                }

                return false;
        }

        /**
         * Check if bag contains this element. The operator == is used to check
         * for equality.
         *
         * @return true, if the Object is found in the bag
         */
        public boolean contains(Object e) {
                for (int i = 0; size > i; i++) {
                        if (e == data[i]) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * @return the element at the specified position in Bag.
         */
        public E get(int index) {
                return data[index];
        }

        /**
         * @return the number of elements in this bag.
         */
        public int size() {
                return size;
        }

        /**
         * @return the number of elements the bag can hold without growing.
         */
        public int getCapacity() {
                return data.length;
        }

        /**
         * @param index
         * @return whether or not the index is within the bounds of the
         * collection
         */
        public boolean isIndexWithinBounds(int index) {
                return index < getCapacity();
        }

        /**
         * @return true if this list contains no elements
         */
        public boolean isEmpty() {
                return size == 0;
        }

        /**
         * Adds the specified element to the end of this bag. if needed also
         * increases the capacity of the bag.
         *
         * @return true, if the add operation ended succesfully
         */
        @Override
        public boolean add(E e) {
                // is size greater than capacity increase capacity
                if (size == data.length) {
                        grow();
                }
                data[size++] = e;
                return true;
        }

        /**
         * Set element at specified index in the bag.
         */
        public void set(int index, E e) {
                if (index >= data.length) {
                        grow(index * 2);
                }
                size = index + 1;
                data[index] = e;
        }

        /**
         * Removes all of the elements from this bag. The bag will be empty
         * after this call returns.
         */
        public void clear() {
                // null all elements so gc can clean up
                for (int i = 0; i < size; i++) {
                        data[i] = null;
                }
                size = 0;
        }

        private void grow() {
                int newCapacity = (data.length * 3) / 2 + 1;
                grow(newCapacity);
        }

        @SuppressWarnings("unchecked")
        private void grow(int newCapacity) {
                E[] oldData = data;
                data = (E[]) new Object[newCapacity];
                System.arraycopy(oldData, 0, data, 0, oldData.length);
        }

        @Override
        public Iterator<E> iterator() {

                return new Iterator<E>() {
                        private int counter;

                        @Override
                        public boolean hasNext() {
                                if (counter < data.length) {
                                        return true;
                                } else {
                                        return false;
                                }
                        }

                        @Override
                        public E next() {
                                if (!hasNext()) {
                                        throw new NoSuchElementException();
                                } else {
                                        return data[counter++];
                                }
                        }
                };
        }

        @Override
        public Object[] toArray() {
                return data;
        }

        @Override
        public <T> T[] toArray(T[] a) {
                T[] array = (T[]) Array.newInstance(a.getClass(), data.length);
                System.arraycopy(data, 0, array, 0, size);
                return array;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
                for (Object contains : c) {
                        if (!this.contains(contains)) {
                                return false;
                        }
                }
                return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
                int newSize = this.data.length + c.size();
                if (this.size < newSize) {
                        grow(newSize);
                }
                for (E entity : c) {
                        if (!this.add(entity)) {
                                return false;
                        }
                }
                return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
                for (Object toRemove : c) {
                        if (!remove(toRemove)) {
                                return false;
                        }
                }
                return true;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

}
