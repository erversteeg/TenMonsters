package me.versteege.games.libgdx.tenmonsters.world;

import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.ShapeComponent;
import me.versteege.games.libgdx.tenmonsters.system.ShapeRenderingSystem;
import me.versteege.games.libgdx.tenmonsters.system.SpriteRenderingSystem;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TenMonstersWorld extends World {
	
	private static final int NUM_PATH_TILES = 100;
	private static final int WORLD_TO_PIXEL = 50;
	
	private Direction mPathDirection;
	
	public TenMonstersWorld() {
		super();
		
		setSystem(new SpriteRenderingSystem());
		setSystem(new ShapeRenderingSystem());
		initialize();
	}
	
	public void initialize() {
		super.initialize();
		
		generate();
		
		Entity player = createEntity();
		player.addComponent(new PositionComponent(100, 100));
		player.addComponent(new ShapeComponent(50, 75, new Color(1.0f, 0.0f, 0.0f, 1.0f)));
		player.addToWorld();
	}
	
	private void generate() {
		
		mPathDirection = Direction.RIGHT;
		
		Vector2 currentPos = new Vector2(0, 0);
		Direction lastDirection = null;
		
		for(int i = 0; i < NUM_PATH_TILES; i++) {
			
			Entity pathTile = createEntity();
			pathTile.addComponent(new PositionComponent(currentPos.x * WORLD_TO_PIXEL, currentPos.y * WORLD_TO_PIXEL));
			pathTile.addComponent(new ShapeComponent(WORLD_TO_PIXEL, WORLD_TO_PIXEL, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			pathTile.addToWorld();
			
			Direction nextDirection = nextPathDirection();
			
			if(lastDirection != null) {
				while(opposite(lastDirection, nextDirection)) {
					nextDirection = nextPathDirection();
				}
			}	
			
			switch(nextDirection) {
				case DOWN:
					currentPos.y--;
					break;
				case LEFT:
					currentPos.x--;
					break;
				case RIGHT:
					currentPos.x++;
					break;
				case UP:
					currentPos.y++;
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
