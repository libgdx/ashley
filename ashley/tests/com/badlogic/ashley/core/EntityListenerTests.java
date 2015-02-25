
package com.badlogic.ashley.core;

import org.junit.Test;

public class EntityListenerTests {

	@Test
	public void addEntityListenerFamilyRemove () {
		final Engine engine = new Engine();

		Entity e = new Entity();
		e.add(new PositionComponent());
		engine.addEntity(e);

		@SuppressWarnings("unchecked")
		Family family = Family.all(PositionComponent.class).get();
		engine.addEntityListener(family, new EntityListener() {

			public void entityRemoved (Entity entity) {
				engine.addEntity(new Entity());
			}

			public void entityAdded (Entity entity) {

			}
		});

		engine.removeEntity(e);
	}

	@Test
	public void addEntityListenerFamilyAdd () {
		final Engine engine = new Engine();

		Entity e = new Entity();
		e.add(new PositionComponent());

		@SuppressWarnings("unchecked")
		Family family = Family.all(PositionComponent.class).get();
		engine.addEntityListener(family, new EntityListener() {

			public void entityRemoved (Entity entity) {

			}

			public void entityAdded (Entity entity) {
				engine.addEntity(new Entity());
			}
		});

		engine.addEntity(e);
	}

	@Test
	public void addEntityListenerNoFamilyRemove () {
		final Engine engine = new Engine();

		Entity e = new Entity();
		e.add(new PositionComponent());
		engine.addEntity(e);

		@SuppressWarnings("unchecked")
		final Family family = Family.all(PositionComponent.class).get();
		engine.addEntityListener(new EntityListener() {

			public void entityRemoved (Entity entity) {
				if (family.matches(entity)) engine.addEntity(new Entity());
			}

			public void entityAdded (Entity entity) {

			}
		});

		engine.removeEntity(e);
	}

	@Test
	public void addEntityListenerNoFamilyAdd () {
		final Engine engine = new Engine();

		Entity e = new Entity();
		e.add(new PositionComponent());

		@SuppressWarnings("unchecked")
		final Family family = Family.all(PositionComponent.class).get();
		engine.addEntityListener(new EntityListener() {

			public void entityRemoved (Entity entity) {

			}

			public void entityAdded (Entity entity) {
				if (family.matches(entity)) engine.addEntity(new Entity());
			}
		});

		engine.addEntity(e);
	}

	public class PositionComponent extends Component {
	}
}
