package me.versteege.games.libgdx.tenmonsters.component;

import me.versteege.games.libgdx.tenmonsters.world.Direction;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class TileWalkingComponent extends Component {

	private final float mSpeed;
	private final Vector2 mPath;
	private final Vector2 mRequestedPosition;
	private Direction mDirection;
	
	public TileWalkingComponent(float speed) {
		mSpeed = speed;
		mPath = new Vector2();
		mRequestedPosition = new Vector2();
	}
	
	public float getSpeed() {
		return mSpeed;
	}
	
	public Vector2 getPath() {
		return mPath;
	}
	
	public Vector2 getRequestedPosition() {
		return mRequestedPosition;
	}
	
	public void setDirection(Direction direction) {
		mDirection = direction;
	}
	
	public Direction getDirection() {
		return mDirection;
	}
}
