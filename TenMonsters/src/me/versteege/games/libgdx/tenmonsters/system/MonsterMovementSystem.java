package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent.MovementState;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.PlayerManager;
import com.artemis.systems.EntityProcessingSystem;

public class MonsterMovementSystem extends EntityProcessingSystem {

	@Mapper private ComponentMapper<PositionComponent> mPositionMapper;
	@Mapper private ComponentMapper<MonsterMovementStateComponent> mMovementStateMapper;
	@Mapper private ComponentMapper<ShapeComponent> mShapeMapper;
	
	@SuppressWarnings("unchecked")
	public MonsterMovementSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class, MonsterComponent.class, MonsterMovementStateComponent.class, ShapeComponent.class));
	}

	@Override
	protected void process(Entity entity) {
		
		PositionComponent positionComponent = mPositionMapper.get(entity);
		MonsterMovementStateComponent movementStateComponent = mMovementStateMapper.get(entity);
		ShapeComponent shapeComponent = mShapeMapper.get(entity);
		
		Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
		PositionComponent playerPosition = player.getComponent(PositionComponent.class);
		
		/*// check for state swap
		if(movementStateComponent.getState() == State.STILL) {
			float disToPlayer = positionComponent.get().dst(playerPosition.get());
			
			if(disToPlayer < 5) {
				shapeComponent.setColor(0.0f, 0.0f, 0.0f, 1.0f);
				movementStateComponent.setState(State.ENGAGING);
			}
		}
		else if(movementStateComponent.getState() == State.ENGAGING) {
			
		}*/
	}
}
