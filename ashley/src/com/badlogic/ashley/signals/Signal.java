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

import com.badlogic.gdx.utils.SnapshotArray;

/**
 * A Signal is a basic event class then can dispatch an event to multiple listeners. It uses generics to allow any type of object
 * to be passed around on dispatch.
 * @author Stefan Bachmann
 */
public class Signal<T> {
	private SnapshotArray<Listener<T>> listeners;

	public Signal () {
		listeners = new SnapshotArray<Listener<T>>();
	}

	/**
	 * Add a Listener to this Signal
	 * @param listener The Listener to be added
	 */
	public void add (Listener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from this Signal
	 * @param listener The Listener to remove
	 */
	public void remove (Listener<T> listener) {
		listeners.removeValue(listener, true);
	}

	/** Removes all listeners attached to this {@link Signal}. */
	public void removeAllListeners () {
		listeners.clear();
	}

	/**
	 * Dispatches an event to all Listeners registered to this Signal
	 * @param object The object to send off
	 */
	public void dispatch (T object) {
		final Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			Listener<T> listener = (Listener<T>)items[i];
			listener.receive(this, object);
		}
		listeners.end();
	}
}
