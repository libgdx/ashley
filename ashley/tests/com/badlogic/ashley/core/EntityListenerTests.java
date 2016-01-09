
package com.badlogic.ashley.core;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

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

	public class PositionComponent implements Component {
	}

	@Test
	public void entityListenerPriority () {
		EntityListener a = mock(EntityListener.class);
		EntityListener b = mock(EntityListener.class);
		EntityListener c = mock(EntityListener.class);
		InOrder inOrder = inOrder(a, b, c);

		Entity entity = new Entity();
		Engine engine = new Engine();
		engine.addEntityListener(-3, b);
		engine.addEntityListener(c);
		engine.addEntityListener(-4, a);
		inOrder.verifyNoMoreInteractions();

		engine.addEntity(entity);
		inOrder.verify(a).entityAdded(entity);
		inOrder.verify(b).entityAdded(entity);
		inOrder.verify(c).entityAdded(entity);
		inOrder.verifyNoMoreInteractions();

		engine.removeEntity(entity);
		inOrder.verify(a).entityRemoved(entity);
		inOrder.verify(b).entityRemoved(entity);
		inOrder.verify(c).entityRemoved(entity);
		inOrder.verifyNoMoreInteractions();

		engine.removeEntityListener(b);
		inOrder.verifyNoMoreInteractions();

		engine.addEntity(entity);
		inOrder.verify(a).entityAdded(entity);
		inOrder.verify(c).entityAdded(entity);
		inOrder.verifyNoMoreInteractions();

		engine.addEntityListener(4, b);
		inOrder.verifyNoMoreInteractions();

		engine.removeEntity(entity);
		inOrder.verify(a).entityRemoved(entity);
		inOrder.verify(c).entityRemoved(entity);
		inOrder.verify(b).entityRemoved(entity);
		inOrder.verifyNoMoreInteractions();
	}

	private static class ComponentA implements Component {
	}

	private static class ComponentB implements Component {
	}

	@Test
	public void familyListenerPriority () {
		EntityListener a = mock(EntityListener.class);
		EntityListener b = mock(EntityListener.class);
		InOrder inOrder = inOrder(a, b);

		Engine engine = new Engine();
		engine.addEntityListener(Family.all(ComponentB.class).get(), -2, b);
		engine.addEntityListener(Family.all(ComponentA.class).get(), -3, a);
		inOrder.verifyNoMoreInteractions();

		Entity entity = new Entity();
		entity.add(new ComponentA());
		entity.add(new ComponentB());

		engine.addEntity(entity);
		inOrder.verify(a).entityAdded(entity);
		inOrder.verify(b).entityAdded(entity);
		inOrder.verifyNoMoreInteractions();

		entity.remove(ComponentB.class);
		inOrder.verify(b).entityRemoved(entity);
		inOrder.verifyNoMoreInteractions();

		entity.remove(ComponentA.class);
		inOrder.verify(a).entityRemoved(entity);
		inOrder.verifyNoMoreInteractions();

		entity.add(new ComponentA());
		inOrder.verify(a).entityAdded(entity);
		inOrder.verifyNoMoreInteractions();

		entity.add(new ComponentB());
		inOrder.verify(b).entityAdded(entity);
		inOrder.verifyNoMoreInteractions();
	}

    private interface ComponentRecorder {
        void addingComponentA();

        void removingComponentA();

        void addingComponentB();

        void removingComponentB();
    }

    @Test
    public void componentHandlingInListeners() {
        final Engine engine = new Engine();

        final ComponentRecorder recorder = mock(ComponentRecorder.class);

        engine.addEntityListener(new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                recorder.addingComponentA();
                entity.add(new ComponentA());
            }

            @Override
            public void entityRemoved(Entity entity) {
                recorder.removingComponentA();
                entity.remove(ComponentA.class);
            }
        });

        engine.addEntityListener(new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                recorder.addingComponentB();
                entity.add(new ComponentB());
            }

            @Override
            public void entityRemoved(Entity entity) {
                recorder.removingComponentB();
                entity.remove(ComponentB.class);
            }
        });

        engine.update(0);
        Entity e = new Entity();
        engine.addEntity(e);
        engine.update(0);
        engine.removeEntity(e);
        engine.update(0);

        verify(recorder).addingComponentA();
        verify(recorder).removingComponentA();
        verify(recorder).addingComponentB();
        verify(recorder).removingComponentB();
    }
}
