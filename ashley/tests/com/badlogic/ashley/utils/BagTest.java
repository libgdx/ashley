package com.badlogic.ashley.utils;

import org.junit.Assert;
import org.junit.Test;

public class BagTest {
	
	@Test
	public void testSet(){
		final Bag<String> bag = new Bag<String>();
		bag.add("a");
		bag.add("b");
		bag.add("c");
		Assert.assertEquals(3, bag.size());
		
		bag.set(1, "d");
		Assert.assertEquals(3, bag.size());
		Assert.assertEquals("d", bag.get(1));
	}
}
