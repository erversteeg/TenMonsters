package me.versteege.games.libgdx.tenmonsters.system;

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
import me.versteege.games.libgdx.tenmonsters.world.Direction;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;

public class MonsterMovementSystemOld extends EntityProcessingSystem {

	@Mapper private ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper private ComponentMapper<MonsterMovementStateComponent> mMovementStateMapper;
	@Mapper private ComponentMapper<MonsterCombatStateComponent> mCombatStateMapper;
	@Mapper private ComponentMapper<TileWalkingStateComponent> mTileWalkingStateMapper;
	@Mapper private ComponentMapper<TileWalkingComponent> mTileWalkingMapper;
	
	private boolean [][] mTileMap;
	private final Vector2 mTempVector;
	
	@SuppressWarnings("unchecked")
	public MonsterMovementSystemOld() {
		super(Aspect.getAspectForAll(PositionComponent.class, MonsterComponent.class, MonsterMovementStateComponent.class, MonsterCombatStateComponent.class, TileWalkingStateComponent.class, TileWalkingComponent.class, ShapeComponent.class));
		
		mTempVector = new Vector2();
	}

	@Override
	protected void process(Entity entity) {
		
		PositionComponent positionComponent = mPositionMapper.get(entity);
		MonsterMovementStateComponent movementStateComponent = mMovementStateMapper.get(entity);
		MonsterCombatStateComponent combatState = mCombatStateMapper.get(entity);
		TileWalkingStateComponent tileWalkingState = mTileWalkingStateMapper.get(entity);
		TileWalkingComponent tileWalking = mTileWalkingMapper.get(entity);
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		PositionComponent playerPosition = player.getComponent(PositionComponent.class);
		
		// if the monster is currently walking between tiles, don't let anything else happen
		if(tileWalkingState.getState() == TileWalkingState.WALKING) {
			move(tileWalking, tileWalkingState, positionComponent, tileWalking.getRequestedPosition(), world.delta);
		}
		else if(movementStateComponent.getState() == MovementState.STILL) {
			if(combatState.getState() == CombatState.ENGAGING) {
				movementStateComponent.setState(MovementState.MOVING);
			}
		}
		else if(movementStateComponent.getState() == MovementState.MOVING) {
			stepToPlayer(tileWalking, tileWalkingState, positionComponent, mTempVector.set(playerPosition.getX(), playerPosition.getY()).sub(positionComponent.getX(), positionComponent.getY()));
			if(playerPosition.get().dst(positionComponent.get()) == 1.0f) {
				combatState.setState(CombatState.WAITING);
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
	
	public void setTileMap(boolean [][] tileMap) {
		mTileMap = tileMap;
	}
	
	private void stepToPlayer(TileWalkingComponent walking, TileWalkingStateComponent walkingState, PositionComponent monsterPosition, Vector2 toPlayer) {
		
		System.out.println(new StringBuilder().append("Monster position: ").append(monsterPosition.getX()).append(", ").append(monsterPosition.getY()));
		
		// left, right, up, down
		int [] directionPriorities = getDirectionPriorities(toPlayer);
		
		boolean moved = false;
		while(!moved) {
			switch(getMaxIndex(directionPriorities)) {
				case 0:
					if(moveDirection(monsterPosition, Direction.LEFT)) {
						System.out.println("Move left");
						moved = true;
						walking.setDirection(Direction.LEFT);
						walking.getPath().set(-1, 0);
						walking.getRequestedPosition().set(monsterPosition.getX(), monsterPosition.getY()).add(walking.getPath());
						walkingState.setState(TileWalkingState.WALKING);
					}
					directionPriorities[0] = -1;
					break;
				case 1:
					if(moveDirection(monsterPosition, Direction.RIGHT)) {
						System.out.println("Move right");
						moved = true;
						walking.setDirection(Direction.RIGHT);
						walking.getPath().set(1, 0);
						walking.getRequestedPosition().set(monsterPosition.getX(), monsterPosition.getY()).add(walking.getPath());
						walkingState.setState(TileWalkingState.WALKING);
					}
					directionPriorities[1] = -1;
					break;
				case 2:
					if(moveDirection(monsterPosition, Direction.UP)) {
						System.out.println("Move up");
						moved = true;
						walking.setDirection(Direction.UP);
						walking.getPath().set(0, 1);
						walking.getRequestedPosition().set(monsterPosition.getX(), monsterPosition.getY()).add(walking.getPath());
						walkingState.setState(TileWalkingState.WALKING);
					}
					directionPriorities[2] = -1;
					break;
				case 3:
					if(moveDirection(monsterPosition, Direction.DOWN)) {
						System.out.println("Move down");
						moved = true;
						walking.setDirection(Direction.DOWN);
						walking.getPath().set(0, -1);
						walking.getRequestedPosition().set(monsterPosition.getX(), monsterPosition.getY()).add(walking.getPath());
						walkingState.setState(TileWalkingState.WALKING);
					}
					directionPriorities[3] = -1;
					break;
			}
		}
	}
	
	private boolean moveDirection(PositionComponent monsterPosition, Direction direction) {
		switch(direction) {
			case LEFT:
				if(mTileMap[(int)monsterPosition.getX() - 1][(int)monsterPosition.getY()]) {
					monsterPosition.setX(monsterPosition.getX() - 1);
					return true;
				}
				else {
					return false;
				}
			case RIGHT:
				if(mTileMap[(int)monsterPosition.getX() + 1][(int)monsterPosition.getY()]) {
					monsterPosition.setX(monsterPosition.getX() + 1);
					return true;
				}
				else {
					return false;
				}
			case UP:
				if(mTileMap[(int)monsterPosition.getX()][(int)monsterPosition.getY() + 1]) {
					monsterPosition.setY(monsterPosition.getY() + 1);
					return true;
				}
				else {
					return false;
				}
			case DOWN:
				if(mTileMap[(int)monsterPosition.getX()][(int)monsterPosition.getY() - 1]) {
					monsterPosition.setY(monsterPosition.getY() - 1);
					return true;
				}
				else {
					return false;
				}
		}
		return false;
	}
	
	private int getMaxIndex(int [] array) {
		int maxIndex = -1;
		int maxValue = Integer.MIN_VALUE;
		for(int i = 0; i < array.length; i++) {
			if(array[i] > maxValue) {
				maxValue = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	private int [] getDirectionPriorities(Vector2 toPlayer) {
		
		float verticalDist = toPlayer.y;
		float horizontalDist = toPlayer.x;
		
		int left = 0;
		int right = 0;
		int up = 0;
		int down = 0;
		
		if(verticalDist > 0) up++;
		else down++;
		
		if(horizontalDist > 0) right++;
		else left++;
		
		if(verticalDist > horizontalDist) {
			up++;
			down++;
		}
		else {
			left++;
			right++;
		}
		
		return new int [] {left, right, up, down};
	}
}
