package ash.core;

import ash.tools.Print;

/**
 * An internal class for a linked list of entities. Used inside the framework for
 * managing the entities.
 * 
 * @author Erik Borgers
 */
class EntityList extends List<Entity> {

	@Override
	public void add(Entity system, int priority) {
		Print.fatal("Entity should be added with a priority"); // TODO 9 well, why not?
	}
}
