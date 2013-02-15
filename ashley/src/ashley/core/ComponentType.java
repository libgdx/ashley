package ashley.core;

import ashley.utils.ObjectMap;

public class ComponentType {
	private static int typeIndex = 0;
	private static ObjectMap<Class<? extends Component>, ComponentType> componentTypes = new ObjectMap<Class<? extends Component>, ComponentType>();
	
	private final int index;
	
	public ComponentType(){
		index = typeIndex++;
	}
	
	public int getIndex(){
		return index;
	}
	
    public static ComponentType getTypeFor(Class<? extends Component> c) {
        ComponentType type = componentTypes.get(c);

        if (type == null) {
        	type = new ComponentType();
	    	componentTypes.put(c, type);
        }

        return type;
    }

    public static int getIndexFor(Class<? extends Component> component) {
    	return getTypeFor(component).getIndex();
    }
}
