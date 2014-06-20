package ashley.tests.systems;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.core.EntitySystem;
import ashley.core.Family;
import ashley.tests.components.PositionComponent;
import ashley.tests.components.VisualComponent;
import ashley.utils.ImmutableIntMap;
import ashley.utils.ImmutableIntMap.Keys;

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
		
		Keys keys = entities.immutableKeys();

		while(keys.hasNext()){
			Entity e = entities.get(keys.next());
			position = e.getComponent(PositionComponent.class);
			visual = e.getComponent(VisualComponent.class);
			
			batch.draw(visual.region, position.x, position.y);
		}
		
		batch.end();
	}
}
