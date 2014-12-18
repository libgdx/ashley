package ash.fsm;

import java.util.HashMap;
import java.util.Map;

import ash.core.Component;
import ash.core.Entity;

/**
 * This is a state machine for an Entity. The state machine manages a set of states,
 * each of which has a set of component providers. When the state machine changes the state, it removes
 * components associated with the previous state and adds components associated with the new state.
 * 
 * @author Erik Borgers
 */
public class EntityStateMachine {
	private Map<String, EntityState> states;
	/**
	 * The current state of the state machine.
	 */
	private EntityState currentState; // : EntityState;
	/**
	 * The entity whose state machine this is
	 */
	public Entity entity;

	/**
	 * Constructor. Creates an EntityStateMachine.
	 */
	public EntityStateMachine(Entity entity) {
		this.entity = entity;
		states = new HashMap<String, EntityState>(); // better beformance then a HasTable when non synchronised
	}

	/**
	 * Add a state to this state machine.
	 * 
	 * @param name The name of this state - used to identify it later in the changeState method call.
	 * @param state The state.
	 * @return This state machine, so methods can be chained.
	 */
	public EntityStateMachine addState(String name, EntityState state) {
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
	public EntityState createState(String name) {
		EntityState state = new EntityState();
		states.put(name, state);
		return state;
	}

	/**
	 * Change to a new state. The components from the old state will be removed and the components
	 * for the new state will be added.
	 * 
	 * @param name The name of the state to change to.
	 */
	public void changeState(String name) {
		EntityState newState = states.get(name);
		if (newState == null) {
			throw (new Error("Entity state " + name + " doesn't exist"));
		}
		if (newState == currentState) {
			newState = null;
			return;
		}

		Map<Class<? extends Component>, IComponentProvider> toAdd = null;

		if (currentState != null) {
			toAdd = new HashMap<Class<? extends Component>, IComponentProvider>();
			for (Class<? extends Component> type : newState.getProviders().keySet()) {
				toAdd.put(type, newState.getProviders().get(type));
			}

			for (Class<? extends Component> type : currentState.getProviders().keySet()) {
				IComponentProvider other = toAdd.get(type);

				// if the currentState already has the Component of that type, do not replace it
				if ((other != null) && (other.getIdentifier() == currentState.get(type).getIdentifier())) {
					toAdd.remove(type); // remove from lists to add later
				} else {
					entity.remove(type); // remove the state from current. Note that this not remove Nodes (we do not want that for state entities, since no systems handle them) 
				}
			}
		} else {
			toAdd = newState.getProviders();
		}

		for (Class<? extends Component> type : toAdd.keySet()) {
			entity.add(type, toAdd.get(type).getComponent()); // Note that this not create Nodes (we do not want that for state entities, since no systems handle them)
		}
		currentState = newState;
	}
}
