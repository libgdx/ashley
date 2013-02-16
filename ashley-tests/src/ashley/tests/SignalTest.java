package ashley.tests;

import ashley.signals.Listener;
import ashley.signals.Signal;

public class SignalTest {
	public static void main(String[] args){
		Signal<String> signal = new Signal<String>();
		
		Listener<String> listener = new Listener<String>() {
			@Override
			public void receive(Signal<String> signal, String object) {
				System.out.println("Received event: " + object);
			}
		};
		
		signal.add(listener);
		signal.dispatch("Hello World!");
	}
}
