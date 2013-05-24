package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class TileWalkingStateComponent extends Component {

	public enum TileWalkingState {
		WALKING,
		IDLE
	}
	
	private TileWalkingState mState;
	
	public TileWalkingStateComponent(TileWalkingState start) {
		mState = start;
	}
	
	public void setState(TileWalkingState state) {
		mState = state;
	}
	
	public TileWalkingState getState() {
		return mState;
	}
}
