
package com.badlogic.ashley.benchmark.ashley;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.ashley.benchmark.Constants;
import com.badlogic.ashley.benchmark.Constants.ComponentType;
import com.badlogic.ashley.benchmark.ashley.components.MovementComponent;
import com.badlogic.ashley.benchmark.ashley.components.PositionComponent;
import com.badlogic.ashley.benchmark.ashley.components.RadiusComponent;
import com.badlogic.ashley.benchmark.ashley.components.StateComponent;
import com.badlogic.ashley.benchmark.ashley.systems.CollisionSystem;
import com.badlogic.ashley.benchmark.ashley.systems.MovementSystem;
import com.badlogic.ashley.benchmark.ashley.systems.RemovalSystem;
import com.badlogic.ashley.benchmark.ashley.systems.StateSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class AshleyBenchmark extends AbstractBenchmark {
	private static Engine engineSmall;
	private static Engine engineMedium;
	private static Engine engineBig;

	@BeforeClass
	public static void prepare () {
		engineSmall = prepareEngine(Constants.ENTITIES_SMALL_TEST);
		engineMedium = prepareEngine(Constants.ENTITIES_MEDIUM_TEST);
		engineBig = prepareEngine(Constants.ENTITIES_BIG_TEST);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void ashleySmallTest () {
		runEngineTest(engineSmall);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void ashleyMediumTest () {
		runEngineTest(engineMedium);
	}

	@BenchmarkOptions(benchmarkRounds = Constants.BENCHMARK_ROUNDS, warmupRounds = Constants.WARMUP_ROUNDS)
	@Test
	public void ashleyBigTest () {
		runEngineTest(engineBig);
	}

	private void runEngineTest (Engine engine) {
		for (int i = 0; i < Constants.FRAMES; ++i) {
			engine.update(Constants.DELTA_TIME);
		}
	}

	private static Engine prepareEngine (int numEntities) {
		Engine engine = new Engine();

		engine.addSystem(new MovementSystem());
		engine.addSystem(new StateSystem());
		engine.addSystem(new CollisionSystem());
		engine.addSystem(new RemovalSystem());

		for (int i = 0; i < numEntities; ++i) {
			Entity entity = new Entity();

			if (Constants.shouldHaveComponent(ComponentType.POSITION, i)) {
				PositionComponent pos = new PositionComponent();
				pos.pos.x = MathUtils.random(Constants.MIN_POS, Constants.MAX_POS);
				pos.pos.y = MathUtils.random(Constants.MIN_POS, Constants.MAX_POS);
				entity.add(pos);
			}

			if (Constants.shouldHaveComponent(ComponentType.MOVEMENT, i)) {
				MovementComponent mov = new MovementComponent();
				mov.velocity.x = MathUtils.random(Constants.MIN_VEL, Constants.MAX_VEL);
				mov.velocity.y = MathUtils.random(Constants.MIN_VEL, Constants.MAX_VEL);
				mov.accel.x = MathUtils.random(Constants.MIN_ACC, Constants.MAX_ACC);
				mov.accel.y = MathUtils.random(Constants.MIN_ACC, Constants.MAX_ACC);

				entity.add(mov);
			}

			if (Constants.shouldHaveComponent(ComponentType.RADIUS, i)) {
				RadiusComponent rad = new RadiusComponent();
				rad.radius = MathUtils.random(Constants.MIN_RADIUS, Constants.MAX_RADIUS);
				entity.add(rad);
			}

			if (Constants.shouldHaveComponent(ComponentType.STATE, i)) {
				entity.add(new StateComponent());
			}

			engine.addEntity(entity);
		}

		return engine;
	}
}
