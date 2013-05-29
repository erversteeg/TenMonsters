package me.versteege.games.libgdx.tenmonsters.system.rendering;

import me.versteege.games.libgdx.tenmonsters.component.FontComponent;
import me.versteege.games.libgdx.tenmonsters.component.HUDComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.TextComponent;
import me.versteege.games.libgdx.tenmonsters.component.TimerComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class HUDRenderingSystem extends EntityProcessingSystem {

	// for game title //////////////////
	private long mSystemInitTimeMillis;
	private Entity mGameTitle;
	private boolean mCreatedTitle = false;
	////////////////////////////////////
	
	private ShapeRenderer mShapeRenderer;
	private SpriteBatch mSpriteBatch;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<ShapeComponent> mShapeMapper;
	@Mapper ComponentMapper<TimerComponent> mTimerMapper;
	@Mapper ComponentMapper<FontComponent> mFontMapper;
	
	@SuppressWarnings("unchecked")
	public HUDRenderingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, HUDComponent.class));
		
		mShapeRenderer = new ShapeRenderer();
		mSpriteBatch = new SpriteBatch();
		
		mSystemInitTimeMillis = System.currentTimeMillis();
	}

	@Override
	protected void process(Entity entity) {	
		
		PositionComponent positionComponent = mPositionMapper.get(entity);
		ShapeComponent shapeComponent = mShapeMapper.get(entity);
		TimerComponent timerComponent = mTimerMapper.get(entity);
		FontComponent fontComponent = mFontMapper.get(entity);
		TextComponent textComponent = entity.getComponent(TextComponent.class);
		
		if(shapeComponent != null) {
			mShapeRenderer.setColor(shapeComponent.getColor());
			mShapeRenderer.rect(positionComponent.getX(), positionComponent.getY(), shapeComponent.getWidth(), shapeComponent.getHeight());
		}
		
		if(timerComponent != null && fontComponent != null) {
			fontComponent.getFont().draw(mSpriteBatch, String.format("%.1f", timerComponent.getElapsedTime()), positionComponent.getX(), positionComponent.getY());
		}
		
		if(textComponent != null && fontComponent != null) {
			fontComponent.getFont().draw(mSpriteBatch, textComponent.getText(), positionComponent.getX(), positionComponent.getY());
		}
	}

	@Override
	protected void begin() {
		super.begin();
		
		mShapeRenderer.begin(ShapeType.Filled);
		mSpriteBatch.begin();
		
		if(!mCreatedTitle) {
			mGameTitle = world.createEntity();
			mGameTitle.addComponent(new HUDComponent());
			mGameTitle.addComponent(new FontComponent(2));
			mGameTitle.addComponent(new PositionComponent(20, 50));
			mGameTitle.addComponent(new TextComponent("Ten Monsters"));
			mGameTitle.addToWorld();
			
			mCreatedTitle = true;
		}
		
		if(mGameTitle != null) {
			if(System.currentTimeMillis() - mSystemInitTimeMillis > 10000) {
				mGameTitle.deleteFromWorld();
			}
		}
	}

	@Override
	protected void end() {	
		mSpriteBatch.end();
		mShapeRenderer.end();
		
		super.end();
	}
}
