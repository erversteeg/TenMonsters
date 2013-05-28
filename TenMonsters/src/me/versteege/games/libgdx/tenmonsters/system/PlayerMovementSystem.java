package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.PlayerComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionHistoryComponent;
import me.versteege.games.libgdx.tenmonsters.world.Direction;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class PlayerMovementSystem extends EntityProcessingSystem {
	
	private enum Movement {
		MOVING,
		IDLE
	}
	
	private static final float COOLDOWN_TIME = 0.2f;
	
	private float mElapsedWait = Float.MAX_VALUE;
	
	private boolean [][] mTileMap;
	private final Vector2 mRequestedPosition;
	private final Vector2 mMovementPath;
	private Direction mMovementDirection;
	
	private Movement mState;
	
	@Mapper ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper ComponentMapper<PositionHistoryComponent> mPositionHistoryMapper;
	
	@SuppressWarnings("unchecked")
	public PlayerMovementSystem() {
		super(Aspect.getAspectForAll(PlayerComponent.class, PositionComponent.class, PositionHistoryComponent.class));

		mRequestedPosition = new Vector2(0, 0);
		mMovementPath = new Vector2();
		
		mState = Movement.IDLE;
	}

	@Override
	protected void process(Entity entity) {
		
		PositionComponent positionComponent = mPositionMapper.get(entity);
		PositionHistoryComponent positionHistory = mPositionHistoryMapper.get(entity);
		
		if(mState == Movement.IDLE) {
			
			mRequestedPosition.set(positionComponent.getX(), positionComponent.getY());
			
			if(mElapsedWait >= COOLDOWN_TIME) {
				
				if(Gdx.input.isKeyPressed(Keys.W)) {
					mRequestedPosition.y ++;
					mElapsedWait = 0.0f;
					mMovementPath.set(mRequestedPosition).sub(positionComponent.getX(), positionComponent.getY()).nor();
				}
				else if(Gdx.input.isKeyPressed(Keys.S)) {
					mRequestedPosition.y --;
					mElapsedWait = 0.0f;
					mMovementPath.set(mRequestedPosition).sub(positionComponent.getX(), positionComponent.getY()).nor();
				}
				else if(Gdx.input.isKeyPressed(Keys.A)) {
					mRequestedPosition.x --;
					mElapsedWait = 0.0f;
					mMovementPath.set(mRequestedPosition).sub(positionComponent.getX(), positionComponent.getY()).nor();
				}
				else if(Gdx.input.isKeyPressed(Keys.D)) {
					mRequestedPosition.x ++;
					mElapsedWait = 0.0f;
					mMovementPath.set(mRequestedPosition).sub(positionComponent.getX(), positionComponent.getY()).nor();
				}
			}
			
			if(mTileMap == null) return;
			
			if(mTileMap[(int) mRequestedPosition.x][(int) mRequestedPosition.y] && (mRequestedPosition.x != positionComponent.getX() || mRequestedPosition.y != positionComponent.getY())) {
				mState = Movement.MOVING;
				
				// set direction
				// left
				if(mMovementPath.x < 0 && mMovementPath.y == 0) {
					mMovementDirection = Direction.LEFT;
				}
				// right
				else if(mMovementPath.x > 0 && mMovementPath.y == 0) {
					mMovementDirection = Direction.RIGHT;
				}
				// up
				else if(mMovementPath.x == 0 && mMovementPath.y > 0) {
					mMovementDirection = Direction.UP;
				}
				// down
				else if(mMovementPath.x == 0 && mMovementPath.y < 0) {
					mMovementDirection = Direction.DOWN;
				}
			}
		}
		else if(mState == Movement.MOVING) {
			move(positionComponent, mRequestedPosition, world.delta, positionHistory);
		}
	}
	
	@Override
	protected void begin() {
		super.begin();
		
		mElapsedWait += world.delta;
	}

	private void move(PositionComponent playerPosition, Vector2 requestedPosition, float deltaTime, PositionHistoryComponent positionHistory) {
		playerPosition.add(deltaTime / COOLDOWN_TIME * mMovementPath.x, deltaTime / COOLDOWN_TIME * mMovementPath.y);
		
		// left
		if(mMovementDirection == Direction.LEFT && playerPosition.getX() <= requestedPosition.x) {
			playerPosition.setPosition(requestedPosition.x, requestedPosition.y);
			mState = Movement.IDLE;
			positionHistory.push(new Vector2(requestedPosition.x, requestedPosition.y));
		}
		// right
		else if(mMovementDirection == Direction.RIGHT && playerPosition.getX() >= requestedPosition.x) {
			playerPosition.setPosition(requestedPosition.x, requestedPosition.y);
			mState = Movement.IDLE;
			positionHistory.push(new Vector2(requestedPosition.x, requestedPosition.y));
		}
		// up
		else if(mMovementDirection == Direction.UP && playerPosition.getY() >= requestedPosition.y) {
			playerPosition.setPosition(requestedPosition.x, requestedPosition.y);
			mState = Movement.IDLE;
			positionHistory.push(new Vector2(requestedPosition.x, requestedPosition.y));
		}
		// down
		else if(mMovementDirection == Direction.DOWN && playerPosition.getY() <= requestedPosition.y) {
			playerPosition.setPosition(requestedPosition.x, requestedPosition.y);
			mState = Movement.IDLE;
			positionHistory.push(new Vector2(requestedPosition.x, requestedPosition.y));
		}
	}
	
	public void setTileMap(boolean [][] tileMap) {
		mTileMap = tileMap;
	}
}
