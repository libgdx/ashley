package com.badlogic.ashley.core;
import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class FamilyManagerTests {

	private static class ComponentA implements Component {}
	private static class ComponentB implements Component {}
	private static class ComponentC implements Component {}
	
	
	@Test
	public void entitiesForFamily () {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);
		
		Family family = Family.all(ComponentA.class, ComponentB.class).get();
		ImmutableArray<Entity> familyEntities = manager.getEntitiesFor(family);

		assertEquals(0, familyEntities.size());

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();

		entity1.add(new ComponentA());
		entity1.add(new ComponentB());

		entity2.add(new ComponentA());
		entity2.add(new ComponentC());

		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());

		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		entities.add(entity4);

		manager.updateFamilyMembership(entity1);
		manager.updateFamilyMembership(entity2);
		manager.updateFamilyMembership(entity3);
		manager.updateFamilyMembership(entity4);
		
		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
	}

	@Test
	public void entityForFamilyWithRemoval () {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);

		Entity entity = new Entity();
		entity.add(new ComponentA());

		entities.add(entity);
		
		manager.updateFamilyMembership(entity);
		
		ImmutableArray<Entity> familyEntities = manager.getEntitiesFor(Family.all(ComponentA.class).get());
		
		assertEquals(1, familyEntities.size());
		assertTrue(familyEntities.contains(entity, true));
		
		entity.removing = true;
		entities.removeValue(entity, true);
		
		manager.updateFamilyMembership(entity);
		entity.removing = false;

		assertEquals(0, familyEntities.size());
		assertFalse(familyEntities.contains(entity, true));
	}

	@Test
	public void entitiesForFamilyAfter () {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);

		Family family = Family.all(ComponentA.class, ComponentB.class).get();
		ImmutableArray<Entity> familyEntities = manager.getEntitiesFor(family);

		assertEquals(0, familyEntities.size());

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();

		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		entities.add(entity4);

		entity1.add(new ComponentA());
		entity1.add(new ComponentB());

		entity2.add(new ComponentA());
		entity2.add(new ComponentC());

		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());
		
		manager.updateFamilyMembership(entity1);
		manager.updateFamilyMembership(entity2);
		manager.updateFamilyMembership(entity3);
		manager.updateFamilyMembership(entity4);

		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
	}

	@Test
	public void entitiesForFamilyWithRemoval () {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);

		Family family = Family.all(ComponentA.class, ComponentB.class).get();
		ImmutableArray<Entity> familyEntities = manager.getEntitiesFor(family);

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();

		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		entities.add(entity4);

		entity1.add(new ComponentA());
		entity1.add(new ComponentB());

		entity2.add(new ComponentA());
		entity2.add(new ComponentC());

		entity3.add(new ComponentA());
		entity3.add(new ComponentB());
		entity3.add(new ComponentC());

		entity4.add(new ComponentA());
		entity4.add(new ComponentB());
		entity4.add(new ComponentC());
		
		manager.updateFamilyMembership(entity1);
		manager.updateFamilyMembership(entity2);
		manager.updateFamilyMembership(entity3);
		manager.updateFamilyMembership(entity4);

		assertEquals(3, familyEntities.size());
		assertTrue(familyEntities.contains(entity1, true));
		assertTrue(familyEntities.contains(entity3, true));
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity2, true));
	
		entity1.remove(ComponentA.class);
		entity3.removing = true;
		entities.removeValue(entity3, true);

		manager.updateFamilyMembership(entity1);
		manager.updateFamilyMembership(entity3);
		
		entity3.removing = false;
		
		assertEquals(1, familyEntities.size());
		assertTrue(familyEntities.contains(entity4, true));
		assertFalse(familyEntities.contains(entity1, true));
		assertFalse(familyEntities.contains(entity3, true));
		assertFalse(familyEntities.contains(entity2, true));
	}

	@Test
	public void entitiesForFamilyWithRemovalAndFiltering () {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);

		ImmutableArray<Entity> entitiesWithComponentAOnly = manager.getEntitiesFor(Family.all(ComponentA.class)
			.exclude(ComponentB.class).get());

		ImmutableArray<Entity> entitiesWithComponentB = manager.getEntitiesFor(Family.all(ComponentB.class).get());

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();

		entities.add(entity1);
		entities.add(entity2);

		entity1.add(new ComponentA());

		entity2.add(new ComponentA());
		entity2.add(new ComponentB());
		
		manager.updateFamilyMembership(entity1);
		manager.updateFamilyMembership(entity2);

		assertEquals(1, entitiesWithComponentAOnly.size());
		assertEquals(1, entitiesWithComponentB.size());

		entity2.remove(ComponentB.class);
		
		manager.updateFamilyMembership(entity2);

		assertEquals(2, entitiesWithComponentAOnly.size());
		assertEquals(0, entitiesWithComponentB.size());
	}
	
	@Test
	public void entityListenerThrows() {
		Array<Entity> entities = new Array<Entity>();
		ImmutableArray<Entity> immutableEntities = new ImmutableArray<Entity>(entities);
		FamilyManager manager = new FamilyManager(immutableEntities);
		
		EntityListener listener = new EntityListener() {
			@Override
			public void entityAdded (Entity entity) {
				throw new GdxRuntimeException("throwing");
			}

			@Override
			public void entityRemoved (Entity entity) {
				throw new GdxRuntimeException("throwing");
			}
		};
		
		manager.addEntityListener(Family.all().get(), 0, listener);
		
		Entity entity = new Entity();
		entities.add(entity);
		
		boolean thrown = false;
		try {
			manager.updateFamilyMembership(entity);
		}
		catch (Exception e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		assertFalse(manager.notifying());
	}
}
