package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class AttackCooldownComponent extends Component {
	
	private static final float COOLDOOWN_TIME = 0.5f;
	private float mElapsedTime = 0.0f;
	
	public void update(float deltaTime) {
		mElapsedTime += deltaTime;
	}
	
	public boolean shouldExecuteAttack() {
		boolean should = mElapsedTime >= COOLDOOWN_TIME;
		
		if(should) mElapsedTime = 0.0f;
		
		return should;
	}
	
	public void reset() {
		mElapsedTime = 0.0f;
	}
}
