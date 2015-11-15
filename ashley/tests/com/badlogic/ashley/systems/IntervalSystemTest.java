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

package com.badlogic.ashley.systems;

import org.junit.Test;

import com.badlogic.ashley.core.Engine;

import static org.junit.Assert.*;

public class IntervalSystemTest {
	private static final float deltaTime = 0.1f;

	private static class IntervalSystemSpy extends IntervalSystem {
		public int numUpdates;

		public IntervalSystemSpy () {
			super(deltaTime * 2.0f);
		}

		@Override
		protected void updateInterval () {
			++numUpdates;
		}
	}

	@Test
	public void intervalSystem () {
		Engine engine = new Engine();
		IntervalSystemSpy intervalSystemSpy = new IntervalSystemSpy();

		engine.addSystem(intervalSystemSpy);

		for (int i = 1; i <= 10; ++i) {
			engine.update(deltaTime);
			assertEquals(i / 2, intervalSystemSpy.numUpdates);
		}
	}

	@Test
	public void testGetInterval () {
		IntervalSystemSpy intervalSystemSpy = new IntervalSystemSpy();
		assertEquals(intervalSystemSpy.getInterval(), deltaTime * 2.0f, 0);
	}
}
