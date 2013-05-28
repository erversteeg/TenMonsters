package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * Holds width, height and color for game entities.
 * @author versteege
 *
 */

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
	
	public void setWidth(float width) {
		mWidth = width;
	}
	
	public float getHeight() {
		return mHeight;
	}
	
	public Color getColor() {
		return mColor;
	}
	
	public void setColor(float r, float g, float b, float a) {
		mColor.set(r, g, b, a);
	}
}
