package ashley.signals;

import com.badlogic.gdx.utils.Array;

public class Signal<T> {
	private Array<Listener<T>> listeners;
	
	public Signal(){
		listeners = new Array<Listener<T>>();
	}
	
	public void add(Listener<T> listener){
		listeners.add(listener);
	}
	
	public void remove(Listener<T> listener){
		listeners.removeValue(listener, true);
	}
	
	public void dispatch(T object){
		for(int i=0; i<listeners.size; i++){
			listeners.get(i).receive(this, object);
		}
	}
}
