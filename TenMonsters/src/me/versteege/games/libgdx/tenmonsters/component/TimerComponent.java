package me.versteege.games.libgdx.tenmonsters.component;

import com.artemis.Component;

public class TimerComponent extends Component {
	
	private long mStartMillis;
	
	public TimerComponent() {
		mStartMillis = System.currentTimeMillis();
	}
	
	public float getElapsedTime() {
		return (System.currentTimeMillis() - mStartMillis) / 1000.0f;
	}
}
