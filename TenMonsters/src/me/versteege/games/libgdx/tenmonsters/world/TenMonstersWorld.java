package me.versteege.games.libgdx.tenmonsters.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.versteege.games.libgdx.tenmonsters.component.CameraFollowComponent;
import me.versteege.games.libgdx.tenmonsters.component.HealthComponent;
import me.versteege.games.libgdx.tenmonsters.component.AttackCooldownComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterCombatStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterComponent;
import me.versteege.games.libgdx.tenmonsters.component.MonsterMovementStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionHistoryComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent;
import me.versteege.games.libgdx.tenmonsters.component.TileWalkingStateComponent.TileWalkingState;
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
import me.versteege.games.libgdx.tenmonsters.system.rendering.ShapeRenderingSystem;
import me.versteege.games.libgdx.tenmonsters.system.rendering.SpriteRenderingSystem;
import me.versteege.games.libgdx.tenmonsters.system.rendering.ZIndexedEntity;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TenMonstersWorld extends World {
	
	public static final int WORLD_TO_PIXEL = 50;
	
	private final PlayerManager mPlayerManager;
	
	private final List<Vector2> mTiles;
	private final List<Vector2> mMonsterPos;
	
	private boolean [][] mTileMap;
	
	private Direction mPathDirection;
	
	private Entity mPlayer;
	private Vector2 mPlayerPos;
	
	public TenMonstersWorld() {
		super();
		
		mPlayerManager = new PlayerManager();
		
		mTiles = new LinkedList<Vector2>();
		mMonsterPos = new LinkedList<Vector2>();
		
		setSystem(new SpriteRenderingSystem());
		setSystem(new ShapeRenderingSystem());
		setSystem(new CameraTrackingSystem());
		setSystem(new PlayerMovementSystem());
		setSystem(new MonsterMovementSystem());
		setSystem(new MonsterCombatSystem());
		setSystem(new PlayerCombatSystem());
		
		initialize();
	}
	
	public void initialize() {
		super.initialize();
		
		List<ZIndexedEntity> zIndexedEntities = new ArrayList<ZIndexedEntity>(20);
		
		generate();
		adjustTiles();
		createTiles();
		createMonsters(mPlayerPos, zIndexedEntities);
		populateTileMap();
		
		mPlayer = createEntity();
		mPlayer.addComponent(new PlayerComponent());
		mPlayer.addComponent(new WaitCooldownComponent());
		mPlayer.addComponent(new CameraFollowComponent());
		mPlayer.addComponent(new PositionComponent(mPlayerPos.x, mPlayerPos.y));
		mPlayer.addComponent(new ShapeComponent(1, 1.5f, new Color(0.0f, 1.0f, 0.0f, 1.0f)));
		mPlayer.addComponent(new HealthComponent(100));
		mPlayer.addComponent(new PositionHistoryComponent());
		mPlayer.addToWorld();
		
		zIndexedEntities.add(new ZIndexedEntity(mPlayer));
		
		getSystem(ShapeRenderingSystem.class).setZIndexedEntities(zIndexedEntities);

		mPlayerManager.setPlayer(mPlayer, "player");
	}
	
	public PlayerManager getPlayerManager() {
		return mPlayerManager;
	}
	
	private void generate() {
		
		Vector2 currentPos = new Vector2(0, 0);
		Direction lastDirection = null;
		
		int monsterCount = 0;
		
		while(monsterCount < 10) {
			
			mTiles.add(new Vector2(currentPos.x, currentPos.y));
			mTiles.add(new Vector2(currentPos.x + 1, currentPos.y));
			mTiles.add(new Vector2(currentPos.x, currentPos.y + 1));
			mTiles.add(new Vector2(currentPos.x + 1, currentPos.y + 1));
			
			//////////////////////////////////////////////////
			
			if(MathUtils.random() < 0.075f) {
				mMonsterPos.add(new Vector2(currentPos.x, currentPos.y));
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
	
	private void adjustTiles() {
		
		float minX = Float.MAX_VALUE;
		float minY = Float.MIN_VALUE;
		
		// find bottom - left tile
		for(Vector2 tilePos : mTiles) {
			if(tilePos.x < minX) minX = tilePos.x;
			if(tilePos.y < minY) minY = tilePos.y;
		}
		
		float tX = minX - 1;
		float tY = minY - 1;
		
		for(Vector2 tilePos : mTiles) {
			tilePos.sub(tX, tY);
		}
		
		// adjust monsters
		for(Vector2 monsterPos : mMonsterPos) {
			monsterPos.sub(tX, tY);
		}
		
		positionPlayer(-tX, -tY);
	}
	
	private void positionPlayer(float x, float y) {
		mPlayerPos = new Vector2(x, y);
	}
	
	private void createTiles() {
		
		for(Vector2 tilePos : mTiles) {
			Entity pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(tilePos.x, tilePos.y));
			pathTile.addComponent(new ShapeComponent(1, 1, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
		}
	}
	
	private void createMonsters(Vector2 playerPos, List<ZIndexedEntity> zIndexedEntities) {
		
		for(Vector2 monsterPos : mMonsterPos) {
			
			while(monsterPos.dst(playerPos) < 10) {
				monsterPos.set(mMonsterPos.get(MathUtils.random(mMonsterPos.size() - 1)));
			}
			
			Entity monster = createEntity();
			monster.addComponent(new MonsterComponent());
			monster.addComponent(new MonsterMovementStateComponent(MovementState.STILL));
			monster.addComponent(new MonsterCombatStateComponent(CombatState.DORMANT));
			monster.addComponent(new TileWalkingStateComponent(TileWalkingState.IDLE));
			monster.addComponent(new TileWalkingComponent(0.2f));
			monster.addComponent(new AttackCooldownComponent());
			monster.addComponent(new HealthComponent(50));
			monster.addComponent(new WaitCooldownComponent());
			monster.addComponent(new PositionComponent(monsterPos.x, monsterPos.y));
			monster.addComponent(new ShapeComponent(1, 1.5f, new Color(1.0f, 0.0f, 0.0f, 1.0f)));
			monster.addToWorld();
			
			zIndexedEntities.add(new ZIndexedEntity(monster));
		}
	}
	
	private void populateTileMap() {
		
		// find max tile
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		
		for(Vector2 tilePos : mTiles) {
			if(tilePos.x > maxX) maxX = tilePos.x;
			if(tilePos.y > maxY) maxY = tilePos.y;
		}
		
		// populate!
		mTileMap = new boolean [(int) (maxX + 1 + 1)][(int) (maxY + 1 + 1)];
		for(Vector2 tilePos : mTiles) {
			mTileMap[(int) tilePos.x][(int) tilePos.y] = true;
		}
		
		getSystem(PlayerMovementSystem.class).setTileMap(mTileMap);
		getSystem(MonsterMovementSystem.class).setTileMap(mTileMap);
		getSystem(MonsterCombatSystem.class).setTileMap(mTileMap);
		getSystem(ShapeRenderingSystem.class).setTileMap(mTileMap);
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
