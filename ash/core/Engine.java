package ash.core;

import java.util.ArrayList;
import java.util.HashMap;

import ash.tools.Print;

/**
 * The Engine class is the central point for creating and managing your game state. Add
 * entities and systems to the engine, and fetch families of nodes from the engine.
 * 
 * @author Erik Borgers
 */
public class Engine {
	private final HashMap<String, Entity> entityNames; // list of entities ordered by name
	private final EntityList entityList; // an unordered list of entities
	private final SystemList systemList; // an ordered list of systems (ordered on priority!)
	private final HashMap<Class<? extends Node>, IFamily> families; // lists of Node(s) of a certain Node class
	private Pool<EntitySystem> systemPool;
	private Pool<Entity> entityPool;
	/**
	 * Indicates if the engine is currently in its update loop.
	 */
	public boolean updating;

	/**
	 * Dispatched when the update loop ends. If you want to add and remove systems from the
	 * engine it is usually best not to do so during the update loop. To avoid this you can
	 * listen for this signal and make the change when the signal is dispatched.
	 * 
	 * Erik: Signalling is not implemented. Better override the update() method in a class based on this class (on which you can subscibe) and add some messaging mechanism of your choosing
	 */
	// public Signal0 updateComplete;

	/**
	 * The class used to manage node lists (Erik: create new families). In most cases the default class is sufficient
	 * but it is exposed here so advanced developers can choose to create and use a 
	 * different implementation.
	 * 
	 * The class must implement the Family interface.
	 */
	private Class<? extends IFamily> alternativeFamilyClass = null; // null means: use the default =  a ComponentMatchingFamily 

	public void setAlternativeFamily(Class<? extends IFamily> alternative) {
		alternativeFamilyClass = alternative;
	}

	public Engine() {
		entityList = new EntityList();
		systemList = new SystemList();
		families = new HashMap<Class<? extends Node>, IFamily>();
		entityNames = new HashMap<String, Entity>();
		entityPool = new Pool<Entity>(Entity.class);
		systemPool = new Pool<EntitySystem>(EntitySystem.class);
	}

	///////// entity management

	/**
	 * Add an entity to the engine.
	 * 
	 * @param entity The entity to add.
	 */
	public void addEntity(Entity entity) {
		entityList.add(entity);
		int l = entityNames.size();
		entityNames.put(entity.getName(), entity);
		if (entityNames.size() == l)
			Print.fatal("Entity with name <" + entity.getName() + "> already exists.");

		for (IFamily iFamily : families.values()) {
			Node newNode = iFamily.newEntity(entity);

			// for every watching System, notify a Node was added
			if (newNode != null) {
				for (EntitySystem system : iFamily.isBeingWatched()) {
					system.nodeAdded(newNode);
				}
			}
		}
	}

	/**
	 * Remove an entity from the engine.
	 * 
	 * @param entity The entity to remove.
	 */
	public void removeEntity(Entity entity) {
		for (IFamily iFamily : families.values()) {
			Node removedNode = iFamily.removeEntity(entity);

			// for every watching System, notify a Node was removed
			if (removedNode != null) {
				for (EntitySystem system : iFamily.isBeingWatched()) {
					system.nodeRemoved(removedNode);
				}
			}
		}

		entityList.remove(entity);
		entityNames.remove(entity.getName());
		entityPool.cache(entity);
		// we do not actively clear the entity yet, because it might be referenced elsewhere (in a Component or even outside the kernel). If not, let the garbage collector do its work!
	}

	/** 
	 * call this function when the name is changed for internal housekeeping. If this occurs, the hashmap must be rebuild. 
	 * We do not allow name change in this implementation. What is the need for it? 
	*/
	void entityNameChanged(Entity entity, String oldName) {

		Print.fatal("named changed event handling not implemented");
		/*
		
		if( entityNames[ oldName ] == entity )
		{
			delete entityNames[ oldName ];
			entityNames[ entity.name ] = entity;
		}
		*/
	}

	/**
	 * Get an entity based on its name.
	 * 
	 * @param name The name of the entity
	 * @return The entity, or null if no entity with that name exists on the engine
	 */
	public Entity getEntityByName(String name) {
		return entityNames.get(name);
	}

	/**
	 * Remove all entities from the engine.
	 */
	public void removeAllEntities() {
		while (!entityList.isEmpty()) {
			removeEntity(entityList.getHead());
		}
	}

	/**
	 * Returns a vector (copy you can manipulate) containing all the entities in the engine.
	 */
	public ArrayList<Entity> getEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();

		for (Entity entity : entityList) {
			entities.add(entity);
		}
		return entities;
	}

	// callback: if during the life team of an Entity components are added or removed, this means you have to update the families
	// This function seems not used. In Ash, this is used as a callback in Entity. Important?
	void componentAdded(Entity entity, Class<? extends Component> klass) {
		for (IFamily iFamily : families.values()) {
			iFamily.componentAddedToEntity(entity, klass);
		}
	}

	// callback: if during the life team of an Entity components are added or removed, this means you have to update the families
	// This function seems not used. In Ash, this is used as a callback in Entity. Important?
	void componentRemoved(Entity entity, Class<? extends Component> klass) {
		for (IFamily iFamily : families.values()) {
			iFamily.componentRemovedFromEntity(entity, klass);
		}
	}

	/**
	 * Get a collection of nodes from the engine, based on the type of the node required.
	 * 
	 * <p>The engine will create the appropriate NodeList if it doesn't already exist and 
	 * will keep its contents up to date as entities are added to and removed from the
	 * engine.</p>
	 * 
	 * <p>If a NodeList is no longer required, release it with the releaseNodeList method.</p>
	 * 
	 * @param nodeClass The type of node required.
	 * @return A linked list of all nodes of this type from all entities in the engine.
	 * Hand over the list of Node(s) maintained by the IFamily for this class of Node This is not a copy, so do not
	 * interfere with it!
	 * 
	 */
	public NodeList getNodeList(Class<? extends Node> nodeClass) {
		IFamily family = families.get(nodeClass);
		if (family != null)
			return family.getNodeList();
		return null;
	}

	/**
	 * If a NodeList is no longer required, this method will stop the engine updating
	 * the list and will release all references to the list within the framework
	 * classes, enabling it to be garbage collected.
	 * 
	 * <p>It is not essential to release a list, but releasing it will free
	 * up memory and processor resources.</p>
	 * 
	 * @param nodeClass The type of the node class if the list to be released.
	 */
	public void releaseNodeList(Class<? extends Node> nodeClass) {
		IFamily toRemove = families.get(nodeClass);

		if (toRemove != null) {
			toRemove.cleanUp();
			families.remove(toRemove);
		}
	}

	///////// systems management

	/**
	 * Add a system to the engine, and set its priority for the order in which the
	 * systems are updated by the engine update loop.
	 * 
	 * <p>The priority dictates the order in which the systems are updated by the engine update 
	 * loop. Lower numbers for priority are updated first. i.e. a priority of 1 is 
	 * updated before a priority of 2.</p>
	 * 
	 * @param system The system to add to the engine.
	 * @param priority The priority for updating the systems during the engine loop. A 
	 * lower number means the system is updated sooner.
	 */
	public void addSystem(EntitySystem system, int priority) {
		systemList.add(system, priority); // note that the systemList will arrange the order according to priority

		// add a Family for the Node Class if it does not already exist 
		IFamily newFamily = families.get(system.getNodeClass());
		if (newFamily == null) {

			if (alternativeFamilyClass != null) {
				try {
					newFamily = alternativeFamilyClass.newInstance();
				} catch (InstantiationException e) {
					Print.fatal("could not create Family");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Print.fatal("could not create Family");
					e.printStackTrace();
				}
				newFamily.setEngine(this);
				newFamily.setNodeClass(system.getNodeClass());
			} else {
				// use the default ComponentMatchingFamily
				newFamily = new ComponentMatchingFamily(system.getNodeClass(), this);
			}
			families.put(system.getNodeClass(), newFamily);
		}
		newFamily.registerSystem(system);
		system.addToEngine(this);
	}

	/**
	 * Get the system instance of a particular type from within the engine.
	 * 
	 * @param type The type of system
	 * @return The instance of the system type that is in the engine, or
	 * null if no systems of this type are in the engine.
	 *
	 * return the first system of the desired Class
	 * 
	 */
	public EntitySystem getSystem(Class<? extends EntitySystem> systemClass) {
		for (EntitySystem system : systemList) {
			if (system.getClass().equals(systemClass)) {
				return system;
			}
		}
		return null;
	}

	/**
	 * Returns a vector containing all the systems in the engine.
	 * NOTE: Ash returns a copy. This function return the internal list
	 */
	public ArrayList<EntitySystem> getSystems() {
		ArrayList<EntitySystem> systems = new ArrayList<EntitySystem>();
		for (EntitySystem system : systemList) {
			systems.add(system);
		}
		return systems;
	}

	/**
	 * Remove a system from the engine.
	 * 
	 * @param system The system to remove from the engine.
	 */
	public void removeSystem(EntitySystem system) {
		IFamily family = families.get(system.getNodeClass());
		family.unregisterSystem(system);
		systemList.remove(system);
		// maybe we could remove the family too, if it is no longer needed, but don't bother for now. 
		system.removeFromEngine(this);
		// we do not need to resort
		systemPool.cache(system);
	}

	/**
	 * Remove all systems from the engine.
	 */
	public void removeAllSystems() {
		while (!systemList.isEmpty()) {
			EntitySystem system = systemList.getHead();
			removeSystem(system);
		}
	}

	// Non Ash: probably not used
	public void clear() {
		removeAllEntities();
		removeAllSystems();
	}

	/**
	 * Update the engine. This causes the engine update loop to run, calling update on all the systems in the engine.
	 * 
	 * <p>
	 * The package net.richardlord.ash.tick contains classes that can be used to provide a steady or variable tick that
	 * calls this update method.
	 * </p>
	 * 
	 * @time The duration, in seconds, of this update step.
	 */
	public void update(float delta) {

		updating = true;
		for (EntitySystem system : systemList) {
			system.update(delta);
		}
		// do cleaning up, eg pool stuff 
		for (IFamily family : families.values()) {
			family.updated();
		}
		// clean up systems and entities
		entityPool.releaseCache();
		systemPool.releaseCache();
		updating = false;

		// updateComplete.dispatch(); 
		// NOTE Erik Borgers: signalling is not implemented in the core. You can implement a subclass of Engine and override this method and use a messaging system of your own liking
	}

}
