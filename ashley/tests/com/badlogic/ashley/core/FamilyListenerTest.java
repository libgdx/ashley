package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;

public class FamilyListenerTest {

	public static int entitesInMovementSystem = 0;
	public static int entitesInPositionSystem = 0;

	@Test
	public void validFamilyListener() {
		PooledEngine engine = new PooledEngine();

		@SuppressWarnings("unchecked")
		Family familyAB = Family.getFor(ComponentA.class, ComponentB.class);
		SystemAB systemAB = new SystemAB(familyAB);

		@SuppressWarnings("unchecked")
		Family familyA = Family.getFor(ComponentA.class);
		SystemA systemA = new SystemA(familyA);

		engine.addSystem(systemA);
		engine.addSystem(systemAB);

		Array<Entity> entities = new Array<Entity>();

		for (int i = 0; i < 10; i++) {
			Entity entity = engine.createEntity();
			entity.add(new ComponentA());
			entity.add(new ComponentB());

			engine.addEntity(entity);

			entities.add(entity);
		}

		for (int i = 0; i < 10; i++) {
			engine.update(0.25f);
			checkNumOfEntitiesInSystems(engine, familyAB, familyA);
		}

		for (int i = 0; i < entities.size; i++) {
			if (i >= 5)
				entities.get(i).remove(ComponentA.class);
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, familyAB, familyA);

		for (int i = 0; i < entities.size; i++) {
			if (i < 5)
				entities.get(i).remove(ComponentA.class);
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, familyAB, familyA);

		for (int i = 0; i < entities.size; i++) {
			if (i < 5)
				entities.get(i).add(engine.createComponent(ComponentA.class));
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, familyAB, familyA);

		for (int i = 0; i < entities.size; i++) {
			if (i >= 5)
				entities.get(i).add(engine.createComponent(ComponentB.class));
		}
		engine.update(0.25f);
		checkNumOfEntitiesInSystems(engine, familyAB, familyA);

	}

	private static void checkNumOfEntitiesInSystems(PooledEngine engine,
			Family familyAB, Family familyA) {

		assertTrue(entitesInMovementSystem == engine.getEntitiesFor(familyAB)
				.size());

		assertTrue(entitesInPositionSystem == engine.getEntitiesFor(familyA)
				.size());
	}

	private static class ComponentA extends Component {
	}

	private static class ComponentB extends Component {
	}

	public static class SystemAB extends IteratingSystem {
		public SystemAB(Family family) {
			super(family);
		}

		@Override
		public void processEntity(Entity e, float deltaTime) {

		}

		@Override
		public void entityAddedToSystem(Entity e) {
			FamilyListenerTest.entitesInPositionSystem++;
		}

		@Override
		public void entityRemovedFromSystem(Entity e) {
			// log(this.getClass().getSimpleName() + "= [" + e
			// + "] removed from System.");
			FamilyListenerTest.entitesInPositionSystem--;
		}

	}

	public static class SystemA extends IteratingSystem {

		public SystemA(Family family) {
			super(family);
		}

		@Override
		public void processEntity(Entity e, float deltaTime) {

		}

		@Override
		public void entityAddedToSystem(Entity e) {
			FamilyListenerTest.entitesInMovementSystem++;
		}

		@Override
		public void entityRemovedFromSystem(Entity e) {
			FamilyListenerTest.entitesInMovementSystem--;
		}

	}

	public static void log(String string) {
		log(string, false);
	}

	public static void log(String string, boolean err) {
		if (err)
			System.err.println(string);
		else
			System.out.println(string);
	}

}
