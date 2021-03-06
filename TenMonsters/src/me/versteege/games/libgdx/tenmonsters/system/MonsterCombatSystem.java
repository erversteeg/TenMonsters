package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.AttackCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionHistoryComponent;
import me.versteege.games.libgdx.tenmonsters.component.SpriteComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.WaitCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent.CombatState;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent.MovementState;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent.TileWalkingState;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.global.SharedGame;
import me.versteege.games.libgdx.tenmonsters.utils.WorldUtils;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;

public class MonsterCombatSystem extends EntityProcessingSystem {

	@Mapper private ComponentMapper<MonsterMovementStateComponent> mMovementStateMapper;
	@Mapper private ComponentMapper<TileWalkingStateComponent> mTileWalkingStateMapper;
	@Mapper private ComponentMapper<TileWalkingComponent> mTileWalkingMapper;
	@Mapper private ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper private ComponentMapper<SpriteComponent> mSpriteMapper;
	@Mapper private ComponentMapper<MonsterCombatStateComponent> mCombatStateMapper;
	@Mapper private ComponentMapper<WaitCooldownComponent> mWaitCooldownMapper;
	@Mapper private ComponentMapper<AttackCooldownComponent> mAttackCooldownMapper;
	
	private boolean [][] mTileMap;
	
	@SuppressWarnings("unchecked")
	public MonsterCombatSystem() {
		super(Aspect.getAspectForAll(MonsterMovementStateComponent.class, TileWalkingStateComponent.class, TileWalkingComponent.class, PositionComponent.class, SpriteComponent.class, MonsterCombatStateComponent.class, WaitCooldownComponent.class, AttackCooldownComponent.class));
	}

	@Override
	protected void process(Entity entity) {
		MonsterMovementStateComponent movementState = mMovementStateMapper.get(entity);
		TileWalkingStateComponent tileWalkingState = mTileWalkingStateMapper.get(entity);
		TileWalkingComponent tileWalking = mTileWalkingMapper.get(entity);
		PositionComponent position = mPositionMapper.get(entity);
		SpriteComponent sprite = mSpriteMapper.get(entity);
		MonsterCombatStateComponent combatState = mCombatStateMapper.get(entity);
		WaitCooldownComponent waitCooldown = mWaitCooldownMapper.get(entity);
		AttackCooldownComponent attackCooldown = mAttackCooldownMapper.get(entity);
		
		// update cooldown
		waitCooldown.update(world.delta);
		attackCooldown.update(world.delta);
		
		if(combatState.getState() == CombatState.DORMANT) {
			if(movementState.getState() == MovementState.STILL) {
				
				Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
				PositionComponent playerPosition = player.getComponent(PositionComponent.class);
				
				// check for adjacent position
				//if(position.get().dst(playerPosition.get()) == 1.0f) {
				//if(canSeePlayer(position, playerPosition)) {
				if(playerWithinRange(position, playerPosition)) {
					combatState.setState(CombatState.ENGAGING);
					//shape.setColor(1.0f, 1.0f, 0.0f, 1.0f);
				}
			}
		}
		else if(combatState.getState() == CombatState.ENGAGING) {
			if(movementState.getState() == MovementState.STILL) {
				combatState.setState(CombatState.WAITING);
				waitCooldown.reset();
				//shape.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			}
		}
		else if(combatState.getState() == CombatState.WAITING) {
			
			Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
			PositionComponent playerPosition = player.getComponent(PositionComponent.class);
			PositionHistoryComponent playerPositionHistory = player.getComponent(PositionHistoryComponent.class);
			
			if(waitCooldown.shouldStartAttack()) {
				combatState.setState(CombatState.ATTACKING);
				attackCooldown.reset();
				//shape.setColor(0.0f, 1.0f, 1.0f, 1.0f);
			}
			
			// player is on top of monster
			if(playerPosition.get().equals(position.get())) {
				
				Vector2 lastPlayerPos = playerPositionHistory.peek(1);
				
				tileWalkingState.setState(TileWalkingState.WALKING);
				tileWalking.getPath().set(lastPlayerPos).sub(position.get());
				tileWalking.setDirection(WorldUtils.directionForUnitVector(tileWalking.getPath()));
				tileWalking.getRequestedPosition().set(lastPlayerPos);
			}
		}
		
		else if(combatState.getState() == CombatState.ATTACKING) {
			if(attackCooldown.shouldExecuteAttack()) {
				
				Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
				PositionComponent playerPosition = player.getComponent(PositionComponent.class);
				HealthComponent playerHealth = player.getComponent(HealthComponent.class);
				PositionHistoryComponent playerPositionHistory = player.getComponent(PositionHistoryComponent.class);
				
				if(position.get().dst(playerPosition.get()) == 1.0f) {
					playerHealth.damage(3);
					((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(1).getComponent(ShapeComponent.class).setWidth(playerHealth.getPercent() * 300.0f);
					
					if(playerHealth.isDead()) {
						SharedGame.gameScreen.newGame();
					}
				}
				
				// player is on top of monster
				if(playerPosition.get().equals(position.get())) {
					
					Vector2 lastPlayerPos = playerPositionHistory.peek(1);
					
					tileWalkingState.setState(TileWalkingState.WALKING);
					tileWalking.getPath().set(lastPlayerPos).sub(position.get());
					tileWalking.setDirection(WorldUtils.directionForUnitVector(tileWalking.getPath()));
					tileWalking.getRequestedPosition().set(lastPlayerPos);
				}
				
				combatState.setState(CombatState.WAITING);
				waitCooldown.reset();
				//shape.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			}
		}
	}
	
	private boolean playerWithinRange(PositionComponent monster, PositionComponent player) {
		if(monster.get().dst(player.get()) < 5.0f) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	private boolean canSeePlayer(PositionComponent monster, PositionComponent player) {
		
		int dx = (int) (player.getX() - monster.getX());
		int dy = (int) (player.getY() - monster.getY());
		
		if(dx == 0 && dy == 0) {
			return true;
		}
		
		if(dx == 0) {
			for(int y = (int) monster.getY(); y <= ((int) player.getY()); y++) {
				//if(!mTileMap.contains(mTempTilePos.set(monster.getX(), y))) {
				if(!mTileMap[(int) monster.getX()][y]) {
					return false;
				}
			}
			for(int y = (int) monster.getY(); y >= ((int) player.getY()); y--) {
				if(!mTileMap[(int) monster.getX()][y]) {
					return false;
				}
			}
			
			return true;
		}
		
		float error = 0;
		float dError = Math.abs(dy / dx);
		
		int y = (int) monster.getY();
		
		for(int x = (int) monster.getX(); x <= ((int) player.getX()); x++) {
			
			// check tile pos
			if(!mTileMap[x][y]) {
				return false;
			}
			
			error += dError;
			if(error >= 0.5f) {
				y ++;
				error -= 1.0f;
			}
		}
		
		for(int x = (int) monster.getX(); x >= ((int) player.getX()); x--) {
			
			// check tile pos
			if(!mTileMap[x][y]) {
				return false;
			}
			
			error += dError;
			if(error >= 0.5f) {
				y ++;
				error -= 1.0f;
			}
		}
		
		return true;
	}
	
	public void setTileMap(boolean [][] tileMap) {
		mTileMap = tileMap;
	}
}
