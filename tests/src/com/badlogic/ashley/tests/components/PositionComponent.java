package com.badlogic.ashley.tests.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
	public float x, y;
	
	public PositionComponent(float x, float y){
		this.x = x;
		this.y = y;
	}
}
