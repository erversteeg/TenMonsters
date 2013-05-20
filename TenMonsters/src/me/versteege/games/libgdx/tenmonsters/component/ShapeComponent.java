package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class ShapeComponent extends Component {

	private float mWidth;
	private float mHeight;
	private Color mColor;
	
	public ShapeComponent(float width, float height, Color color) {
		super();
		
		mWidth = width;
		mHeight = height;
		mColor = color;
	}
	
	public float getWidth() {
		return mWidth;
	}
	
	public float getHeight() {
		return mHeight;
	}
	
	public Color getColor() {
		return mColor;
	}
}
