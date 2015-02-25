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

package com.badlogic.ashley.benchmark.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.ashley.benchmark.artemis.components.PositionComponent;
import com.badlogic.ashley.benchmark.artemis.components.RadiusComponent;

public class CollisionSystem extends EntitySystem {

	public CollisionSystem () {
		super(Aspect.getAspectForAll(PositionComponent.class, RadiusComponent.class));
	}

	@Override
	protected boolean checkProcessing () {
		return true;
	}

	@Override
	protected void processEntities (ImmutableBag<Entity> entities) {

	}
}
