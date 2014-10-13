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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import java.util.Comparator;
import java.util.LinkedList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SortedIteratingSystemTest {
	private static final ComponentMapper<OrderComponent> orderMapper = ComponentMapper.getFor(OrderComponent.class);
	private static final OrderComparator comparator = new OrderComparator();
	private static final float deltaTime = 0.16f;
	
	private static class ComponentB extends Component {}
	private static class ComponentC extends Component {}

	private static class SortedIteratingSystemMock extends SortedIteratingSystem {
		public LinkedList<String> expectedNames = new LinkedList<String>();

		public SortedIteratingSystemMock(Family family) {
			super(family, comparator);
		}
		
		@Override
		public void update(float deltaTime) {
			super.update(deltaTime);
			assertTrue(expectedNames.isEmpty());
		}

		@Override
		public void processEntity(Entity entity, float deltaTime) {
			OrderComponent component = orderMapper.get(entity);
			assertNotNull(component);
			assertFalse(expectedNames.isEmpty());
			assertEquals(expectedNames.poll(), component.name);
		}
	}
	
	public static class OrderComponent extends Component {
		public String name;
		public int zLayer;

		public OrderComponent(String name, int zLayer) {
			this.name = name;
			this.zLayer = zLayer;
		}
	}
	
	private static class SpyComponent extends Component {
		public int updates = 0;
	}
	
	private static class IndexComponent extends Component {
		public int index = 0;
	}
	
	private static class IteratingComponentRemovalSystem extends SortedIteratingSystem {

		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;
		
		public IteratingComponentRemovalSystem() {
			super(Family.getFor(SpyComponent.class, IndexComponent.class), comparator);
			
			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}

		@Override
		public void processEntity(Entity entity, float deltaTime) {
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
	
	private static class IteratingRemovalSystem extends SortedIteratingSystem {

		private Engine engine;
		private ComponentMapper<SpyComponent> sm;
		private ComponentMapper<IndexComponent> im;
		
		public IteratingRemovalSystem() {
			super(Family.getFor(SpyComponent.class, IndexComponent.class), comparator);
			
			sm = ComponentMapper.getFor(SpyComponent.class);
			im = ComponentMapper.getFor(IndexComponent.class);
		}
		
		@Override
		public void addedToEngine(Engine engine) {
			super.addedToEngine(engine);
			this.engine = engine;
		}

		@Override
		public void processEntity(Entity entity, float deltaTime) {
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

		final Family family = Family.getFor(OrderComponent.class, ComponentB.class);
		final SortedIteratingSystemMock system = new SortedIteratingSystemMock(family);
		final Entity e = new Entity();

		engine.addSystem(system);
		engine.addEntity(e);
		
		//When entity has OrderComponent
		e.add(new OrderComponent("A", 0));
		engine.update(deltaTime);

		//When entity has OrderComponent and ComponentB
		e.add(new ComponentB());
		system.expectedNames.addLast("A");
		engine.update(deltaTime);

		//When entity has OrderComponent, ComponentB and ComponentC
		e.add(new ComponentC());
		system.expectedNames.addLast("A");
		engine.update(deltaTime);

		//When entity has ComponentB and ComponentC
		e.remove(OrderComponent.class);
		e.add(new ComponentC());
		engine.update(deltaTime);
	}
	
	@Test
	public void entityRemovalWhileIterating(){
		Engine engine = new Engine();
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.getFor(SpyComponent.class, IndexComponent.class));
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);
		
		engine.addSystem(new IteratingRemovalSystem());
		
		final int numEntities = 10;
		
		for (int i = 0; i < numEntities; ++i) {
			Entity e = new Entity();
			e.add(new SpyComponent());
			e.add(new OrderComponent("" + i, i));
			
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
	public void componentRemovalWhileIterating(){
		Engine engine = new Engine();
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.getFor(SpyComponent.class, IndexComponent.class));
		ComponentMapper<SpyComponent> sm = ComponentMapper.getFor(SpyComponent.class);
		
		engine.addSystem(new IteratingComponentRemovalSystem());
		
		final int numEntities = 10;
		
		for (int i = 0; i < numEntities; ++i) {
			Entity e = new Entity();
			e.add(new SpyComponent());
			e.add(new OrderComponent("" + i, i));
			
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
	
	private static Entity createOrderEntity(String name, int zLayer) {
		Entity entity = new Entity();
		entity.add(new OrderComponent(name, zLayer));
		return entity;
	}
	
	@Test
	public void entityOrder(){
		Engine engine = new Engine();
		
		final Family family = Family.getFor(OrderComponent.class);
		final SortedIteratingSystemMock orderSystem = new SortedIteratingSystemMock(family);
		engine.addSystem(orderSystem);
		
		Entity a = createOrderEntity("A", 0);
		Entity b = createOrderEntity("B", 1);
		Entity c = createOrderEntity("C", 3);
		Entity d = createOrderEntity("D", 2);
		
		engine.addEntity(a);
		engine.addEntity(b);
		engine.addEntity(c);
		orderSystem.expectedNames.addLast("A");
		orderSystem.expectedNames.addLast("B");
		orderSystem.expectedNames.addLast("C");
		engine.update(0);
		
		engine.addEntity(d);
		orderSystem.expectedNames.addLast("A");
		orderSystem.expectedNames.addLast("B");
		orderSystem.expectedNames.addLast("D");
		orderSystem.expectedNames.addLast("C");
		engine.update(0);
		
		orderMapper.get(a).zLayer = 3;
		orderMapper.get(b).zLayer = 2;
		orderMapper.get(c).zLayer = 1;
		orderMapper.get(d).zLayer = 0;
		orderSystem.forceSort();
		orderSystem.expectedNames.addLast("D");
		orderSystem.expectedNames.addLast("C");
		orderSystem.expectedNames.addLast("B");
		orderSystem.expectedNames.addLast("A");
		engine.update(0);
	}
	
	private static class OrderComparator implements Comparator<Entity> {

		@Override
		public int compare(Entity a, Entity b) {
			OrderComponent ac = orderMapper.get(a);
			OrderComponent bc = orderMapper.get(b);
			return ac.zLayer > bc.zLayer ? 1 : (ac.zLayer == bc.zLayer) ? 0 : -1;
		}
	}
}
