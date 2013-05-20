package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent extends Component {
	
	private Texture mTexture;
	private Sprite mSprite;
	private float mWidth;
	private float mHeight;
	
	public SpriteComponent(FileHandle fileHandle, float width, float height) {
		super();
		
		mTexture = new Texture(fileHandle);
		mSprite = new Sprite(mTexture);
		
		mWidth = width;
		mHeight = height;
	}

	public Texture getTexture() {
		return mTexture;
	}

	public void setmTexture(Texture texture) {
		mTexture = texture;
	}

	public Sprite getSprite() {
		return mSprite;
	}

	public void setSprite(Sprite sprite) {
		mSprite = sprite;
	}
	
	public float getWidth() {
		return mWidth;
	}
	
	public float getHeight() {
		return mHeight;
	}
}
