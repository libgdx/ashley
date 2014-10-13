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

package com.badlogic.ashley.tests;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import java.util.Comparator;

public class SortedIteratingSystemTest {
	private static final ComponentMapper<OrderComponent> mapper = ComponentMapper.getFor(OrderComponent.class);
	private static final OrderComparator comparator = new OrderComparator();
	
	public static void main(String[] args){
		Engine engine = new Engine();
		
		OrderSystem orderSystem = new OrderSystem();
		engine.addSystem(orderSystem);
		
		Entity a = createEntity("A", 0);
		Entity b = createEntity("B", 1);
		Entity c = createEntity("C", 3);
		Entity d = createEntity("D", 2);
		
		engine.addEntity(a);
		engine.addEntity(b);
		engine.addEntity(c);
		engine.update(0);
		
		engine.addEntity(d);
		engine.update(0);
		
		a.getComponent(OrderComponent.class).zLayer = 3;
		b.getComponent(OrderComponent.class).zLayer = 2;
		c.getComponent(OrderComponent.class).zLayer = 1;
		d.getComponent(OrderComponent.class).zLayer = 0;
		orderSystem.forceSort();
		engine.update(0);
	}
	
	private static Entity createEntity(String name, int zLayer) {
		Entity entity = new Entity();
		entity.add(new OrderComponent(name, zLayer));
		return entity;
	}
	
	public static class OrderComponent extends Component {
		public String name;
		public int zLayer;

		public OrderComponent(String name, int zLayer) {
			this.name = name;
			this.zLayer = zLayer;
		}
	}
	
	public static class OrderSystem extends SortedIteratingSystem {
		public OrderSystem() {
			super(Family.getFor(OrderComponent.class), comparator);
		}

		@Override
		public void update(float deltaTime) {
			System.out.println("-------");
			super.update(deltaTime);
		}

		@Override
		protected void processEntity(Entity entity, float deltaTime) {
			OrderComponent component = mapper.get(entity);
			System.out.printf("%s (%d)\n", component.name, component.zLayer);
		}
		
	}
	
	private static class OrderComparator implements Comparator<Entity> {

		@Override
		public int compare(Entity a, Entity b) {
			OrderComponent ac = mapper.get(a);
			OrderComponent bc = mapper.get(b);
			return ac.zLayer > bc.zLayer ? 1 : (ac.zLayer == bc.zLayer) ? 0 : -1;
		}
	}
}
