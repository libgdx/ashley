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

import java.util.Iterator;

import org.junit.Test;

import com.badlogic.ashley.utils.ImmutableIntMap;
import com.badlogic.ashley.utils.ImmutableIntMap.ImmutableEntries;
import com.badlogic.ashley.utils.ImmutableIntMap.ImmutableValues;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

public class ImmutableIntMapTests {

	@Test
	public void basicInterface() {
		int numElements = 10;
		
		IntMap<Integer> map = new IntMap<Integer>();
		
		for (int i = 0; i < numElements; ++i) {
			map.put(i, i);
		}
		
		ImmutableIntMap<Integer> immutable = new ImmutableIntMap<Integer>(map);
		
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
		
		ImmutableIntMap<Integer> immutable = new ImmutableIntMap<Integer>(map);
		
		ImmutableEntries<Integer> entries = immutable.entries();
		for (Entry<Integer> entry : entries) {
			assertEquals((int)entry.key, (int)entry.value);
		}
		
		ImmutableValues<Integer> values = immutable.values();
		int counter = 0;
		for (Integer value : values) {
			assertEquals(counter++, value.intValue());
		}
	}
		
	@Test
	public void forbiddenRemove() {
		IntMap<Integer> map = new IntMap<Integer>();
		map.put(0, 0);

		ImmutableIntMap<Integer> immutable = new ImmutableIntMap<Integer>(map);
		
		ImmutableEntries<Integer> entries = immutable.entries();
		boolean entriesThrown = false;
		
		try {
			Iterator<Entry<Integer>> it = entries.iterator();
			it.next();
			it.remove();
		} catch (GdxRuntimeException e) {
			entriesThrown = true;
		}
		
		assertTrue(entriesThrown);
		
		ImmutableValues<Integer> values = immutable.values();
		
		boolean valuesThrown = false;
		
		try {
			Iterator<Integer> it = values.iterator();
			it.next();
			it.remove();
		} catch (GdxRuntimeException e) {
			valuesThrown = true;
		}
		
		assertTrue(valuesThrown);
	}
}
