package ashley.tests;

import ashley.core.Engine;
import ashley.core.Entity;
import ashley.tests.components.MovementComponent;
import ashley.tests.components.PositionComponent;
import ashley.tests.components.VisualComponent;
import ashley.tests.systems.MovementSystem;
import ashley.tests.systems.RenderSystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class RenderSystemTest {
	public static void main(String[] args){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		config.useGL20 = true;
		
		new LwjglApplication(new MainClass(), config);
	}
	
	public static class MainClass extends ApplicationAdapter {
		Engine engine;
		
		@Override
		public void create() {
			OrthographicCamera camera = new OrthographicCamera(640, 480);
			camera.position.set(320, 240, 0);
			camera.update();
			
			Texture crateTexture = new Texture("assets/crate.png");
			Texture coinTexture = new Texture("assets/coin.png");
			
			engine = new Engine();
			engine.addSystem(new RenderSystem(camera));
			engine.addSystem(new MovementSystem());
			
			Entity crate = engine.createEntity();
			crate.add(new PositionComponent(50, 50));
			crate.add(new VisualComponent(new TextureRegion(crateTexture)));

			engine.addEntity(crate);
			
			TextureRegion coinRegion = new TextureRegion(coinTexture);
			
			for(int i=0; i<100; i++){
				Entity coin = engine.createEntity();
				coin.add(new PositionComponent(MathUtils.random(640), MathUtils.random(480)));
				coin.add(new MovementComponent(10.0f, 10.0f));
				coin.add(new VisualComponent(coinRegion));
				engine.addEntity(coin);
			}
		}

		@Override
		public void render() {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			engine.update(Gdx.graphics.getDeltaTime());
		}
	}
}
