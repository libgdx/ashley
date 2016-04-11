
package com.badlogic.ashley.core;

import static org.junit.Assert.*;

import com.badlogic.ashley.systems.IteratingSystem;
import org.junit.Test;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PooledEngineTests {
	private float deltaTime = 0.16f;

	public static class PositionComponent implements Component {
		public float x = 0.0f;
		public float y = 0.0f;
	}

	public static class MyPositionListener implements EntityListener {
		public static ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

		int counter = 0;

		@Override
		public void entityAdded (Entity entity) {

		}

		@Override
		public void entityRemoved (Entity entity) {
			PositionComponent position = positionMapper.get(entity);
			assertNotNull(position);
		}
	}

	public static class CombinedSystem extends EntitySystem {
		private ImmutableArray<Entity> allEntities;
		private int counter = 0;

		@Override
		public void addedToEngine (Engine engine) {
			allEntities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
		}

		@Override
		public void update (float deltaTime) {
			if (counter >= 6 && counter <= 8) {
				getEngine().removeEntity(allEntities.get(2));
			}
			counter++;
		}
	}

	public static class ComponentCounterListener implements Listener<Entity> {
		public int totalCalls = 0;

		@Override
		public void receive (Signal<Entity> signal, Entity object) {
			totalCalls++;
		}
	}

	private static class RemoveEntityTwiceSystem extends EntitySystem {
		private ImmutableArray<Entity> entities;

		@Override
		public void addedToEngine (Engine engine) {
			entities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
		}

		@Override
		public void update (float deltaTime) {
			Entity entity;
			PooledEngine engine = (PooledEngine)getEngine();
			for (int i = 0; i < 10; i++) {
				entity = engine.createEntity();
				assertEquals(0, entity.flags);
				entity.flags = 1;
				entity.add(engine.createComponent(PositionComponent.class));
				engine.addEntity(entity);
			}
			for (int i = 0; i < entities.size(); ++i) {
				entity = entities.get(i);
				engine.removeEntity(entity);
				engine.removeEntity(entity);
			}
		}
	}
	
	private static class PooledComponentSpy implements Component, Poolable {
		public boolean recycled = false;
		
		
		@Override
		public void reset () {
			recycled = true;
		}
	}

	private static class UniquePooledCompnentA implements Component, Poolable {
		public int count = 1;

		@Override
		public void reset() {
			count = 1;
		}
	}

	private static class UniquePooledCompnentB implements Component, Poolable {
		public float value = 1f;

		@Override
		public void reset() {
			value = 1f;
		}
	}

	private class RemoveAFromBSystem extends IteratingSystem{

		final int numberToRemove;
		int numberRemoved = 0;
		public RemoveAFromBSystem(int numberToRemove){
			super(Family.all(UniquePooledCompnentA.class, UniquePooledCompnentB.class).get());
			this.numberToRemove = numberToRemove;
		}

		//Removes one each update
		@Override
		public void update(float deltaTime) {
			super.update(deltaTime);
			numberRemoved = 0;
		}

		@Override
		protected void processEntity(Entity entity, float deltaTime) {
			if(numberRemoved < numberToRemove){
				if(entity.hasComponent(ComponentType.getFor(UniquePooledCompnentA.class))){
					getEngine().removeEntity(entity);
					//entity.remove(UniquePooledCompnentA.class);
					numberRemoved++;
				}
			}
		}
	}

	private interface IAssertEntityInFamily{
		public void assertAgainst(Entity entity);
	}

	private class ListeningAndLookingForASystem extends IteratingSystem{

		EntityListener el;
		IAssertEntityInFamily asserter;
		final Family targetFamily;
		public ListeningAndLookingForASystem(Family familyToListenOn, IAssertEntityInFamily entityAsserter){
			super(familyToListenOn);
			this.asserter = entityAsserter;
			this.targetFamily = familyToListenOn;
		}

		@Override
		public void addedToEngine(Engine engine) {
			super.addedToEngine(engine);

			final Engine eg = engine;
			if(el == null){
				el = new EntityListener() {
					@Override
					public void entityAdded(Entity entity) {

					}

					@Override
					public void entityRemoved(Entity entity) {
						for(Entity e : eg.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get())){
							asserter.assertAgainst(e);
						}
					}
				};
			}
			engine.addEntityListener(targetFamily, el);
		}

		@Override
		protected void processEntity(Entity entity, float deltaTime) {

		}
	}

	private class GenerateABSystem extends EntitySystem{

		int numberToAdd = 1;
		public GenerateABSystem(int numberToAdd){
			super();
			this.numberToAdd = numberToAdd;
		}

		//Removes one each update
		@Override
		public void update(float deltaTime) {
			super.update(deltaTime);
			for(int i=0;i<numberToAdd;i++) {
				PooledEngine engine = (PooledEngine) getEngine();

				//Try to use up a pooled entity on a non-matching
				//	entity
				Entity bOnly = engine.createEntity();
				bOnly.add(engine.createComponent(UniquePooledCompnentB.class));
				engine.addEntity(bOnly);

				Entity ab = engine.createEntity();
				ab.add(engine.createComponent(UniquePooledCompnentA.class));
				ab.add(engine.createComponent(UniquePooledCompnentB.class));
				engine.addEntity(ab);
			}


		}

	}


	@Test
	public void entityRemovalListenerOrder () {
		PooledEngine engine = new PooledEngine();

		CombinedSystem combinedSystem = new CombinedSystem();

		engine.addSystem(combinedSystem);
		engine.addEntityListener(Family.all(PositionComponent.class).get(), new MyPositionListener());

		for (int i = 0; i < 10; i++) {
			Entity entity = engine.createEntity();
			entity.add(engine.createComponent(PositionComponent.class));
			engine.addEntity(entity);
		}

		assertEquals(10, combinedSystem.allEntities.size());

		for (int i = 0; i < 10; i++) {
			engine.update(deltaTime);
		}

		engine.removeAllEntities();
	}

	@Test
	public void resetEntityCorrectly () {
		PooledEngine engine = new PooledEngine();

		ComponentCounterListener addedListener = new ComponentCounterListener();
		ComponentCounterListener removedListener = new ComponentCounterListener();

		// force the engine to create a Family so family bits get set
		ImmutableArray<Entity> familyEntities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());

		Entity[] entities = new Entity[10];
		final int totalEntities = 10;

		for (int i = 0; i < totalEntities; i++) {
			entities[i] = engine.createEntity();

			entities[i].flags = 5;

			entities[i].componentAdded.add(addedListener);
			entities[i].componentRemoved.add(removedListener);

			entities[i].add(engine.createComponent(PositionComponent.class));
			engine.addEntity(entities[i]);

			assertNotNull(entities[i].componentOperationHandler);
			assertEquals(1, entities[i].getComponents().size());
			assertFalse(entities[i].getFamilyBits().isEmpty());
			assertTrue(familyEntities.contains(entities[i], true));
		}

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(0, removedListener.totalCalls);

		engine.removeAllEntities();

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);

		for (int i = 0; i < totalEntities; i++) {
			assertFalse(entities[i].removing);
			assertEquals(0, entities[i].flags);
			assertNull(entities[i].componentOperationHandler);
			assertEquals(0, entities[i].getComponents().size());
			assertTrue(entities[i].getFamilyBits().isEmpty());
			assertFalse(familyEntities.contains(entities[i], true));
			
			entities[i].componentAdded.dispatch(entities[i]);
			entities[i].componentRemoved.dispatch(entities[i]);
		}

		assertEquals(totalEntities, addedListener.totalCalls);
		assertEquals(totalEntities, removedListener.totalCalls);
	}

	@Test
	public void recycleEntity () {
		int numEntities = 5;
		PooledEngine engine = new PooledEngine(numEntities, 100, 0, 100);
		Array<Entity> entities = new Array<Entity>();

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			assertFalse(entity.removing);
			assertEquals(0, entity.flags);
			engine.addEntity(entity);
			entities.add(entity);
			entity.flags = 1;
		}

		for (Entity entity : entities) {
			engine.removeEntity(entity);
			assertEquals(0, entity.flags);
			assertFalse(entity.removing);
		}

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = engine.createEntity();
			assertEquals(0, entity.flags);
			assertFalse(entity.removing);
			assertTrue(entities.contains(entity, true));
		}
	}

	@Test
	public void removeEntityTwice () {
		PooledEngine engine = new PooledEngine();
		engine.addSystem(new RemoveEntityTwiceSystem());

		for (int j = 0; j < 2; j++) {
			engine.update(0);
		}
	}
	
	@Test
	public void recycleComponent() {
		int maxEntities = 10;
		int maxComponents = 10;
		PooledEngine engine = new PooledEngine(maxEntities, maxEntities, maxComponents, maxComponents);
		
		for (int i = 0; i < maxComponents; ++i) {
			Entity e = engine.createEntity();
			PooledComponentSpy c = engine.createComponent(PooledComponentSpy.class);
			
			assertEquals(false, c.recycled);
			
			e.add(c);
			
			engine.addEntity(e);
		}
		
		engine.removeAllEntities();
		
		for (int i = 0; i < maxComponents; ++i) {
			Entity e = engine.createEntity();
			PooledComponentSpy c = engine.createComponent(PooledComponentSpy.class);
			
			assertEquals(true, c.recycled);
			
			e.add(c);
		}
		
		engine.removeAllEntities();
	}

	@Test
	public void getEntitiesForFamilyReturnsOnlyEntitiesInFamily(){

		final PooledEngine engine = new PooledEngine();

		int aCount = 2;
		int bCount = 3;

		for(int i = 0;i<aCount;i++){
			Entity a = engine.createEntity();
			a.add(engine.createComponent(UniquePooledCompnentA.class));
			engine.addEntity(a);
		}

		for(int i = 0;i<bCount;i++){
			Entity b = engine.createEntity();
			b.add(engine.createComponent(UniquePooledCompnentA.class));
			b.add(engine.createComponent(UniquePooledCompnentB.class));
			engine.addEntity(b);
		}

		engine.addSystem(new RemoveAFromBSystem(1));

		assertEquals(5, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
		engine.update(0.16f);
		assertEquals(4, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
		engine.update(0.16f);
		assertEquals(3, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
		engine.update(0.16f);
		assertEquals(2, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());

		//From here, the system family shouldn't find the 2 A-only entities
		engine.update(0.16f);
		assertEquals(2, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
		engine.update(0.16f);
		assertEquals(2, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
	}


	@Test
	public void getEntitiesForInEntityListenerShouldReturnOnlyEntitisInFamily(){

		final PooledEngine engine = new PooledEngine();

		//Flexible so we can check at volumes, and high churn
		int aCount = 200;
		int bCount = 400;
		int total = aCount + bCount;
		int addsPerIteration = 5;
		int removesPerIteration = 6;
		int changePerIteration = removesPerIteration - addsPerIteration;

		for(int i = 0;i<aCount;i++){
			Entity a = engine.createEntity();
			a.add(engine.createComponent(UniquePooledCompnentA.class));
			engine.addEntity(a);
		}

		for(int i = 0;i<bCount;i++){
			Entity b = engine.createEntity();
			b.add(engine.createComponent(UniquePooledCompnentA.class));
			b.add(engine.createComponent(UniquePooledCompnentB.class));
			engine.addEntity(b);
		}

		engine.addSystem(new GenerateABSystem(addsPerIteration)); //Adds 2
		engine.addSystem(new RemoveAFromBSystem(removesPerIteration));
		engine.addSystem(new ListeningAndLookingForASystem(Family.all(UniquePooledCompnentB.class).get(), new IAssertEntityInFamily() {
			@Override
			public void assertAgainst(Entity entity) {
				assertTrue("Entity does not have value", entity.hasComponent(ComponentType.getFor(UniquePooledCompnentA.class)));
			}
		}));

		int iterations = Math.abs(bCount/changePerIteration);
		for(int i=0;i<iterations;i++){
			int expectedTotal = total-((changePerIteration)*i);

			assertEquals("Before Update Total should be right on iteration " + i, expectedTotal, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
			engine.update(0.16f);
			assertEquals("After Update Total Should be right on iteration " + i, expectedTotal - changePerIteration, engine.getEntitiesFor(Family.all(UniquePooledCompnentA.class).get()).size());
		}
	}


	private static class FollowerComponent implements Component, Poolable{

		@Override
		public void reset() {

		}
	}

	private static class EnemyComponent implements Component, Poolable{
		@Override
		public void reset() {

		}
	}

	private static class OtherComponent implements  Component, Poolable{

		@Override
		public void reset() {

		}
	}

	@Test
	public void removeDuringEntityRemovedHandledOk(){
		final PooledEngine engine = new PooledEngine();

		final Entity enemy = engine.createEntity();
		enemy.add(engine.createComponent(EnemyComponent.class));
		engine.addEntity(enemy);

		final Entity otherFollower = engine.createEntity();
		otherFollower.add(engine.createComponent(FollowerComponent.class));
		otherFollower.add(engine.createComponent(OtherComponent.class));
		engine.addEntity(otherFollower);

		final Entity enemyWithFollower = engine.createEntity();
		enemyWithFollower.add(engine.createComponent(EnemyComponent.class));
		enemyWithFollower.add(engine.createComponent(FollowerComponent.class));
		engine.addEntity(enemyWithFollower);


		engine.addEntityListener(Family.all(EnemyComponent.class).get(), new EntityListener() {
			private boolean isMidRemoval = false;
			private ComponentMapper<FollowerComponent> fm = ComponentMapper.getFor(FollowerComponent.class);
			@Override
			public void entityAdded(Entity entity) {

			}

			@Override
			public void entityRemoved(Entity entity) {
				ImmutableArray<Entity> followers = engine.getEntitiesFor(Family.all(FollowerComponent.class).get());

				for(Entity follower: followers){
					FollowerComponent fc = fm.get(follower);
					assertTrue("Follower Component is not defined for entity matching Family.all(FollowerComponent.class)", fc != null);
					//UNCOMMENT THIS TO REPRODUCE ISSUE #214
					//follower.removeAll();
					engine.removeEntity(follower);
				}
			}
		});

		engine.addSystem(new EntitySystem() {
			@Override
			public void update(float deltaTime) {
				super.update(deltaTime);
				getEngine().removeEntity(enemy);
			}
		});

		engine.update(0.16f);
		engine.update(0.16f);
	}
}
