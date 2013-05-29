package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class FontComponent extends Component {
	
	private BitmapFont mFont;
	
	public FontComponent() {
		mFont = new BitmapFont();
		mFont.setColor(Color.WHITE);
	}
	
	public FontComponent(float scale) {
		mFont = new BitmapFont();
		mFont.setScale(scale);
		mFont.setColor(Color.WHITE);
	}
	
	public BitmapFont getFont() {
		return mFont;
	}

	public Component setColor(Color color) {
		mFont.setColor(color);
		return this;
	}
}
