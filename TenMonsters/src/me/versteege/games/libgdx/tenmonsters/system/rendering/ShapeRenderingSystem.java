package me.versteege.games.libgdx.tenmonsters.system.rendering;

import java.util.Arrays;
import java.util.List;

import me.versteege.games.libgdx.tenmonsters.component.HUDComponent;
import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.PlayerComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.system.CameraTrackingSystem;
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
	private List<ZIndexedEntity> mZIndexedEntities;
	private boolean [][] mTileMap;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<ShapeComponent> mShapeMapper;
	
	@SuppressWarnings("unchecked")
	public ShapeRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, ShapeComponent.class).exclude(PlayerComponent.class, MonsterComponent.class, HUDComponent.class));
		
		mShapeRenderer = new ShapeRenderer();
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
		
		CameraTrackingSystem cameraTrackingSystem = world.getSystem(CameraTrackingSystem.class);
		OrthographicCamera camera = cameraTrackingSystem.getCamera();
		
		camera.update();
		
		mShapeRenderer.setProjectionMatrix(camera.combined);
		mShapeRenderer.begin(ShapeType.Filled);
	}

	@Override
	protected void end() {
		
		if(mZIndexedEntities != null) {
			// update z-indexes
			for(ZIndexedEntity entity : mZIndexedEntities) {
				entity.setZIndex((int)-entity.getEntity().getComponent(PositionComponent.class).getY());
			}
			
			int [][] entityPositionFrequency = new int [mTileMap.length][mTileMap[0].length];
			
			ZIndexedEntity [] zIndexedEntityArray = mZIndexedEntities.toArray(new ZIndexedEntity [mZIndexedEntities.size()]);
			Arrays.sort(zIndexedEntityArray);
			
			for(int i = 0; i < zIndexedEntityArray.length; i++) {
				
				PositionComponent positionComponent = zIndexedEntityArray[i].getEntity().getComponent(PositionComponent.class);
				ShapeComponent shapeComponent = zIndexedEntityArray[i].getEntity().getComponent(ShapeComponent.class);
				
				float yOffset = entityPositionFrequency[(int) positionComponent.getX()][(int) positionComponent.getY()] * 0.2f;
				
				mShapeRenderer.setColor(shapeComponent.getColor());
				mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY(), shapeComponent.getWidth(), shapeComponent.getHeight());
				
				// health bars
				HealthComponent healthComponent = zIndexedEntityArray[i].getEntity().getComponent(HealthComponent.class);
				if(healthComponent != null) {
					// background
					mShapeRenderer.setColor(Color.BLACK);
					mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY() + shapeComponent.getHeight() + 0.1f + yOffset, shapeComponent.getWidth(), 0.15f);
					
					// foreground
					mShapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
					mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY() + shapeComponent.getHeight() + 0.1f + yOffset, shapeComponent.getWidth() * healthComponent.getPercent(), 0.15f);
				}
				
				entityPositionFrequency[(int) positionComponent.getX()][(int) positionComponent.getY()]++;
			}
		}
		
		mShapeRenderer.end();
		super.end();
	}
	
	public void setTileMap(boolean [][] tileMap) {
		mTileMap = tileMap;
	}
	
	public void setZIndexedEntities(List<ZIndexedEntity> zIndexedEntities) {
		mZIndexedEntities = zIndexedEntities;
	}
	
	public void removeZIndexedEntity(Entity entity) {
		mZIndexedEntities.remove(new ZIndexedEntity(entity));
	}
}
