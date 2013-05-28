package me.versteege.games.libgdx.tenmonsters.system.rendering;

import me.versteege.games.libgdx.tenmonsters.component.HUDComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class HUDRenderingSystem extends EntityProcessingSystem {

	private ShapeRenderer mShapeRenderer;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<ShapeComponent> mShapeMapper;
	
	@SuppressWarnings("unchecked")
	public HUDRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, ShapeComponent.class, HUDComponent.class));
		
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
		
		mShapeRenderer.begin(ShapeType.Filled);
	}

	@Override
	protected void end() {	
		mShapeRenderer.end();
		
		super.end();
	}
}
