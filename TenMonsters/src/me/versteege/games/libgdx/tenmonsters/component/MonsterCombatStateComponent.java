package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class MonsterCombatStateComponent extends Component {
	
	public enum CombatState {
		DORMANT,
		ENGAGING,
		WAITING,
		ATTACKING
	}
	
	private CombatState mState;
	
	public MonsterCombatStateComponent(CombatState start) {
		mState = start;
	}
	
	public void setState(CombatState state) {
		mState = state;
	}
	
	public CombatState getState() {
		return mState;
	}
}
