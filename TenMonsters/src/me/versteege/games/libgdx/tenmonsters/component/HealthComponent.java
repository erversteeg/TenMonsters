package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class HealthComponent extends Component {

	private final int mMax;
	private int mCurrent;
	
	public HealthComponent(int max) {
		mMax = max;
		mCurrent = mMax;
	}
	
	public void damage(int amt) {
		mCurrent -= amt;
		
		if(mCurrent < 0) mCurrent = 0;
	}
	
	public void heal(int amt) {
		mCurrent += amt;
		
		if(mCurrent > mMax) mCurrent = mMax;
	}
	
	public float getPercent() {
		return mCurrent / (float) mMax;
	}
	
	public boolean isDead() {
		return mCurrent <= 0;
	}
}
