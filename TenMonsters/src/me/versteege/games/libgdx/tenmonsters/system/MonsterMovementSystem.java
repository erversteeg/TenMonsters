package me.versteege.games.libgdx.tenmonsters.system;

import java.util.LinkedList;
import java.util.List;

import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent.CombatState;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent.MovementState;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent.TileWalkingState;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.utils.WorldUtils;
import me.versteege.games.libgdx.tenmonsters.world.Direction;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;

public class MonsterMovementSystem extends EntityProcessingSystem {

	@Mapper private ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper private ComponentMapper<MonsterMovementStateComponent> mMovementStateMapper;
	@Mapper private ComponentMapper<MonsterCombatStateComponent> mCombatStateMapper;
	@Mapper private ComponentMapper<TileWalkingStateComponent> mTileWalkingStateMapper;
	@Mapper private ComponentMapper<TileWalkingComponent> mTileWalkingMapper;
	@Mapper private ComponentMapper<ShapeComponent> mShapeMapper;
	
	private boolean [][] mTileMap;
	private final Vector2 mTempVector;
	private final List<Vector2> mVectorList;				// used in closest tile checking, just don't want any alloc'd memory during execution
	
	@SuppressWarnings("unchecked")
	public MonsterMovementSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, MonsterComponent.class, MonsterMovementStateComponent.class, MonsterCombatStateComponent.class, TileWalkingStateComponent.class, TileWalkingComponent.class, ShapeComponent.class));
		
		mTempVector = new Vector2();
		mVectorList = new LinkedList<Vector2>();
	}

	@Override
	protected void process(Entity entity) {
		
		PositionComponent positionComponent = mPositionMapper.get(entity);
		MonsterMovementStateComponent movementStateComponent = mMovementStateMapper.get(entity);
		MonsterCombatStateComponent combatState = mCombatStateMapper.get(entity);
		TileWalkingStateComponent tileWalkingState = mTileWalkingStateMapper.get(entity);
		TileWalkingComponent tileWalking = mTileWalkingMapper.get(entity);
		ShapeComponent shapeComponent = mShapeMapper.get(entity);
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		PositionComponent playerPosition = player.getComponent(PositionComponent.class);
		
		if(tileWalkingState.getState() == TileWalkingState.WALKING) {
			move(tileWalking, tileWalkingState, positionComponent, mTempVector.set(tileWalking.getRequestedPosition()), world.delta);
		}
		else if(tileWalkingState.getState() == TileWalkingState.IDLE && combatState.getState() == CombatState.ENGAGING || combatState.getState() == CombatState.WAITING || combatState.getState() == CombatState.ATTACKING) {
			if(positionComponent.get().dst(playerPosition.get()) > 1) {
				step(tileWalking, tileWalkingState, playerPosition.get(), positionComponent);
			}
			else {
				movementStateComponent.setState(MovementState.STILL);
			}
		}
	}
	
	private void move(TileWalkingComponent walking, TileWalkingStateComponent walkingState, PositionComponent position, Vector2 requestedPosition, float deltaTime) {
		position.add(deltaTime / walking.getSpeed() * walking.getPath().x, deltaTime / walking.getSpeed() * walking.getPath().y);
		
		// left
		if(walking.getDirection() == Direction.LEFT && position.getX() <= requestedPosition.x) {
			position.setPosition(requestedPosition.x, requestedPosition.y);
			walkingState.setState(TileWalkingState.IDLE);
		}
		// right
		else if(walking.getDirection() == Direction.RIGHT && position.getX() >= requestedPosition.x) {
			position.setPosition(requestedPosition.x, requestedPosition.y);
			walkingState.setState(TileWalkingState.IDLE);
		}
		// up
		else if(walking.getDirection() == Direction.UP && position.getY() >= requestedPosition.y) {
			position.setPosition(requestedPosition.x, requestedPosition.y);
			walkingState.setState(TileWalkingState.IDLE);
		}
		// down
		else if(walking.getDirection() == Direction.DOWN && position.getY() <= requestedPosition.y) {
			position.setPosition(requestedPosition.x, requestedPosition.y);
			walkingState.setState(TileWalkingState.IDLE);
		}
	}
	
	private void step(TileWalkingComponent walking, TileWalkingStateComponent walkingState, Vector2 toward, PositionComponent position) {
		
		mVectorList.clear();
		
		boolean validTile = false;
		while(!validTile) {
			Vector2 closestDir = WorldUtils.closestTileToPosition(toward, position.get(), mVectorList, mTempVector);
			Vector2 closestTilePos = closestDir.add(position.get());
			
			if(WorldUtils.isValidTile(closestTilePos, mTileMap)) {
				walking.getRequestedPosition().set(closestTilePos);
				
				Vector2 unitVec = closestTilePos.sub(position.get());
				
				walking.setDirection(WorldUtils.directionForUnitVector(unitVec));
				walking.getPath().set(unitVec);
				
				walkingState.setState(TileWalkingState.WALKING);
				
				validTile = true;
			}
			else {
				Vector2 unitVec = closestTilePos.sub(position.get());
				
				mVectorList.add(unitVec);
			}
		}	
	}
	
	public void setTileMap(boolean [][] tileMap) {
		mTileMap = tileMap;
	}
}
