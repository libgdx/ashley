package ashley.core;

import java.util.BitSet;

import ashley.utils.ObjectMap;

public class Family {
	private static ObjectMap<String, Family> families = new ObjectMap<String, Family>();
	private static int familyIndex = 0;
	
	private final BitSet bits;
	private final int index;
	
	private Family(BitSet bits){
		this.bits = bits;
		this.index = familyIndex++;
	}
	
	public int getFamilyIndex(){
		return this.index;
	}
	
	public boolean matches(Entity e){
		BitSet entityComponentBits = e.getComponentBits();
		
		if(entityComponentBits.isEmpty())
			return false;
		
		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1)){
			if(!entityComponentBits.get(i))
				return false;
		}
		
		return true;
	}
	
	@SafeVarargs
	public static Family getFamilyFor(Class<? extends Component> ...componentTypes){
		BitSet bits = new BitSet();
		
		for(int i=0; i<componentTypes.length; i++){
			bits.set(ComponentType.getIndexFor(componentTypes[i]));	
		}
		
		String hash = bits.toString();
		Family family = families.get(hash, null);
		if(family == null){
			family = new Family(bits);
			families.put(hash, family);
		}
		
		return family;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Family){
			if(obj == this)
				return true;
			else return bits.equals(((Family)obj).bits);
		}
		return false;
	}
}
