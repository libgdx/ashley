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

/**
 * A simple Listener interface used to listen to a {@link Signal}.
 * @author Stefan Bachmann
 */
public interface Listener<T> {
	/**
	 * @param signal The Signal that triggered event
	 * @param object The object passed on dispatch
	 */
	public void receive (Signal<T> signal, T object);
}
