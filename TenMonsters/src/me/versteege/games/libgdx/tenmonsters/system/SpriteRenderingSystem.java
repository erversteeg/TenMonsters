package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.SpriteComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpriteRenderingSystem extends EntityProcessingSystem {

	private SpriteBatch mSpriteBatch;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<SpriteComponent> mSpriteMapper;
	
	@SuppressWarnings("unchecked")
	public SpriteRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, SpriteComponent.class));
		
		mSpriteBatch = new SpriteBatch();
	}

	@Override
	protected void process(Entity entity) {	
		PositionComponent positionComponent = mPositionMapper.get(entity);
		SpriteComponent spriteComponent = mSpriteMapper.get(entity);
			
		mSpriteBatch.draw(spriteComponent.getSprite(), positionComponent.getX(), positionComponent.getY(), spriteComponent.getWidth(), spriteComponent.getHeight());
	}

	@Override
	protected void begin() {
		super.begin();
		
		mSpriteBatch.begin();
	}

	@Override
	protected void end() {
		mSpriteBatch.end();
		
		super.end();
	}
}
