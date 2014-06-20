package ashley.utils;

import java.util.Iterator;

public interface ImmutableIntMap<V> {
	public int size();
	public V get(int key);
	public V get(int key, V defaultValue);
	public boolean containsValue(Object object, boolean identity);
	public boolean containsKey(int key);
	public int findKey(Object object, boolean identity, int notFound);
	
	public ImmutableEntries<V> immutableEntries();
	public ImmutableValues<V> immutableValues();
	public ImmutableKeys immutableKeys();
	
	public static interface ImmutableEntries<V> {
		public void reset();
		public Entry<Integer, V> next();
		public boolean hasNext();
		public Iterator<Entry<Integer, V>> iterator();
	}
	
	public static interface ImmutableValues<V> {
		public void reset();
		public V next();
		public boolean hasNext();
		public Iterator<V> iterator();
	}
	
	public static interface ImmutableKeys {
		public void reset();
		public Integer next();
		public boolean hasNext();
		public Iterator<Integer> iterator();
	}
}
