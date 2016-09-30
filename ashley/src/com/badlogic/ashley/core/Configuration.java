package com.badlogic.ashley.core;

/**An interface for Objects that need to perform operations when added to or removed from the engine's configurations*/
	public interface Configuration {
		public boolean onAdd(Engine engine);
		public boolean onRemove(Engine engine);
}
