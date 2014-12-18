package ash.core;

/**
 * A collection of nodes.
 * 
 * <p>Systems within the engine access the components of entities via NodeLists. A NodeList contains
 * a node for each Entity in the engine that has all the components required by the node. To iterate
 * over a NodeList, start from the head and step to the next on each loop, until the returned value
 * is null.</p>
 * 
 * <p>for( var node : Node = nodeList.head; node; node = node.next )
 * {
 *   // do stuff
 * }</p>
 * 
 * <p>It is safe to remove items from a NodeList during the loop. When a Node is removed form the 
 * NodeList it's previous and next properties still point to the nodes that were before and after
 * it in the NodeList just before it was removed.</p>
 * 
 * @author Erik Borgers
 */

// TODO signalling for removing and adding is not implemented
public class NodeList extends List<Node> {

}
