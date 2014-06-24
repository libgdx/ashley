package ashley.utils;

/**
 * Interface for immutable associative arrays indexed by integers.
 * However, note that the indexed values could be modified if they are mutable.
 * 
 * @author David Saltares
 */
public interface ImmutableIntMap<V> {
	public int size();
	public V get(int key);
	public V get(int key, V defaultValue);
	public boolean containsValue(Object object, boolean identity);
	public boolean containsKey(int key);
	public int findKey(Object object, boolean identity, int notFound);
	
	public Entries<V> immutableEntries();
	public Values<V> immutableValues();
	public Keys immutableKeys();
	
	/**
	 * @author David Saltares
	 *
	 * Immutable entries for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Entries<V> extends Iterable<Entry<Integer, V>> { 
		public void reset();
		public Entry<Integer, V> next();
		public boolean hasNext();

	}
	
	/**
	 * @author David Saltares
	 *
	 * Immutable values for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Values<V>  extends Iterable<V> {
		public void reset();
		public V next();
		public boolean hasNext();
	}
	
	/**
	 * @author David Saltares
	 *
	 * Immutable keys for easy iteration.
	 * Calling {@code remove()} on the iterator will throw an {@code UnsupportedOperationException}.
	 */
	public static interface Keys extends Iterable<Integer> {
		public void reset();
		public Integer next();
		public boolean hasNext();
	}
}
