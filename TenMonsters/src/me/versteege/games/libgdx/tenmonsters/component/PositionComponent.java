package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Component {
	private Vector2 mPosition;
	
	public PositionComponent() {
		mPosition = new Vector2();
	}
	
	public PositionComponent(float x, float y) {
		mPosition = new Vector2(x, y);
	}
	
	public float getX() {
		return mPosition.x;
	}
	
	public float getY() {
		return mPosition.y;
	}
	
	public void setX(float x) {
		mPosition.x = x;
	}
	
	public void setY(float y) {
		mPosition.y = y;
	}
	
	public void setPosition(float x, float y) {
		mPosition.x = x;
		mPosition.y = y;
	}
	
	public void addX(float x) {
		mPosition.x += x;
	}
	
	public void addY(float y) {
		mPosition.y += y;
	}
}
