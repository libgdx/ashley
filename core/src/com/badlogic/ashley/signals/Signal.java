package com.badlogic.ashley.signals;

import com.badlogic.ashley.utils.Array;

/**
 * A Signal is a basic event class then can dispatch an event to multiple listeners. It uses
 * generics to allow any type of object to be passed around on dispatch.
 * 
 * @author Stefan Bachmann
 */
public class Signal<T> {
	private Array<Listener<T>> listeners;
	
	public Signal(){
		listeners = new Array<Listener<T>>();
	}
	
	/**
	 * Add a Listener to this Signal
	 * @param listener The Listener to be added
	 */
	public void add(Listener<T> listener){
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener from this Signal
	 * @param listener The Listener to remove
	 */
	public void remove(Listener<T> listener){
		listeners.removeValue(listener, true);
	}
	
	/**
	 * Dispatches an event to all Listeners registered to this Signal
	 * @param object The object to send off
	 */
	public void dispatch(T object){
		for(int i=0; i<listeners.size; i++){
			listeners.get(i).receive(this, object);
		}
	}
}
