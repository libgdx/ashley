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

package com.badlogic.ashley.tests.utils;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * A simple Timer class that let's you measure multiple times and are identified via an id.
 * @author Stefan Bachmann
 *
 */
public class Timer {
	private ObjectMap<String, Long> times;
	
	public Timer(){
		times = new ObjectMap<String, Long>();
	}
	
	/**
	 * Start tracking a time with name as id.
	 * @param name The timer's id
	 */
	public void start(String name){
		times.put(name, System.currentTimeMillis());
	}
	
	
	/**
	 * Stop tracking the specified id
	 * @param name The timer's id
	 * @return the elapsed time
	 */
	public long stop(String name){
		if(times.containsKey(name)){
			long startTime = times.remove(name);
			return System.currentTimeMillis() - startTime;
		}
		else
			throw new RuntimeException("Timer id doesn't exist.");
	}
}

