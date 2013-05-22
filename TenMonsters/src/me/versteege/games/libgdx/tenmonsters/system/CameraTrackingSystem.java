package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.CameraFollowComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraTrackingSystem extends EntityProcessingSystem {

	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	
	private OrthographicCamera mCamera;
	
	@SuppressWarnings("unchecked")
	public CameraTrackingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, CameraFollowComponent.class));
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		mCamera = new OrthographicCamera(w / TenMonstersWorld.WORLD_TO_PIXEL, h / TenMonstersWorld.WORLD_TO_PIXEL);
	}

	@Override
	protected void process(Entity entity) {
		PositionComponent positionComponent = mPositionMapper.get(entity);
		mCamera.position.set(positionComponent.getX(), positionComponent.getY(), 0);
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
	}

	public OrthographicCamera getCamera() {
		return mCamera;
	}
}
