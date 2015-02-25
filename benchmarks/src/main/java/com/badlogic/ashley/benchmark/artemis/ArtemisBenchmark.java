
package com.badlogic.ashley.benchmark.artemis;

import org.junit.BeforeClass;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.ashley.benchmark.Constants;
import com.badlogic.ashley.benchmark.Constants.ComponentType;
import com.badlogic.ashley.benchmark.artemis.components.MovementComponent;
import com.badlogic.ashley.benchmark.artemis.components.PositionComponent;
import com.badlogic.ashley.benchmark.artemis.components.RadiusComponent;
import com.badlogic.ashley.benchmark.artemis.components.StateComponent;
import com.badlogic.ashley.benchmark.artemis.systems.CollisionSystem;
import com.badlogic.ashley.benchmark.artemis.systems.MovementSystem;
import com.badlogic.ashley.benchmark.artemis.systems.RemovalSystem;
import com.badlogic.ashley.benchmark.artemis.systems.StateSystem;
import com.badlogic.gdx.math.MathUtils;
import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class ArtemisBenchmark extends AbstractBenchmark {
	private static World worldSmall;
	private static World worldMedium;
	private static World worldBig;

	@BeforeClass
	public static void prepare () {
		worldSmall = prepareWorld(Constants.ENTITIES_SMALL_TEST);
		worldMedium = prepareWorld(Constants.ENTITIES_MEDIUM_TEST);
		worldBig = prepareWorld(Constants.ENTITIES_BIG_TEST);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void worldSmallTest () {
		runWorldTest(worldSmall);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void worldMediumTest () {
		runWorldTest(worldMedium);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void worldBigTest () {
		runWorldTest(worldBig);
	}

	private void runWorldTest (World world) {
		for (int i = 0; i < Constants.FRAMES; ++i) {
			world.setDelta(Constants.DELTA_TIME);
			world.process();
		}
	}

	private static World prepareWorld (int numEntities) {
		World world = new World();

		world.setSystem(new MovementSystem());
		world.setSystem(new StateSystem());
		world.setSystem(new CollisionSystem());
		world.setSystem(new RemovalSystem());

		world.initialize();

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = world.createEntity();

			if (Constants.shouldHaveComponent(ComponentType.POSITION, i)) {
				PositionComponent pos = new PositionComponent();
				pos.pos.x = MathUtils.random(Constants.MIN_POS, Constants.MAX_POS);
				pos.pos.y = MathUtils.random(Constants.MIN_POS, Constants.MAX_POS);
				entity.addComponent(pos);
			}

			if (Constants.shouldHaveComponent(ComponentType.MOVEMENT, i)) {
				MovementComponent mov = new MovementComponent();
				mov.velocity.x = MathUtils.random(Constants.MIN_VEL, Constants.MAX_VEL);
				mov.velocity.y = MathUtils.random(Constants.MIN_VEL, Constants.MAX_VEL);
				mov.accel.x = MathUtils.random(Constants.MIN_ACC, Constants.MAX_ACC);
				mov.accel.y = MathUtils.random(Constants.MIN_ACC, Constants.MAX_ACC);

				entity.addComponent(mov);
			}

			if (Constants.shouldHaveComponent(ComponentType.RADIUS, i)) {
				RadiusComponent rad = new RadiusComponent();
				rad.radius = MathUtils.random(Constants.MIN_RADIUS, Constants.MAX_RADIUS);
				entity.addComponent(rad);
			}

			if (Constants.shouldHaveComponent(ComponentType.STATE, i)) {
				entity.addComponent(new StateComponent());
			}

			world.addEntity(entity);
		}

		return world;
	}
}
