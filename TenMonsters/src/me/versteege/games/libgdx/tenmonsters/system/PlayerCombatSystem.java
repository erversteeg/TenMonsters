package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.PlayerComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.WaitCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.system.rendering.ShapeRenderingSystem;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentType;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class PlayerCombatSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public PlayerCombatSystem() {
		super(Aspect.getAspectForAll(MonsterComponent.class));
	}

	@Override
	protected void process(Entity entity) {
		
		PositionComponent position = entity.getComponent(PositionComponent.class);
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		PositionComponent playerPosition = player.getComponent(PositionComponent.class);
		
		WaitCooldownComponent playerWaitCooldown = player.getComponent(WaitCooldownComponent.class);
		
		if(playerPosition.get().dst(position.get()) == 1.0f) {
			if(Gdx.input.isKeyPressed(Keys.SPACE)) {
				if(playerWaitCooldown.shouldStartAttack()) {
					HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
					healthComponent.damage(10);
					
					if(healthComponent.isDead()) {
						world.getSystem(ShapeRenderingSystem.class).removeZIndexedEntity(entity);
						entity.deleteFromWorld();
					}
					
					playerWaitCooldown.reset();
				}
			}
		}
	}

	@Override
	protected void begin() {
		super.begin();
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		WaitCooldownComponent playerWaitCooldown = player.getComponent(WaitCooldownComponent.class);
		
		// update player wait cooldown
		playerWaitCooldown.update(world.delta);
	}	
}
