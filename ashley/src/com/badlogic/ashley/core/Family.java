/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.core;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;


/**
 * Represents a group of {@link Component}s. It is used to describe what {@link Entity} objects an
 * {@link EntitySystem} should process.
 * 
 * Example: {@code Family.getFor(PositionComponent.class, VelocityComponent.class)}
 * 
 * Families can't be instantiate directly but must be accessed via {@code Family.getFor()}, this is
 * to avoid duplicate families that describe the same components.
 * 
 * @author Stefan Bachmann
 */
public class Family {
	/** The hashmap holding all families */
	private static ObjectMap<String, Family> families = new ObjectMap<String, Family>();
	private static int familyIndex = 0;
	
	/** Must contain all the components in the set */
	private final Bits all;
	/** Must contain at least one of the components in the set */
	private final Bits one;
	/** Cannot contain any of the components in the set */
	private final Bits exclude;
	/** Each family has a unique index, used for bitmasking */
	private final int index;
	
	/** Private constructor, use static method Family.getFamilyFor() */
	private Family(Bits all, Bits any, Bits exclude){
		this.all = all;
		this.one = any;
		this.exclude = exclude;
		this.index = familyIndex++;
	}
	
	/**
	 * @return This family's unique index
	 */
	public int getIndex(){
		return this.index;
	}
	
	/**
	 * @return Whether the entity matches the family requirements or not
	 */
	public boolean matches(Entity entity){
		Bits entityComponentBits = entity.getComponentBits();
		
		if(entityComponentBits.isEmpty())
			return false;
		
		for (int i = all.nextSetBit(0); i >= 0; i = all.nextSetBit(i+1)){
			if(!entityComponentBits.get(i))
				return false;
		}
		
		if (!one.isEmpty() && !one.intersects(entityComponentBits)) {
			return false;
		}
		
		if (!exclude.isEmpty() && exclude.intersects(entityComponentBits)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return The family matching the specified {@link Component} classes as a descriptor. Each set of component types will
	 * always return the same Family instance.
	 */
	@SafeVarargs
	public static Family getFor(Class<? extends Component> ...componentTypes){
		return getFor(ComponentType.getBitsFor(componentTypes), new Bits(), new Bits());
	}
	
	/**
	 * Returns a family with the passed {@link Component} classes as a descriptor. Each set of component types will
	 * always return the same Family instance.
	 *  
	 * @param all entities will have to contain all of the components in the set. See {@link ComponentType#getBitsFor(Class<? extends Component> ...)}.
	 * @param one entities will have to contain at least one of the components in the set.See {@link ComponentType#getBitsFor(Class<? extends Component> ...)}.
	 * @param exclude entities cannot contain any of the components in the set. See {@link ComponentType#getBitsFor(Class<? extends Component> ...)}.
	 * @return The family
	 */
	public static Family getFor(Bits all, Bits one, Bits exclude){
		String hash = getFamilyHash(all, one, exclude);
		Family family = families.get(hash, null);
		if(family == null){
			family = new Family(all, one, exclude);
			families.put(hash, family);
		}
		
		return family;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((all == null) ? 0 : all.hashCode());
		result = prime * result + ((one == null) ? 0 : one.hashCode());
		result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Family))
			return false;
		Family other = (Family) obj;
		return index == other.index && 
			   all.equals(other.all) &&
			   one.equals(other.one) &&
			   exclude.equals(other.exclude); 
	}
	
	private static String getFamilyHash(Bits all, Bits one, Bits exclude) {
		StringBuilder builder = new StringBuilder();
		if (!all.isEmpty()) {
			builder.append("{all:").append(getBitsString(all)).append("}");
		}
		if (!one.isEmpty()) {
			builder.append("{one:").append(getBitsString(one)).append("}");
		}
		if (!exclude.isEmpty()) {
			builder.append("{exclude:").append(getBitsString(exclude)).append("}");
		}
		return builder.toString();
	}
	
	private static String getBitsString(Bits bits) {
		StringBuilder builder = new StringBuilder();
		
		int numBits = bits.length();
		for (int i = 0; i < numBits; ++i) {
			builder.append(bits.get(i) ? "1" : "0");
		}
		
		return builder.toString();
	}
}
