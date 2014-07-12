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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.IntMap.Keys;

/**
 * Wrapper class to treat {@link IntMap} objects as if they were immutable.
 * However, note that the indexed values could be modified if they are mutable.
 * 
 * @author David Saltares
 */
public class ImmutableIntMap<V> implements Iterable<IntMap.Entry<V>> {
	private final IntMap<V> map;
	private ImmutableEntries entries;
	private ImmutableValues values;
	private ImmutableKeys keys;
	
	public ImmutableIntMap(IntMap<V> map) {
		this.map = map;
	}
	
	public int size() {
		return map.size;
	}
	
	public V get(int key) {
		return map.get(key);
	}
	
	public V get(int key, V defaultValue) {
		return map.get(key, defaultValue);
	}
	
	public boolean containsValue(Object object, boolean identity) {
		return map.containsValue(object, identity);
	}
	
	public boolean containsKey(int key) {
		return map.containsKey(key);
	}
	
	public int findKey(Object object, boolean identity, int notFound) {
		return map.findKey(object, identity, notFound);
	}
	
	public String toString () {
		return map.toString();
	}
	
	/** Returns an iterator for the entries in the map. Calling {@link Entries#remove()} will throw an
	 * {@link GdxRuntimeException}. Note that the same iterator instance is returned each
	 * time this method is called.*/
	public ImmutableEntries<V> entries() {
		if (entries == null) {
			entries = new ImmutableEntries(map.entries());
		}
		
		entries.reset();
		return entries;
	}
	
	/** Returns an iterator for the values in the map. Calling {@link Values#remove()} will throw an
	 * {@link GdxRuntimeException}. Note that the same iterator instance is returned each
	 * time this method is called.*/
	public ImmutableValues<V> values() {
		if (values == null) {
			values = new ImmutableValues(map.values());
		}
		
		values.reset();
		return values;
	}
	
	/** Returns an iterator for the keys in the map. Calling {@link Keys#remove()} will throw an
	 * {@link GdxRuntimeException}. Note that the same iterator instance is returned each time
	 * this method is called. */
	public ImmutableKeys keys() {
		if (keys == null) {
			keys = new ImmutableKeys(map.keys());
		}
	
		return keys;
	}
	
	public Iterator<Entry<V>> iterator () {
		return entries();
	}
	
	static public class ImmutableEntries<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {
		private final Entries<V> entries;

		public ImmutableEntries (Entries<V> entries) {
			this.entries = entries;
		}
		
		public void reset () {
			entries.reset();
		}

		public void remove () {
			throw new GdxRuntimeException("Remove not allowed.");
		}

		public boolean hasNext () {
			return entries.hasNext();
		}

		public Entry<V> next () {
			return entries.next();
		}

		public Iterator<Entry<V>> iterator () {
			return this;
		}
	}
	
	static public class ImmutableValues<V> implements Iterable<V>, Iterator<V>  {
		private final Values<V> values;
		
		public ImmutableValues (Values<V> values) {
			this.values = values;
		}

		public void reset () {
			values.reset();
		}

		public void remove () {
			throw new GdxRuntimeException("Remove not allowed.");
		}

		public boolean hasNext () {
			return values.hasNext();
		}

		public V next () {
			return values.next();
		}

		public Iterator<V> iterator () {
			return this;
		}
	}
	
	static public class ImmutableKeys  {
		private final Keys keys;
		
		public ImmutableKeys (Keys keys) {
			this.keys = keys;
		}
		
		public void reset () {
			keys.reset();
		}

		public int next () {
			return keys.next();
		}

		public IntArray toArray () {
			return keys.toArray();
		}
	}
}
