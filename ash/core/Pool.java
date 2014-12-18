package ash.core;

import java.util.Stack;

import ash.tools.Print;

/**
 * This internal class maintains a pool of deleted elements from a list for reuse by the engine. This reduces the overhead
 * from object creation and garbage collection.
 * 
 * Because nodes may be deleted from a NodeList while in use, by deleting Nodes from a NodeList
 * while iterating through the NodeList, the pool also maintains a cache of nodes that are added to the pool
 * but should not be reused yet. They are then released into the pool by calling the releaseCache method.
 * 
 * The same is true for Entity and EntitySystem
 * 
 * @author Erik Borgers
 */

public class Pool<T extends Poolable> {

	private Class<T> elClass;
	private Stack<T> pool;
	private Stack<T> cache;

	public Pool(Class<T> theClass) {
		this.elClass = theClass;
		pool = new Stack<T>();
		cache = new Stack<T>();
	}

	// this code does not work, you cannot refer to T (says the Java documentation), although it does seem to work with ListIteratingSystem (see code)!
	/*
	public Pool() {
		this.elClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		pool = new Stack<T>();
		cache = new Stack<T>();
	}*/

	/**
	 * Fetches a node from the pool (reused one or creates an instance)
	 */
	public final T get() {
		T el = null;
		if (pool.empty() == false) {
			return pool.pop();
		}
		try {
			el = elClass.newInstance();
		} catch (InstantiationException e) {
			Print.fatal("Creating Node of Class " + elClass.getName(), e);
		} catch (IllegalAccessException e) {
			Print.fatal("Creating Node of Class " + elClass.getName(), e);
		}
		return el;
	}

	/**
	 * Adds a node to the pool. It can be reused immediately
	 * The current implementation does not check if the element was already in the pool or cache;
	 */
	public final void dispose(T el) {
		// clear the node for reuse
		el.release();
		// now add it to the pool
		pool.push(el);
	}

	/**
	 * Adds a node to the cache. It can be reused only after a call to releaseCache
	 * The current implementation does not check if the element was already in the pool or cache;
	 */
	public final void cache(T el) {
		cache.push(el);
	}

	/**
	 * Releases all nodes from the cache into the pool
	 */
	public final void releaseCache() {
		while (cache.empty() == false) {
			T el = cache.pop();
			dispose(el);
			pool.push(el);
		}
	}

}
