package ash.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ash.tools.Print;

/**
 * A collection of ListElements
 * 
 * To iterate over a List, start from the head and step to the next on each loop, until the returned value
 * is null or use "for" (iterator)</p>
 * 
 * <p>for( T node : List )
 * {
 *   // do stuff
 * }</p>
 * 
 * <p>It is safe to remove items from a List during the loop. When an Element is removed from the 
 * List it's previous and next properties still point to the nodes that were before and after
 * it in the List just before it was removed.</p>
 */

/* 
 * NOTE: this implementation allows you to put a ListElement on only ONE list, because next-previous info is in the elements itself, not in the List.
 * a Map, Set or ArrayList implementation would not have this disadvantages (and much less code!)
 * 
 * this has the following tradeoffs:
 * 
 * PLUS
 * it is very fast in remove and add actions. Not in get(), but get() is not an issue in Ash
 * there is a minimum of overhead 
 * there are no extra objects created
 * there is special code for sorting on priorities
 * order is preserved
 * you can safely remove while iterating
 * 
 * MINUS
 * it is "ugly" code, since the T itself contains unnecessary code for the List and the T can only be in one list
 * 
 */
public class List<T extends ListElement> implements Iterable<T> {
	private T head;
	private T tail;
	private int size;

	public List() {
		size = 0;
		head = null;
		tail = null;
	}

	public void add(T el) {
		if (el.getList() != null)
			Print.fatal("element is already on a list");

		if (isEmpty()) {
			head = el;
			tail = el;
			el.setNext(null);
			el.setPrevious(null);
		} else {
			// add it to the end
			tail.setNext(el);
			el.setPrevious(tail);
			el.setNext(null);
			tail = el;
			//Print.printnl("added nr " + (size + 1));
		}
		el.setList(this);
		size++;
	}

	/**
	 * add with priority. Adds this element just before an element with lower priority. 
	 * The lowest priority is the tail.
	 * 
	 * @param system
	 * @param priority
	 */

	@SuppressWarnings("unchecked")
	public void add(T el, int priority) {

		if (el.getList() != null)
			Print.fatal("element is already on a list");

		el.setPriority(priority);

		if (isEmpty()) {
			head = el;
			tail = el;
			el.setNext(null);
			el.setPrevious(null);
		} else {

			T current = head;
			if (current.getPriority() < el.getPriority()) {
				// insert this element at the start 
				el.setNext(current);
				el.setPrevious(null);
				current.setPrevious(el);
				head = el;
			} else {
				do {
					T next = (T) current.getNext(); // we could replace T by ListElement, but using T gives far better type checking for the rest of the code
					if (next == null) {
						// we are at the end of the list, add it at the end
						current.setNext(el);
						el.setPrevious(current);
						el.setNext(null);
						tail = el;
						break;
					}
					if (next.getPriority() < el.getPriority()) {
						// insert this element between current and next 
						current.setNext(el);
						el.setPrevious(current);
						el.setNext(next);
						next.setPrevious(el);
						break;
					}
					current = (T) current.getNext();
				} while (true);
			}
		}
		el.setList(this);
		size++;
	}

	/**
	 * remove el from the list. It is not released yet for garbage collection.
	 * 
	 * 
	 * @param el
	 */
	@SuppressWarnings("unchecked")
	public final void remove(T el) {
		if (el.getList() != this)
			Print.fatal("element is not on this list");

		if (head == el) {
			head = (T) head.getNext(); // we could replace T by list element, but using T gives far better type checking for the rest of the code
		}

		if (tail == el) {
			tail = (T) tail.getPrevious(); // we could replace T by list element, but using T gives far better type checking for the rest of the code
		}

		if (el.hasPrevious()) {
			el.getPrevious().setNext(el.getNext());
		}

		if (el.hasNext()) {
			el.getNext().setPrevious(el.getPrevious());
		}
		el.setList(null); // el might still point to pevious and next, because we can delete elements while looping
		size--;
	}

	@SuppressWarnings("unchecked")
	public final void clear() {
		while (!isEmpty()) {
			T el = head;
			head = (T) el.getNext(); // we could replace T by list element, but using T gives far better type checking for the rest of the code
			remove(el);
		}
		tail = null;
	}

	public final boolean isEmpty() {
		return head == null;
	}

	public final T getHead() {
		return head;
	}

	public final T getTail() {
		return tail;
	}

	public final int getSize() {
		return size;
	}

	@Override
	public Iterator<T> iterator() {
		return new LocalListIterator();
	}

	private class LocalListIterator implements Iterator<T> {

		private T current = head;
		private T lastReturned = null;

		@Override
		// the iterator points to the element to return (if any). 
		public boolean hasNext() {
			return current != null;
		}

		@SuppressWarnings("unchecked")
		@Override
		// return the current element and move the iterator forward
		public T next() {
			T next = current;
			if (next == null)
				throw new NoSuchElementException();
			current = (T) current.getNext(); // we could replace T by list element, but using T gives far better type checking for the rest of the code
			lastReturned = next;
			return next;
		}

		/**
		 * It is nice that this is implemented, but very dangerous too! Element might not be released from memory since it keeps pointing to the list
		 * The Pool is used instead of removing.
		 */
		@Override
		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();
			List.this.remove(lastReturned);
			lastReturned = null;
		}

	}

}