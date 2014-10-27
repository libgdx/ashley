/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.utils;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;


/**
 * Wrapper class to treat {@link Array} objects as if they were immutable.
 * However, note that the values could be modified if they are mutable.
 * 
 * @author David Saltares
 */
public class ImmutableArray<T> implements Iterable<T> {
	private final Array<T> array;
    private Array.ArrayIterator<T> immutableIterator;
	
	public ImmutableArray(Array<T> array) {
		this.array = array;
	}
	
	public int size() {
		return array.size;
	}
	
	public T get(int index) {
		return array.get(index);
	}
	
	public boolean contains(T value, boolean identity) {
		return array.contains(value, identity);
	}
	
	public int indexOf(T value, boolean identity) {
		return array.indexOf(value, identity);
	}
	
	public int lastIndexOf(T value, boolean identity) {
		return array.lastIndexOf(value, identity);
	}
	
	public T peek() {
		return array.peek();
	}
	
	public T first() {
		return array.first();
	}
	
	public T random () {
		return array.random();
	}
	
	public T[] toArray () {
		return array.toArray();
	}

	public <V> V[] toArray (Class<?> type) {
		return array.toArray(type);
	}

	public boolean equals (Object object) {
		return array.equals(object);
	}

	public String toString () {
		return array.toString();
	}

	public String toString (String separator) {
		return array.toString(separator); 
	}

    @Override
    public Iterator<T> iterator() {
        if (immutableIterator == null) immutableIterator = new Array.ArrayIterator<T>(array, false);
        immutableIterator.reset();
        return immutableIterator;
    }
}
