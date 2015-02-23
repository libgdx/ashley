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
 * Represents a group of {@link Component}s. It is used to describe what {@link Entity} objects an {@link EntitySystem} should
 * process. Example: {@code Family.all(PositionComponent.class, VelocityComponent.class).get()} Families can't be instantiated
 * directly but must be accessed via a builder ( start with {@code Family.all()}, {@code Family.one()} or {@code Family.exclude()}
 * ), this is to avoid duplicate families that describe the same components.
 * @author Stefan Bachmann
 */
public class Family {
	private static ObjectMap<String, Family> families = new ObjectMap<String, Family>();
	private static int familyIndex = 0;
	private static final Builder builder = new Builder();
	private static final Bits zeroBits = new Bits();

	private final Bits all;
	private final Bits one;
	private final Bits exclude;
	private final int index;

	/** Private constructor, use static method Family.getFamilyFor() */
	private Family (Bits all, Bits any, Bits exclude) {
		this.all = all;
		this.one = any;
		this.exclude = exclude;
		this.index = familyIndex++;
	}

	/** @return This family's unique index */
	public int getIndex () {
		return this.index;
	}

	/** @return Whether the entity matches the family requirements or not */
	public boolean matches (Entity entity) {
		Bits entityComponentBits = entity.getComponentBits();

		if (entityComponentBits.isEmpty()) return false;

		for (int i = all.nextSetBit(0); i >= 0; i = all.nextSetBit(i + 1)) {
			if (!entityComponentBits.get(i)) return false;
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
	 * @param componentTypes entities will have to contain all of the specified components.
	 * @return A Builder singleton instance to get a family
	 */
	@SafeVarargs
	public static Builder all (Class<? extends Component>... componentTypes) {
		return builder.reset().all(componentTypes);
	}

	/**
	 * @param componentTypes entities will have to contain at least one of the specified components.
	 * @return A Builder singleton instance to get a family
	 */
	@SafeVarargs
	public static Builder one (Class<? extends Component>... componentTypes) {
		return builder.reset().one(componentTypes);
	}

	/**
	 * @param componentTypes entities cannot contain any of the specified components.
	 * @return A Builder singleton instance to get a family
	 */
	@SafeVarargs
	public static Builder exclude (Class<? extends Component>... componentTypes) {
		return builder.reset().exclude(componentTypes);
	}

	public static class Builder {
		private Bits all = zeroBits;
		private Bits one = zeroBits;
		private Bits exclude = zeroBits;

		/**
		 * Resets the builder instance
		 * @return A Builder singleton instance to get a family
		 */
		public Builder reset () {
			all = zeroBits;
			one = zeroBits;
			exclude = zeroBits;
			return this;
		}

		/**
		 * @param componentTypes entities will have to contain all of the specified components.
		 * @return A Builder singleton instance to get a family
		 */
		@SafeVarargs
		public Builder all (Class<? extends Component>... componentTypes) {
			Bits bits = ComponentType.getBitsFor(componentTypes);
			bits.or(all);
			all = bits;
			return this;
		}

		/**
		 * @param componentTypes entities will have to contain at least one of the specified components.
		 * @return A Builder singleton instance to get a family
		 */
		@SafeVarargs
		public Builder one (Class<? extends Component>... componentTypes) {
			Bits bits = ComponentType.getBitsFor(componentTypes);
			bits.or(one);
			one = bits;
			return this;
		}

		/**
		 * @param componentTypes entities cannot contain any of the specified components.
		 * @return A Builder singleton instance to get a family
		 */
		@SafeVarargs
		public Builder exclude (Class<? extends Component>... componentTypes) {
			Bits bits = ComponentType.getBitsFor(componentTypes);
			bits.or(exclude);
			exclude = bits;
			return this;
		}

		/** @return A family for the configured component types */
		public Family get () {
			String hash = getFamilyHash(all, one, exclude);
			Family family = families.get(hash, null);
			if (family == null) {
				family = new Family(all, one, exclude);
				families.put(hash, family);
			}
			return family;
		}
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + all.hashCode();
		result = prime * result + one.hashCode();
		result = prime * result + exclude.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Family)) return false;
		Family other = (Family)obj;
		return index == other.index && all.equals(other.all) && one.equals(other.one) && exclude.equals(other.exclude);
	}

	private static String getFamilyHash (Bits all, Bits one, Bits exclude) {
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

	private static String getBitsString (Bits bits) {
		StringBuilder builder = new StringBuilder();

		int numBits = bits.length();
		for (int i = 0; i < numBits; ++i) {
			builder.append(bits.get(i) ? "1" : "0");
		}

		return builder.toString();
	}
}
