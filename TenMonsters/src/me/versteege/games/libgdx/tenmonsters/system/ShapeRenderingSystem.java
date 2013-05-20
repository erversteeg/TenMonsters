package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.SpriteComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ShapeRenderingSystem extends EntityProcessingSystem {

	private ShapeRenderer mShapeRenderer;
	private OrthographicCamera mCamera;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<ShapeComponent> mShapeMapper;
	
	@SuppressWarnings("unchecked")
	public ShapeRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, ShapeComponent.class));
		
		mShapeRenderer = new ShapeRenderer();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		mCamera = new OrthographicCamera(w, h);
	}

	@Override
	protected void process(Entity entity) {	
		PositionComponent positionComponent = mPositionMapper.get(entity);
		ShapeComponent shapeComponent = mShapeMapper.get(entity);
		
		mShapeRenderer.setColor(shapeComponent.getColor());
		mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY(), shapeComponent.getWidth(), shapeComponent.getHeight());
	}

	@Override
	protected void begin() {
		super.begin();
		
		if(Gdx.input.isKeyPressed(Keys.MINUS)) {
			mCamera.zoom += 0.1f;
		}
		else if(Gdx.input.isKeyPressed(Keys.EQUALS)) {
			mCamera.zoom -= 0.1f;
		}
		
		mCamera.update();
		
		mShapeRenderer.setProjectionMatrix(mCamera.combined);
		mShapeRenderer.begin(ShapeType.Filled);
	}

	@Override
	protected void end() {
		mShapeRenderer.end();
		
		super.end();
	}
}
