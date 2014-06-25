package com.badlogic.ashley.utils;

/**
 * Interface for arrays that cannot be modified.
 * However, note that mutable elements in the array could be modified.
 * 
 * @author David Saltares
 *
 */
public interface ImmutableArray<T> {
	public int getSize();
	public T get(int index);
	public boolean contains(T value, boolean identity);
	public int indexOf(T value, boolean identity);
	public int lastIndexOf(T value, boolean identity);
}
