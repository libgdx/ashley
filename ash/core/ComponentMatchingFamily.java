package ash.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ash.tools.Print;

/**
 * The default class for managing a NodeList. This class creates the NodeList and adds and removes nodes to/from the
 * list as the entities and the components in the engine change.
 * 
 * It uses the basic entity matching pattern of an entity system - entities are added to the list if they contain
 * components matching all the public properties of the node class.
 * 
 * In essence, a family manages a NodeList. So whenever a System requests a collection of nodes, the engine looks for a
 * family that is managing such a collection. If it doesn't have one, it creates a new one. Either way, it then passes
 * the NodeList managed by the family back to the System that requested it.
 * 
 * The family is initialized with a node type. It uses reflection to determine what components that node type requires.
 * Whenever an entity is added to the engine it is passed to every family in the engine, and the families determine
 * whether that entity has the necessary components to join that family. If it does then the family will add it to its
 * NodeList.
 * 
 * If the components on an entity are changed, by adding or removing a component, then again the engine checks with all
 * the families and each family tests whether to add or remove the entity to/from its NodeList. Finally, when an entity
 * is removed form the engine the engine informs all the families and any family that has the entity in its NodeList
 * will remove it.
 * 
 * @author Erik Borgers
 * 
 */
class ComponentMatchingFamily implements IFamily {

	private final NodeList nodes; // list of all nodes
	private final Map<Entity, Node> entities; // all the entities with this node type (specific set of components)
	private final Class<? extends Node> nodeClass; // the Node Class that this IFamily collects
	private final ArrayList<Class<? extends Component>> components; // Component Classes in this Node. Key is the Class of the Component
	private final Pool<Node> nodePool;
	private final Engine engine;

	private final ArrayList<EntitySystem> watchers; // Systems watching this family

	/**
	 * The family is initialised with a node type. It uses reflection to determine what components that node type
	 * requires.
	 * 
	 * Whenever an entity is added to the engine it is passed to every family in the engine, and the families determine
	 * whether that entity has the necessary components to join that family. If it does then the family will add it to
	 * its NodeList.
	 * 
	* The constructor. Creates a ComponentMatchingFamily to provide a NodeList for the
		 * given node class.
		 * 
		 * @param nodeClass The type of node to create and manage a NodeList for.
		 * 
		 * @param engine The engine that this family is managing the NodeList for.
	 */
	@SuppressWarnings("unchecked")
	public ComponentMatchingFamily(Class<? extends Node> nodeClass, Engine engine) {
		this.nodeClass = nodeClass;
		this.engine = engine;
		this.nodePool = new Pool(nodeClass); // the Pool must create nodeClass type of Nodes!
		this.entities = new HashMap<Entity, Node>(); // better performance then a HasTable when non synchronised
		this.nodes = new NodeList();
		this.components = new ArrayList<Class<? extends Component>>();
		this.watchers = new ArrayList<EntitySystem>();
		// find the public fields of class Component in this Node and add those to the Component's list. This is done by reflection
		Field[] fields = nodeClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			if (!Modifier.isPublic(modifiers)) { // must be public, otherwise cannot be assigned
				Print.fatal("Node class " + nodeClass.getName() + " contains non-public field " + fields[i].getName());
			}
			// this is a public field
			Class<?> fieldClass = fields[i].getType(); // fields[i].getDeclaringClass();
			if (!Component.class.isAssignableFrom(fieldClass)) {
				Print.fatal("There is a field of class " + fieldClass.getName() + " in the declaration of Node class "
						+ nodeClass.getName() + " that is not a Component");
			}

			@SuppressWarnings("unchecked")
			Class<? extends Component> componentClass = (Class<? extends Component>) fieldClass;
			if (components.contains(componentClass)) {
				Print.fatal("The Component field " + componentClass.getName()
						+ " occurs multiple times in the declaration of Node class " + nodeClass.getName());
			}
			components.add(componentClass);

		}
	}

	@Override
	public void setNodeClass(Class<? extends Node> nodeClass) {
		Print.fatal("Do not call this for a ComponentMatchingFamily. construtor has done this");
	}

	@Override
	public void setEngine(Engine engine) {
		Print.fatal("Do not call this for a ComponentMatchingFamily. constructor has done this");
	}

	@Override
	public Class<? extends Node> getNodeClass() {
		return nodeClass;
	}

	@Override
	/**
	 * The nodelist managed by this family. This is a reference that remains valid always
	 * since it is retained and reused by Systems that use the list. i.e. we never recreate the list,
	 * we always modify it in place.
	 */
	public NodeList getNodeList() {
		return nodes;
	}

	@Override
	/**
	 * Called by the engine when an entity has been added to it. We check if the entity should be in
	 * this family's NodeList and add it if appropriate. Return a new Node or null otherwise.
	 */
	public Node newEntity(Entity entity) {
		return addIfMatch(entity);
	}

	@Override
	/**
	 * Called by the engine when an entity has been rmoved from it. We check if the entity is in
	 * this family's NodeList and remove it if so. Return removed Node or null otherwise.
	 */
	public Node removeEntity(Entity entity) {
		return removeIfMatch(entity);
	}

	/**
	 * add the Entity and create a Node in case the Component(s) of the Entity match

	 * If the entity is not in this family's NodeList, tests the components of the entity to see
	 * if it should be in this NodeList and adds it if so.
	 * 
	 * @param entity
	 * @return
	 */
	private Node addIfMatch(Entity entity) {
		if (entities.containsKey(entity))
			return null; // already on the list, nothing new to create

		// Print.message("nr of components on this family =" + components.size());
		for (Class<? extends Component> klass : components) {
			if (!entity.has(klass)) {
				return null; // no match, so no node to create for this Entity
			}
		}

		// this Entity has all Component(s) needed for this Family. So create a new Node of the correct type
		Node node = nodePool.get();
		// now make all Components fields in the Node point to the corresponding Components in the Entity
		node.setEntity(entity);

		/* Erik: Ash remembers the component instances, we choose not have to do so because they are not used
		 * 
		 * for ( componentClass in components )
			{
				node[components[componentClass]] = entity.get( componentClass );
			}
		 */

		entities.put(entity, node);
		nodes.add(node);
		return node;
	}

	private Node removeIfMatch(Entity entity) {
		Node removedNode = entities.remove(entity);
		if (removedNode == null)
			return null; // it was not on the list anyway
		nodes.remove(removedNode);
		if (engine.updating) {
			nodePool.cache(removedNode);
			// engine.updateComplete.add( releaseNodePoolCache ); // Ash uses a signal, but this engine does a call just before ending the update
		} else {
			nodePool.dispose(removedNode);
		}

		return removedNode;
	}

	@Override
	/**
	 * Called by the engine when a component has been added to an entity. We check if the entity is not in
	 * this family's NodeList and should be, and add it if appropriate.
	 */
	public Node componentAddedToEntity(Entity entity, Class<? extends Component> klass) {
		if (components.contains(klass))
			return addIfMatch(entity);
		return null;
	}

	@Override
	/**
	 * Called by the engine when a component has been removed from an entity. We check if the removed component
	 * is required by this family's NodeList and if so, we check if the entity is in this this NodeList and
	 * remove it if so.
	 */
	public Node componentRemovedFromEntity(Entity entity, Class<? extends Component> klass) {
		if (components.contains(klass))
			return removeIfMatch(entity);
		return null;
	}

	@Override
	/**
	 * Removes all nodes from the NodeList.
	 */
	public void cleanUp() {
		for (Node node : nodes) {
			entities.remove(node.getEntity());
		}
		nodes.clear();
	}

	@Override
	public void registerSystem(EntitySystem system) {
		watchers.add(system);

	}

	@Override
	public void unregisterSystem(EntitySystem system) {
		watchers.remove(system);

	}

	@Override
	public Collection<EntitySystem> isBeingWatched() {
		return watchers;
	}

	@Override
	public void updated() {
		nodePool.releaseCache();
	}

}
