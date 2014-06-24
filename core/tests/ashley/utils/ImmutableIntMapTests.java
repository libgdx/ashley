package ashley.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import ashley.utils.ImmutableIntMap.Entries;
import ashley.utils.ImmutableIntMap.Keys;
import ashley.utils.ImmutableIntMap.Values;

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
