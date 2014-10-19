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

package com.badlogic.ashley.core;

import com.badlogic.ashley.systems.*;
import org.junit.Test;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import static org.junit.Assert.*;

public class EntityIteratorTest {
	private static final float deltaTime = 0.16f;
	
	private static class ComponentA extends Component {}
	private static class ComponentB extends Component {}
	private static class ComponentC extends Component {}

	private static class EntitySystemMock extends EntitySystem {
		private final EntityIterator iterator;
		private final EntityIterator.Callback callback;

		private EntitySystemMock(Family family, EntityIterator.Callback callback) {
			iterator = new EntityIterator(family);
			this.callback = callback;
		}
		
		@Override
		public void update(float deltaTime) {
			iterator.iterate(callback, null);
		}
		
		@Override
		public void addedToEngine(Engine engine) {
			iterator.addedToEngine(engine);
		}
		
		@Override
		public void removedFromEngine(Engine engine) {
			iterator.removedFromEngine(engine);
		}

		private ImmutableArray<Entity> getEntities() {
			return iterator.getEntities();
		}
	}
	
	private static class EntityIteratorCallbackMock implements EntityIterator.Callback<Object> {
		public int numUpdates;

		@Override
		public void processEntity(Entity entity, Object param) {
			 ++numUpdates;
		}
	}
	
	private static class SpyComponent extends Component {
		public int updates = 0;
	}
	
	private static class IndexComponent extends Component {
		public int index = 0;
	}
	
	private static class IteratorComponentRemovalCallback implements EntityIterator.Callback<Object> {

		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;
		
		public IteratorComponentRemovalCallback() {
			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}

		@Override
		public void processEntity(Entity entity, Object param) {
			int index = im.get(entity).index;
			if (index % 2 == 0) {
				entity.remove(SpyComponent.class);
				entity.remove(IndexComponent.class);
			}
			else {
				sm.get(entity).updates++;
			}
		}
		
	}
	
	private static class IteratorRemovalCallback implements EntityIterator.Callback<Object> {

		private Engine engine;
		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;

		private IteratorRemovalCallback(Engine engine) {
			this.engine = engine;
			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}

		@Override
		public void processEntity(Entity entity, Object param) {
			int index = im.get(entity).index;
			if (index % 2 == 0) {
				engine.removeEntity(entity);
			}
			else {
				sm.get(entity).updates++;
			}
		}
		
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldIterateEntitiesWithCorrectFamily(){
		final Engine engine = new Engine();

		final Family family = Family.getFor(ComponentA.class, ComponentB.class);
		final EntityIteratorCallbackMock callback = new EntityIteratorCallbackMock();
		final EntitySystemMock system = new EntitySystemMock(family, callback);
		final Entity e = new Entity();

		engine.addSystem(system);
		engine.addEntity(e);

		//When entity has ComponentA
		e.add(new ComponentA());
		engine.update(deltaTime);

		assertEquals(0, callback.numUpdates);

		//When entity has ComponentA and ComponentB
		callback.numUpdates = 0;
		e.add(new ComponentB());
		engine.update(deltaTime);

		assertEquals(1, callback.numUpdates);

		//When entity has ComponentA, ComponentB and ComponentC
		callback.numUpdates = 0;
		e.add(new ComponentC());
		engine.update(deltaTime);

		assertEquals(1, callback.numUpdates);

		//When entity has ComponentB and ComponentC
		callback.numUpdates = 0;
		e.remove(ComponentA.class);
		e.add(new ComponentC());
		engine.update(deltaTime);

		assertEquals(0, callback.numUpdates);
	}
	
	@Test
	public void entityRemovalWhileIterating(){
		Engine engine = new Engine();
		
		Family family = Family.getFor(SpyComponent.class, IndexComponent.class);
		IteratorRemovalCallback callback = new IteratorRemovalCallback(engine);
		EntitySystemMock system = new EntitySystemMock(family, callback);
		engine.addSystem(system);
		
		ImmutableArray<Entity> entities = system.getEntities();
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
		
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);
		for (int i = 0; i < entities.size(); ++i) {
			Entity e = entities.get(i);
			
			assertEquals(1, sm.get(e).updates);
		}
	}
	
	@Test
	public void componentRemovalWhileIterating(){
		Engine engine = new Engine();
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);
		
		Family family = Family.getFor(SpyComponent.class, IndexComponent.class);
		IteratorComponentRemovalCallback callback = new IteratorComponentRemovalCallback();
		EntitySystemMock system = new EntitySystemMock(family, callback);
		engine.addSystem(system);
		
		ImmutableArray<Entity> entities = system.getEntities();
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
