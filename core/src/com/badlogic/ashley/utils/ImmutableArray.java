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
 * Interface for arrays that cannot be modified.
 * However, note that mutable elements in the array could be modified.
 * 
 * @author David Saltares
 *
 */
public interface ImmutableArray<T> {
	public int getSize();
	public T get(int index);
	public boolean contains(T value, boolean identity);
	public int indexOf(T value, boolean identity);
	public int lastIndexOf(T value, boolean identity);
}
