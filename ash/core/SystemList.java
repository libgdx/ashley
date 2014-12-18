package ash.core;

/**
 * Used internally, this is an ordered list of Systems for use by the engine update loop.
 * @author Erik Borgers
 */

import ash.tools.Print;

class SystemList extends List<EntitySystem> {

	@Override
	public void add(EntitySystem system) {
		Print.fatal("EntitySystem must be added with a priority");
	}

}
