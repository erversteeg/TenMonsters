package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.PlayerComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.WaitCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ShapeRenderingSystem extends EntityProcessingSystem {

	private ShapeRenderer mShapeRenderer;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<ShapeComponent> mShapeMapper;
	
	@SuppressWarnings("unchecked")
	public ShapeRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, ShapeComponent.class).exclude(PlayerComponent.class));
		
		mShapeRenderer = new ShapeRenderer();
	}

	@Override
	protected void process(Entity entity) {	
		PositionComponent positionComponent = mPositionMapper.get(entity);
		ShapeComponent shapeComponent = mShapeMapper.get(entity);
		
		mShapeRenderer.setColor(shapeComponent.getColor());
		mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY(), shapeComponent.getWidth(), shapeComponent.getHeight());
		
		HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
		if(healthComponent != null) {
			// background
			mShapeRenderer.setColor(Color.BLACK);
			mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY() + shapeComponent.getHeight() + 0.1f, shapeComponent.getWidth(), 0.15f);
			
			// foreground
			mShapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY() + shapeComponent.getHeight() + 0.1f, shapeComponent.getWidth() * healthComponent.getPercent(), 0.15f);
		}
	}

	@Override
	protected void begin() {
		super.begin();
		
		CameraTrackingSystem cameraTrackingSystem = world.getSystem(CameraTrackingSystem.class);
		OrthographicCamera camera = cameraTrackingSystem.getCamera();
		
		
		camera.update();
		
		mShapeRenderer.setProjectionMatrix(camera.combined);
		mShapeRenderer.begin(ShapeType.Filled);
	}

	@Override
	protected void end() {
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		PositionComponent playerPosition = player.getComponent(PositionComponent.class);
		ShapeComponent playerShape = player.getComponent(ShapeComponent.class);
		
		mShapeRenderer.setColor(playerShape.getColor());
		mShapeRenderer.rect(playerPosition.getX(), playerPosition.getY(), playerShape.getWidth(), playerShape.getHeight());
		
		HealthComponent playerHealth = player.getComponent(HealthComponent.class);
		if(playerHealth != null) {
			// background
			mShapeRenderer.setColor(Color.BLACK);
			mShapeRenderer.rect(playerPosition.getX(), playerPosition.getY() + playerShape.getHeight() + 0.1f, playerShape.getWidth(), 0.15f);
			
			// foreground
			mShapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			mShapeRenderer.rect(playerPosition.getX(), playerPosition.getY() + playerShape.getHeight() + 0.1f, playerShape.getWidth() * playerHealth.getPercent(), 0.15f);
		}
		
		mShapeRenderer.end();
		
		super.end();
	}
}
