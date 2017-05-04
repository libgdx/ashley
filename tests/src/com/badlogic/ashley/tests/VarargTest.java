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

package com.badlogic.ashley.tests;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.tests.components.CameraComponent;
import com.badlogic.ashley.tests.components.MovementComponent;
import com.badlogic.ashley.tests.components.PositionComponent;

public class VarargTest {
	
	public static void main(String[] args){
		Entity entity = new Entity();
		PositionComponent position = new PositionComponent(1, 2);
		MovementComponent movement = new MovementComponent(3, 4);
		CameraComponent camera = new CameraComponent();
		
		entity.add(position, movement, camera);
		
		for (Component component : entity.getComponents()){
			log("This entity has a: " + component.getClass().getName() + " component.");
		}
	}
	
	public static void log(String message){
		System.out.println(message);
	}

}
