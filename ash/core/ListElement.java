package ash.core;

/**
 * 
 * @author Erik Borgers
 *
 */

public abstract class ListElement implements Poolable {

	private ListElement previous;
	private ListElement next;
	@SuppressWarnings("rawtypes")
	private List list; // remember the list where the element is on (there can be only one for an element!)
	private int priority;

	ListElement() {
		previous = null;
		next = null;
		setList(null);
		priority = 0;
	}

	final void setNext(ListElement el) {
		next = el;
	}

	final void setPrevious(ListElement el) {
		previous = el;
	}

	@SuppressWarnings("rawtypes")
	final void setList(List list) {
		this.list = list;
	}

	@SuppressWarnings("rawtypes")
	final List getList() {
		return list;
	}

	public final ListElement getNext() {
		return next;
	}

	public final ListElement getPrevious() {
		return previous;
	}

	public final boolean hasPrevious() {
		return previous != null;
	}

	public final boolean hasNext() {
		return next != null;
	}

	public final void setPriority(int p) {
		priority = p;
	}

	public final int getPriority() {
		return priority;
	}

	/**
	 * release/reset all resources not to be pooled
	 */
	public abstract void releaseResources();

	/**
	 * cleans up all resources and makes it available for retrieval garbage collection
	 * 
	 * be careful, since ListElement's might be pooled or looped about 
	 * 
	 * only override, do not call it directly but leave this to the Engine
	 */
	public void release() {
		previous = null;
		next = null;
		setList(null);
		releaseResources();
	}

}
