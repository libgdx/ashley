package ashley.utils;

public interface ImmutableArray<T> {
	public int getSize();
	public T get(int index);
	public boolean contains(T value, boolean identity);
	public int indexOf(T value, boolean identity);
	public int lastIndexOf(T value, boolean identity);
}
