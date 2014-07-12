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

package com.badlogic.ashley.tests.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.tests.components.PositionComponent;
import com.badlogic.ashley.tests.components.VisualComponent;
import com.badlogic.ashley.utils.ImmutableIntMap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends EntitySystem {
	private ImmutableIntMap<Entity> entities;
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	public RenderSystem(OrthographicCamera camera){
		batch = new SpriteBatch();
		
		this.camera = camera;
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.getFamilyFor(PositionComponent.class, VisualComponent.class));
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}

	@Override
	public void update(float deltaTime) {
		PositionComponent position;
		VisualComponent visual;
		
		camera.update();
		
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		
		for (Entity e : entities.values()) {
			position = e.getComponent(PositionComponent.class);
			visual = e.getComponent(VisualComponent.class);
			
			batch.draw(visual.region, position.x, position.y);
		}
		
		batch.end();
	}
}
