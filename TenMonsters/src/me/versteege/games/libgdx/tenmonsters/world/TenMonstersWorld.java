package me.versteege.games.libgdx.tenmonsters.world;

import java.util.LinkedList;
import java.util.List;

import me.versteege.games.libgdx.tenmonsters.component.CameraFollowComponent;
import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.AttackCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.WaitCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.PlayerComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent.CombatState;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent.MovementState;
import me.versteege.games.libgdx.tenmonsters.system.CameraTrackingSystem;
import me.versteege.games.libgdx.tenmonsters.system.MonsterCombatSystem;
import me.versteege.games.libgdx.tenmonsters.system.MonsterMovementSystem;
import me.versteege.games.libgdx.tenmonsters.system.PlayerCombatSystem;
import me.versteege.games.libgdx.tenmonsters.system.PlayerMovementSystem;
import me.versteege.games.libgdx.tenmonsters.system.ShapeRenderingSystem;
import me.versteege.games.libgdx.tenmonsters.system.SpriteRenderingSystem;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TenMonstersWorld extends World {
	
	private static final int NUM_PATH_TILES = 100;
	public static final int WORLD_TO_PIXEL = 50;
	
	private final PlayerManager mPlayerManager;
	
	private final List<Vector2> mTileMap;
	
	private Direction mPathDirection;
	
	public TenMonstersWorld() {
		super();
		
		mPlayerManager = new PlayerManager();
		
		mTileMap = new LinkedList<Vector2>();
		
		setSystem(new SpriteRenderingSystem());
		setSystem(new ShapeRenderingSystem());
		setSystem(new CameraTrackingSystem());
		setSystem(new PlayerMovementSystem(mTileMap));
		setSystem(new MonsterMovementSystem());
		setSystem(new MonsterCombatSystem());
		setSystem(new PlayerCombatSystem());
		
		initialize();
	}
	
	public void initialize() {
		super.initialize();
		
		generate();
		
		Entity player = createEntity();
		player.addComponent(new PlayerComponent());
		player.addComponent(new WaitCooldownComponent());
		player.addComponent(new CameraFollowComponent());
		player.addComponent(new PositionComponent(0, 0));
		player.addComponent(new ShapeComponent(1, 1.5f, new Color(0.0f, 1.0f, 0.0f, 1.0f)));
		player.addComponent(new HealthComponent(100));
		player.addToWorld();

		mPlayerManager.setPlayer(player, "player");
	}
	
	public PlayerManager getPlayerManager() {
		return mPlayerManager;
	}
	
	private void generate() {
		
		Vector2 currentPos = new Vector2(0, 0);
		Direction lastDirection = null;
		
		int monsterCount = 0;
		
		while(monsterCount < 10) {
			
			Entity pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(currentPos.x, currentPos.y));
			pathTile.addComponent(new ShapeComponent(1, 1, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
			
			mTileMap.add(new Vector2(currentPos.x, currentPos.y));
			
			pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(currentPos.x + 1, currentPos.y));
			pathTile.addComponent(new ShapeComponent(1, 1, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
			
			mTileMap.add(new Vector2(currentPos.x + 1, currentPos.y));
			
			pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(currentPos.x, currentPos.y + 1));
			pathTile.addComponent(new ShapeComponent(1, 1, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
			
			mTileMap.add(new Vector2(currentPos.x, currentPos.y + 1));
			
			pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(currentPos.x + 1, currentPos.y + 1));
			pathTile.addComponent(new ShapeComponent(1, 1, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
			
			mTileMap.add(new Vector2(currentPos.x + 1, currentPos.y + 1));
			
			//////////////////////////////////////////////////
			
			if(MathUtils.random() < 0.075f) {
				Entity monster = createEntity();
				monster.addComponent(new MonsterComponent());
				monster.addComponent(new MonsterMovementStateComponent(MovementState.STILL));
				monster.addComponent(new MonsterCombatStateComponent(CombatState.DORMANT));
				monster.addComponent(new AttackCooldownComponent());
				monster.addComponent(new HealthComponent(50));
				monster.addComponent(new WaitCooldownComponent());
				monster.addComponent(new PositionComponent(currentPos.x, currentPos.y));
				monster.addComponent(new ShapeComponent(1, 1.5f, new Color(1.0f, 0.0f, 0.0f, 1.0f)));
				monster.addToWorld();
				
				monsterCount++;
			}
			
			//////////////////////////////////////////////////
			
			Direction nextDirection = nextPathDirection();
			
			if(lastDirection != null) {
				while(opposite(lastDirection, nextDirection)) {
					nextDirection = nextPathDirection();
				}
			}
			
			switch(nextDirection) {
				case DOWN:
					currentPos.y -= 2;
					break;
				case LEFT:
					currentPos.x -= 2;
					break;
				case RIGHT:
					currentPos.x += 2;
					break;
				case UP:
					currentPos.y += 2;
					break;
			}
			
			lastDirection = nextDirection;
		}
	}
	
	private Direction nextPathDirection() {
		
		Direction nextDir = null;
		
		if(mPathDirection == null) {
			switch(MathUtils.random(3)) {
				case 0:
					nextDir = Direction.LEFT;
					mPathDirection = Direction.LEFT;
					break;
				case 1:
					nextDir = Direction.RIGHT;
					mPathDirection = Direction.RIGHT;
					break;
				case 2:
					nextDir = Direction.UP;
					mPathDirection = Direction.UP;
					break;
				case 3:
					nextDir = Direction.DOWN;
					mPathDirection = Direction.DOWN;
					break;
			}
		}
		else if(mPathDirection == Direction.LEFT) {
			switch(MathUtils.random(2)) {
				case 0:
					nextDir = Direction.LEFT;
					break;
				case 1:
					nextDir = Direction.UP;
					break;
				case 2:
					nextDir = Direction.DOWN;
					break;
			}
		}
		else if(mPathDirection == Direction.RIGHT) {
			switch(MathUtils.random(2)) {
				case 0:
					nextDir = Direction.RIGHT;
					break;
				case 1:
					nextDir = Direction.UP;
					break;
				case 2:
					nextDir = Direction.DOWN;
					break;
			}
		}
		else if(mPathDirection == Direction.UP) {
			switch(MathUtils.random(2)) {
				case 0:
					nextDir = Direction.LEFT;
					break;
				case 1:
					nextDir = Direction.RIGHT;
					break;
				case 2:
					nextDir = Direction.UP;
					break;
			}
		}
		else if(mPathDirection == Direction.DOWN) {
			switch(MathUtils.random(2)) {
				case 0:
					nextDir = Direction.LEFT;
					break;
				case 1:
					nextDir = Direction.RIGHT;
					break;
				case 2:
					nextDir = Direction.DOWN;
					break;
			}
		}
		
		return nextDir;
	}
	
	public boolean opposite(Direction dir1, Direction dir2) {
		switch(dir1) {
			case DOWN:
				return dir2 == Direction.UP;
			case LEFT:
				return dir2 == Direction.RIGHT;
			case RIGHT:
				return dir2 == Direction.LEFT;
			case UP:
				return dir2 == Direction.DOWN;
		}
		return false;
	}
}
