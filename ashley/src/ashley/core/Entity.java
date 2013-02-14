package ashley.core;

import java.util.BitSet;

import ashley.signals.Signal;
import ashley.utils.ObjectMap;

public class Entity {
	private ObjectMap<Class<? extends Component>, Component> components;
	private BitSet componentBits;
	private BitSet familyBits;
	
	public Signal<Entity> componentAdded;
	public Signal<Entity> componentRemoved;
	
	public Entity(){
		components = new ObjectMap<Class<? extends Component>, Component>();
		componentBits = new BitSet();
		familyBits = new BitSet();
		
		componentAdded = new Signal<>();
		componentRemoved = new Signal<>();
	}
	
	public Entity add(Component component){
		components.put(component.getClass(), component);
		
		componentBits.set(ComponentType.getIndexFor(component.getClass()));
		
		componentAdded.dispatch(this);
		return this;
	}
	
	public Component remove(Class<? extends Component> componentType){
		Component removeComponent = components.get(componentType, null);
		
		if(removeComponent != null){
			componentBits.clear(ComponentType.getIndexFor(componentType));
			
			componentRemoved.dispatch(this);
		}
		
		return removeComponent;
	}
	
	public <T extends Component> T getComponent(Class<T> componentType){
		return componentType.cast(components.get(componentType));
	}
	
	public BitSet getComponentBits(){
		return componentBits;
	}
	
	public BitSet getFamilyBits(){
		return familyBits;
	}
}
