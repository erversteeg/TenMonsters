package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class MonsterMovementStateComponent extends Component {
	
	public enum MovementState {
		STILL,
		MOVING
	}
	
	private MovementState mState;
	
	public MonsterMovementStateComponent(MovementState start) {
		mState = start;
	}
	
	public void setState(MovementState state) {
		mState = state;
	}
	
	public MovementState getState() {
		return mState;
	}
}
