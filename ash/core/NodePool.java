package ash.core;

import java.util.Stack;

import common.utils.Print;

/**
 * This internal class maintains a pool of deleted nodes (Erik: maybe systems and components too???) for reuse by the framework. This reduces the overhead
 * from object creation and garbage collection.
 * 
 * Because nodes may be deleted from a NodeList while in use, by deleting Nodes from a NodeList
 * while iterating through the NodeList, the pool also maintains a cache of nodes that are added to the pool
 * but should not be reused yet. They are then released into the pool by calling the releaseCache method.
 * 
 * TODO 7 more elegant if this were a ListElement Pool 
 */

public class NodePool {

	private Class<? extends Node> nodeClass;
	private Stack<Node> pool; // we cannot use NodeList, because in the Ash implementation a Node can only be on 1 list
	private Stack<Node> cache; // we cannot use NodeList, because in the Ash implementation a Node can only be on 1 list

	public NodePool(Class<? extends Node> nodeClass) {
		this.nodeClass = nodeClass;
		pool = new Stack<Node>();
		cache = new Stack<Node>();
	}

	/**
	 * Fetches a node from the pool (reused one or creates an instance)
	 */
	public final Node get() {
		Node newNode = null;
		if (pool.empty() == false) {
			return pool.pop();
		}
		try {
			newNode = nodeClass.newInstance();
		} catch (InstantiationException e) {
			Print.fatal("Creating Node of Class " + nodeClass.getName(), e);
		} catch (IllegalAccessException e) {
			Print.fatal("Creating Node of Class " + nodeClass.getName(), e);
		}
		return newNode;
	}

	/**
	 * Adds a node to the pool. It can be reused immideatley
	 */
	public final void dispose(Node node) {
		// clear the node for reuse
		node.release();
		// now add it to the pool
		pool.push(node);
	}

	/**
	 * Adds a node to the cache. It can be reused only after a call to releaseCache
	 */
	public final void cache(Node node) {
		cache.push(node);
	}

	/**
	 * Releases all nodes from the cache into the pool
	 */
	public final void releaseCache() {
		while (cache.empty() == false) {
			Node node = cache.pop();
			dispose(node);
			pool.push(node);
		}
	}

	/**
	internal class NodePool
	{
		private var tail : Node;
		private var nodeClass : Class;
		private var cacheTail : Node;
		private var components : Dictionary;

		// Creates a pool for the given node class.
		
		public function NodePool( nodeClass : Class, components : Dictionary )
		{
			this.nodeClass = nodeClass;
			this.components = components;
		}

		/**
		 * Fetches a node from the pool.
		 *
		internal function get() : Node
		{
			if ( tail )
			{
				var node : Node = tail;
				tail = tail.previous;
				node.previous = null;
				return node;
			}
			else
			{
				return new nodeClass();
			}
		}

		/**
		 * Adds a node to the pool.
		 *
		internal function dispose( node : Node ) : void
		{
			for each( var componentName : String in components )
			{
				node[ componentName ] = null; // ????? wat doet dit en waarom?
			}
			node.entity = null;
			
			node.next = null;
			node.previous = tail;
			tail = node;
		}
		
		/**
		 * Adds a node to the cache
		 *
		internal function cache( node : Node ) : void
		{
			node.previous = cacheTail;
			cacheTail = node;
		}
		
		/**
		 * Releases all nodes from the cache into the pool
		 *
		internal function releaseCache() : void
		{
			while( cacheTail )
			{
				var node : Node = cacheTail;
				cacheTail = node.previous;
				dispose( node );
			}
		}
	}
	*/

}
