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

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.utils.Entry;
import com.badlogic.ashley.utils.ImmutableIntMap;
import com.badlogic.ashley.utils.IntMap;
import com.badlogic.ashley.utils.ImmutableIntMap.Entries;
import com.badlogic.ashley.utils.ImmutableIntMap.Keys;
import com.badlogic.ashley.utils.ImmutableIntMap.Values;

public class ImmutableIntMapTests {

	@Test
	public void basicInterface() {
		int numElements = 10;
		
		IntMap<Integer> map = new IntMap<Integer>();
		
		for (int i = 0; i < numElements; ++i) {
			map.put(i, i);
		}
		
		ImmutableIntMap<Integer> immutable = map;
		
		assertEquals(numElements, immutable.size());
		assertTrue(immutable.containsKey(0));
		assertFalse(immutable.containsKey(10));
		assertTrue(immutable.containsValue(0, false));
		assertFalse(immutable.containsValue(10, false));
		assertNotEquals(-1, immutable.findKey(0, false, -1));
		assertEquals(-1, immutable.findKey(10, false, -1));
		
		for (int i = 0; i < numElements; ++i) {
			assertEquals(i, immutable.get(i, -1).intValue());
		}
	}
	
	@Test
	public void iterators() {
		int numElements = 10;
		
		IntMap<Integer> map = new IntMap<Integer>();
		
		for (int i = 0; i < numElements; ++i) {
			map.put(i, i);
		}
		
		ImmutableIntMap<Integer> immutable = map;
		
		Entries<Integer> entries = immutable.immutableEntries();
		for (Entry<Integer, Integer> entry : entries) {
			assertEquals(entry.key, entry.value);
		}
		
		Values<Integer> values = immutable.immutableValues();
		int counter = 0;
		for (Integer value : values) {
			assertEquals(counter++, value.intValue());
		}
		
		Keys keys = immutable.immutableKeys();
		for (Integer key : keys) {
			assertEquals(map.get(key), key);
		}
	}
		
	@Test
	public void forbiddenRemove() {
		IntMap<Integer> map = new IntMap<Integer>();
		map.put(0, 0);

		ImmutableIntMap<Integer> immutable = map;
		
		Entries<Integer> entries = immutable.immutableEntries();
		boolean entriesThrown = false;
		
		try {
			entries.iterator().remove();
		} catch (UnsupportedOperationException e) {
			entriesThrown = true;
		}
		
		assertTrue(entriesThrown);
		
		Values<Integer> values = immutable.immutableValues();
		
		boolean valuesThrown = false;
		
		try {
			values.iterator().remove();
		} catch (UnsupportedOperationException e) {
			valuesThrown = true;
		}
		
		assertTrue(valuesThrown);
		
		Keys keys = immutable.immutableKeys();
		
		boolean keysThrown = false;
		
		try {
			keys.iterator().remove();
		} catch (UnsupportedOperationException e) {
			keysThrown = true;
		}
		
		assertTrue(keysThrown);
	}
}
