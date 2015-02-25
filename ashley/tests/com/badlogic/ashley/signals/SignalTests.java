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

package com.badlogic.ashley.signals;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;

public class SignalTests {

	private static class Dummy {

	}

	private static class ListenerMock implements Listener<Dummy> {

		public int count = 0;

		@Override
		public void receive (Signal<Dummy> signal, Dummy object) {
			++count;

			assertNotNull(signal);
			assertNotNull(object);
		}
	}

	private static class RemoveWhileDispatchListenerMock implements Listener<Dummy> {
		public int count = 0;

		@Override
		public void receive (Signal<Dummy> signal, Dummy object) {
			++count;
			signal.remove(this);
		}
	}

	@Test
	public void addListenerAndDispatch () {
		Dummy dummy = new Dummy();
		Signal<Dummy> signal = new Signal<Dummy>();
		ListenerMock listener = new ListenerMock();
		signal.add(listener);

		for (int i = 0; i < 10; ++i) {
			assertEquals(i, listener.count);
			signal.dispatch(dummy);
			assertEquals(i + 1, listener.count);
		}
	}

	@Test
	public void addListenersAndDispatch () {
		Dummy dummy = new Dummy();
		Signal<Dummy> signal = new Signal<Dummy>();
		Array<ListenerMock> listeners = new Array<ListenerMock>();

		int numListeners = 10;

		while (listeners.size < numListeners) {
			ListenerMock listener = new ListenerMock();
			listeners.add(listener);
			signal.add(listener);
		}

		int numDispatchs = 10;

		for (int i = 0; i < numDispatchs; ++i) {
			for (ListenerMock listener : listeners) {
				assertEquals(i, listener.count);
			}

			signal.dispatch(dummy);

			for (ListenerMock listener : listeners) {
				assertEquals(i + 1, listener.count);
			}
		}
	}

	@Test
	public void addListenerDispatchAndRemove () {
		Dummy dummy = new Dummy();
		Signal<Dummy> signal = new Signal<Dummy>();
		ListenerMock listenerA = new ListenerMock();
		ListenerMock listenerB = new ListenerMock();

		signal.add(listenerA);
		signal.add(listenerB);

		int numDispatchs = 5;

		for (int i = 0; i < numDispatchs; ++i) {
			assertEquals(i, listenerA.count);
			assertEquals(i, listenerB.count);

			signal.dispatch(dummy);

			assertEquals(i + 1, listenerA.count);
			assertEquals(i + 1, listenerB.count);
		}

		signal.remove(listenerB);

		for (int i = 0; i < numDispatchs; ++i) {
			assertEquals(i + numDispatchs, listenerA.count);
			assertEquals(numDispatchs, listenerB.count);

			signal.dispatch(dummy);

			assertEquals(i + 1 + numDispatchs, listenerA.count);
			assertEquals(numDispatchs, listenerB.count);
		}
	}

	@Test
	public void removeWhileDispatch () {
		Dummy dummy = new Dummy();
		Signal<Dummy> signal = new Signal<Dummy>();
		RemoveWhileDispatchListenerMock listenerA = new RemoveWhileDispatchListenerMock();
		ListenerMock listenerB = new ListenerMock();

		signal.add(listenerA);
		signal.add(listenerB);

		signal.dispatch(dummy);

		assertEquals(1, listenerA.count);
		assertEquals(1, listenerB.count);
	}

	@Test
	public void removeAll () {
		Dummy dummy = new Dummy();
		Signal<Dummy> signal = new Signal<Dummy>();

		ListenerMock listenerA = new ListenerMock();
		ListenerMock listenerB = new ListenerMock();

		signal.add(listenerA);
		signal.add(listenerB);

		signal.dispatch(dummy);

		assertEquals(1, listenerA.count);
		assertEquals(1, listenerB.count);

		signal.removeAllListeners();

		signal.dispatch(dummy);

		assertEquals(1, listenerA.count);
		assertEquals(1, listenerB.count);
	}

}
