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

/**
 * Interface for immutable associative arrays indexed by integers.
 * However, note that the indexed values could be modified if they are mutable.
 * 
 * @author David Saltares
 */
public interface ImmutableIntMap<V> {
	public int size();
	public V get(int key);
	public V get(int key, V defaultValue);
	public boolean containsValue(Object object, boolean identity);
	public boolean containsKey(int key);
	public int findKey(Object object, boolean identity, int notFound);
	
	public Entries<V> immutableEntries();
	public Values<V> immutableValues();
	public Keys immutableKeys();
	
	/**
	 * @author David Saltares
	 *
	 * Immutable entries for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Entries<V> extends Iterable<Entry<Integer, V>> { 
		public void reset();
		public Entry<Integer, V> next();
		public boolean hasNext();

	}
	
	/**
	 * @author David Saltares
	 *
	 * Immutable values for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Values<V>  extends Iterable<V> {
		public void reset();
		public V next();
		public boolean hasNext();
	}
	
	/**
	 * @author David Saltares
	 *
	 * Immutable keys for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Keys extends Iterable<Integer> {
		public void reset();
		public Integer next();
		public boolean hasNext();
	}
}
