package ash.tools;

import java.lang.reflect.ParameterizedType;

import ash.core.Engine;
import ash.core.EntitySystem;
import ash.core.Node;
import ash.core.NodeList;

/**
 * A useful class for systems which simply iterate over a set of nodes, performing the same action on each node. This
 * class removes the need for a lot of boilerplate code in such systems. Extend this class and pass the node type and a
 * node update method into the constructor. The node update method will be called once per node on the update cycle with
 * the node instance and the frame time as parameters. e.g.
 * 
 * <code>package
 * {
 *   public class MySystem extends ListIteratingSystem
 *   {
 *     public function MySystem()
 *     {
 *       super( MyNode.class );
 *     }
 *     
 *     private function update( MyNode node, float time ) : void
 *     {
 *       // process the node here
 *     }
 *   }
 * }</code>
 * 
 * @Erik Borgers
 * 
 */
public abstract class ListIteratingSystem<NodeType> extends EntitySystem {

	private NodeList nodeList;

	private Class<NodeType> typeOfNodeType;

	// see http://stackoverflow.com/questions/4837190/java-generics-get-class?lq=1
	// we try to avoid that in the constructor you must repeat the type of node, since it is already in <NodeType>
	public ListIteratingSystem() {
		super();
		this.typeOfNodeType = (Class<NodeType>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		// Print.message("type of class = " + this.typeOfNodeType.getName()); // TODO 2 remove
		setNodeClass((Class<? extends Node>) typeOfNodeType);
	}

	@Override
	public void releaseResources() {
		typeOfNodeType = null;
		super.releaseResources();
	}

	/*
	public ListIteratingSystem(Class<? extends Node> nodeClass) {
		super(nodeClass);
	}
	*/

	@Override
	public void addToEngine(Engine engine) {
		super.addToEngine(engine);
		nodeList = engine.getNodeList(this.getNodeClass()); // get the NodeList for this System (NodeList 's are shared between Systems)
	}

	@Override
	public void removeFromEngine(Engine engine) {
		super.removeFromEngine(engine);
		nodeList = null;
	}

	public int size() {
		return nodeList.getSize();
	}

	@Override
	public final void update(float time) {
		this.enter();
		for (Node node : nodeList) {
			Print.message("updating node " + node + " in list iterator " + this);
			this.updateNode((NodeType) node, time);
		}
		this.exit();
	}

	/**
	 * this method is called just before the System starts updating all its nodes
	 */
	public void enter() {
	};

	/**
	 * this method is called after the System has updated all its nodes
	 */
	public void exit() {
	};

	/**
	 * this method is called when the System updates a node
	 */
	public abstract void updateNode(NodeType node, float time);
}
