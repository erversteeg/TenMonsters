package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.AttackCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.WaitCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent.CombatState;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent.MovementState;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

public class MonsterCombatSystem extends EntityProcessingSystem {

	@Mapper private ComponentMapper<MonsterMovementStateComponent> mMovementStateMapper;
	@Mapper private ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper private ComponentMapper<ShapeComponent> mShapeMapper;
	@Mapper private ComponentMapper<MonsterCombatStateComponent> mCombatStateMapper;
	@Mapper private ComponentMapper<WaitCooldownComponent> mWaitCooldownMapper;
	@Mapper private ComponentMapper<AttackCooldownComponent> mAttackCooldownMapper;
	
	@SuppressWarnings("unchecked")
	public MonsterCombatSystem() {
		super(Aspect.getAspectForAll(MonsterMovementStateComponent.class, PositionComponent.class, ShapeComponent.class, MonsterCombatStateComponent.class, WaitCooldownComponent.class, AttackCooldownComponent.class));
	}

	@Override
	protected void process(Entity entity) {
		MonsterMovementStateComponent movementState = mMovementStateMapper.get(entity);
		PositionComponent position = mPositionMapper.get(entity);
		ShapeComponent shape = mShapeMapper.get(entity);
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
				if(position.get().dst(playerPosition.get()) == 1.0f) {
					combatState.setState(CombatState.WAITING);
					waitCooldown.reset();
					shape.setColor(0.0f, 0.0f, 0.0f, 1.0f);
				}
			}
		}
		else if(combatState.getState() == CombatState.WAITING) {
			if(waitCooldown.shouldStartAttack()) {
				combatState.setState(CombatState.ATTACKING);
				attackCooldown.reset();
				shape.setColor(0.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		
		else if(combatState.getState() == CombatState.ATTACKING) {
			if(attackCooldown.shouldExecuteAttack()) {
				
				Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
				PositionComponent playerPosition = player.getComponent(PositionComponent.class);
				HealthComponent playerHealth = player.getComponent(HealthComponent.class);
				
				System.out.println("Attacked!");
				
				if(position.get().dst(playerPosition.get()) == 1.0f) {
					System.out.println("Player hit!");
					playerHealth.damage(10);
				}
				
				combatState.setState(CombatState.WAITING);
				waitCooldown.reset();
				shape.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			}
		}
	}
}
