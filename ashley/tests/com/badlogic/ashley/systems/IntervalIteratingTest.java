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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import static org.junit.Assert.*;

public class IntervalIteratingTest {
	private static final float deltaTime = 0.1f;

	private static class IntervalComponentSpy implements Component {
		public int numUpdates = 0;
	}

	private static class IntervalIteratingSystemSpy extends IntervalIteratingSystem {
		private ComponentMapper<IntervalComponentSpy> im;

		public IntervalIteratingSystemSpy () {
			super(Family.all(IntervalComponentSpy.class).get(), deltaTime * 2.0f);

			im = ComponentMapper.getFor(IntervalComponentSpy.class);
		}

		@Override
		protected void processEntity (Entity entity) {
			im.get(entity).numUpdates++;
		}
	}

	@Test
	public void intervalSystem () {
		Engine engine = new Engine();
		IntervalIteratingSystemSpy intervalSystemSpy = new IntervalIteratingSystemSpy();
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(IntervalComponentSpy.class).get());
		ComponentMapper<IntervalComponentSpy> im = ComponentMapper.getFor(IntervalComponentSpy.class);

		engine.addSystem(intervalSystemSpy);

		for (int i = 0; i < 10; ++i) {
			Entity entity = new Entity();
			entity.add(new IntervalComponentSpy());
			engine.addEntity(entity);
		}

		for (int i = 1; i <= 10; ++i) {
			engine.update(deltaTime);

			for (int j = 0; j < entities.size(); ++j) {
				assertEquals(i / 2, im.get(entities.get(j)).numUpdates);
			}
		}
	}
}
