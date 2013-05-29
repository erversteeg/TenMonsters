package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class TextComponent extends Component {

	private final String mText;
	
	public TextComponent(String text) {
		mText = text;
	}
	
	public String getText() {
		return mText;
	}
}
