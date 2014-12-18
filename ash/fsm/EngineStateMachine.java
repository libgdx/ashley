package ash.fsm;

import java.util.HashMap;
import java.util.Map;

import ash.core.Engine;
import ash.core.EntitySystem;
import ash.tools.Print;

/**
 * This is a state machine for the Engine. The state machine manages a set of states,
 * each of which has a set of System providers. When the state machine changes the state, it removes
 * Systems associated with the previous state and adds Systems associated with the new state.
 * 
 * @author Erik Borgers
 */
public class EngineStateMachine {
	public Engine engine;
	private final Map<String, EngineState> states; // contains the states. The key is the name of the state
	private EngineState currentState;

	/**
	 * Constructor. Creates an SystemStateMachine.
	 */
	public EngineStateMachine(Engine engine) {
		this.engine = engine;
		states = new HashMap<String, EngineState>(); // better beformance then a HasTable when non synchronised
		currentState = null;
	}

	/**
	 * Add a state to this state machine.
	 *
	 * @param name The name of this state - used to identify it later in the changeState method call.
	 * @param state The state.
	 * @return This state machine, so methods can be chained.
	 */
	public EngineStateMachine addState(String name, EngineState state) {
		states.put(name, state);
		return this;
	}

	/**
	 * Create a new state in this state machine.
	 *
	 * @param name The name of the new state - used to identify it later in the changeState method call.
	 * @return The new EntityState object that is the state. This will need to be configured with
	 * the appropriate component providers.
	 */
	public EngineState createState(String name) {
		EngineState state = new EngineState();
		states.put(name, state);
		return state;
	}

	/**
	 * Change to a new state. The Systems from the old state will be removed and the Systems
	 * for the new state will be added.
	 *
	 * @param name The name of the state to change to.
	 */
	public void changeState(String name) {
		EngineState newState = states.get(name);
		if (newState == null) {
			Print.fatal("Entity state " + name + " doesn't exist");
		}
		if (newState == currentState) {
			newState = null;
			return;
		}

		Map<Class<? extends EntitySystem>, ISystemProvider> toAdd;
		toAdd = new HashMap<Class<? extends EntitySystem>, ISystemProvider>();

		for (ISystemProvider provider : newState.getProviders()) {
			Class<? extends EntitySystem> id = provider.getIdentifier();
			toAdd.put(id, provider);
		}

		if (currentState != null) {
			for (ISystemProvider provider : currentState.getProviders()) {
				Object id = provider.getIdentifier(); //  
				ISystemProvider other = toAdd.get(id);

				if (other != null) {
					toAdd.remove(id);
				} else {
					engine.removeSystem(provider.getSystem());
				}
			}
		}
		for (ISystemProvider provider : toAdd.values()) {
			engine.addSystem(provider.getSystem(), provider.getPriority());
		}
		currentState = newState;

	}

}
