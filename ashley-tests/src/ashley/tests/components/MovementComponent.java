package ashley.tests.components;

import ashley.core.Component;

public class MovementComponent extends Component {
	public float velocityX;
	public float velocityY;
	
	public MovementComponent(float velocityX, float velocityY){
		this.velocityX = velocityX;
		this.velocityY = velocityY;
	}
}
