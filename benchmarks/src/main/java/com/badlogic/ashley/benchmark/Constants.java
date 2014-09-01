package com.badlogic.ashley.benchmark;

public class Constants {
	public static enum ComponentType {
		POSITION,
		MOVEMENT,
		RADIUS,
		STATE,
	};
	
	public static final int FRAMES = 100;
	public static final float DELTA_TIME = 1.0f / 60.0f;
	
	public static final int BENCHMARK_ROUNDS = 20;
	public static final int WARMUP_ROUNDS = 20;
	
	public static final int ENTITIES_SMALL_TEST = 5000;
	public static final int ENTITIES_MEDIUM_TEST = 10000;
	public static final int ENTITIES_BIG_TEST = 20000;
	
	public static final float MIN_RADIUS = 0.1f;
	public static final float MAX_RADIUS = 10.0f;
	public static final float MIN_POS = -10.f;
	public static final float MAX_POS = 10.0f;
	public static final float MIN_VEL = -1.0f;
	public static final float MAX_VEL = 1.0f;
	public static final float MIN_ACC = -0.1f;
	public static final float MAX_ACC = 0.1f;
	
	public static final int FRAMES_PER_REMOVAL = 10;
	
	public static boolean shouldHaveComponent(ComponentType type, int index) {
		switch (type) {
		case MOVEMENT:
			return index % 4 == 0;
		case RADIUS:
			return index % 3 == 0;
		case STATE:
			return index % 2 == 0;
		case POSITION:
			return true;
		}
		
		return false;
	}
}
