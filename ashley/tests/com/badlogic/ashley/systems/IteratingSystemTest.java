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
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import static org.junit.Assert.*;

public class IteratingSystemTest {
	private static final float deltaTime = 0.16f;

	private static class ComponentA implements Component {
	}

	private static class ComponentB implements Component {
	}

	private static class ComponentC implements Component {
	}

	private static class IteratingSystemMock extends IteratingSystem {
		public int numUpdates;

		public IteratingSystemMock (Family family) {
			super(family);
		}

		@Override
		public void processEntity (Entity entity, float deltaTime) {
			++numUpdates;
		}
	}

	private static class SpyComponent implements Component {
		public int updates = 0;
	}

	private static class IndexComponent implements Component {
		public int index = 0;
	}

	private static class IteratingComponentRemovalSystem extends IteratingSystem {

		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;

		public IteratingComponentRemovalSystem () {
			super(Family.all(SpyComponent.class, IndexComponent.class).get());

			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}

		@Override
		public void processEntity (Entity entity, float deltaTime) {
			int index = im.get(entity).index;
			if (index % 2 == 0) {
				entity.remove(SpyComponent.class);
				entity.remove(IndexComponent.class);
			} else {
				sm.get(entity).updates++;
			}
		}

	}

	private static class IteratingRemovalSystem extends IteratingSystem {
		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;

		public IteratingRemovalSystem () {
			super(Family.all(SpyComponent.class, IndexComponent.class).get());

			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}

		@Override
		public void addedToEngine (Engine engine) {
			super.addedToEngine(engine);
		}

		@Override
		public void processEntity (Entity entity, float deltaTime) {
			int index = im.get(entity).index;
			if (index % 2 == 0) {
				getEngine().removeEntity(entity);
			} else {
				sm.get(entity).updates++;
			}
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldIterateEntitiesWithCorrectFamily () {
		final Engine engine = new Engine();

		final Family family = Family.all(ComponentA.class, ComponentB.class).get();
		final IteratingSystemMock system = new IteratingSystemMock(family);
		final Entity e = new Entity();

		engine.addSystem(system);
		engine.addEntity(e);

		// When entity has ComponentA
		e.add(new ComponentA());
		engine.update(deltaTime);

		assertEquals(0, system.numUpdates);

		// When entity has ComponentA and ComponentB
		system.numUpdates = 0;
		e.add(new ComponentB());
		engine.update(deltaTime);

		assertEquals(1, system.numUpdates);

		// When entity has ComponentA, ComponentB and ComponentC
		system.numUpdates = 0;
		e.add(new ComponentC());
		engine.update(deltaTime);

		assertEquals(1, system.numUpdates);

		// When entity has ComponentB and ComponentC
		system.numUpdates = 0;
		e.remove(ComponentA.class);
		e.add(new ComponentC());
		engine.update(deltaTime);

		assertEquals(0, system.numUpdates);
	}

	@Test
	public void entityRemovalWhileIterating () {
		Engine engine = new Engine();
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(SpyComponent.class, IndexComponent.class).get());
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);

		engine.addSystem(new IteratingRemovalSystem());

		final int numEntities = 10;

		for (int i = 0; i < numEntities; ++i) {
			Entity e = new Entity();
			e.add(new SpyComponent());

			IndexComponent in = new IndexComponent();
			in.index = i + 1;

			e.add(in);

			engine.addEntity(e);
		}

		engine.update(deltaTime);

		assertEquals(numEntities / 2, entities.size());

		for (int i = 0; i < entities.size(); ++i) {
			Entity e = entities.get(i);

			assertEquals(1, sm.get(e).updates);
		}
	}

	@Test
	public void componentRemovalWhileIterating () {
		Engine engine = new Engine();
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(SpyComponent.class, IndexComponent.class).get());
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);

		engine.addSystem(new IteratingComponentRemovalSystem());

		final int numEntities = 10;

		for (int i = 0; i < numEntities; ++i) {
			Entity e = new Entity();
			e.add(new SpyComponent());

			IndexComponent in = new IndexComponent();
			in.index = i + 1;

			e.add(in);

			engine.addEntity(e);
		}

		engine.update(deltaTime);

		assertEquals(numEntities / 2, entities.size());

		for (int i = 0; i < entities.size(); ++i) {
			Entity e = entities.get(i);

			assertEquals(1, sm.get(e).updates);
		}
	}
}
