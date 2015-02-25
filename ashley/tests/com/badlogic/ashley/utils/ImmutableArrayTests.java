
package com.badlogic.ashley.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ImmutableArrayTests {

	@Test
	public void sameValues () {
		Array<Integer> array = new Array<Integer>();
		ImmutableArray<Integer> immutable = new ImmutableArray<Integer>(array);

		assertEquals(array.size, immutable.size());

		for (int i = 0; i < 10; ++i) {
			array.add(i);
		}

		assertEquals(array.size, immutable.size());

		for (int i = 0; i < array.size; ++i) {
			assertEquals(array.get(i), immutable.get(i));
		}
	}

	@Test
	public void iteration () {
		Array<Integer> array = new Array<Integer>();
		ImmutableArray<Integer> immutable = new ImmutableArray<Integer>(array);

		for (int i = 0; i < 10; ++i) {
			array.add(i);
		}

		Integer expected = 0;
		for (Integer value : immutable) {
			assertEquals(expected++, value);
		}
	}

	@Test
	public void forbiddenRemoval () {
		Array<Integer> array = new Array<Integer>();
		ImmutableArray<Integer> immutable = new ImmutableArray<Integer>(array);

		for (int i = 0; i < 10; ++i) {
			array.add(i);
		}

		boolean thrown = false;

		try {
			immutable.iterator().remove();
		} catch (GdxRuntimeException e) {
			thrown = true;
		}

		assertEquals(true, thrown);
	}
}
