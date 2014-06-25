package com.badlogic.ashley.tests.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualComponent extends Component {
	public TextureRegion region;
	
	public VisualComponent(TextureRegion region){
		this.region = region;
	}
}
