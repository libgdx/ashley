package ashley.core;

import com.badlogic.gdx.utils.ObjectMap;

public class ComponentType {
	private static int typeIndex = 0;
	private static ObjectMap<Class<? extends Component>, ComponentType> componentTypes = new ObjectMap<Class<? extends Component>, ComponentType>();
	
	private final int index;
	private final Class<? extends Component> type;
	
	public ComponentType(Class<? extends Component> type){
		index = typeIndex++;
		this.type = type;
	}
	
	public int getIndex(){
		return index;
	}
	
    public static ComponentType getTypeFor(Class<? extends Component> c) {
        ComponentType type = componentTypes.get(c);

        if (type == null) {
        	type = new ComponentType(c);
	    	componentTypes.put(c, type);
        }

        return type;
    }

    public static int getIndexFor(Class<? extends Component> component) {
    	return getTypeFor(component).getIndex();
    }
}
