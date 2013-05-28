package me.versteege.games.libgdx.tenmonsters.component;

import java.util.Stack;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionHistoryComponent extends Component {
	
	private Stack<Vector2> mHistory;

	public PositionHistoryComponent() {
		mHistory = new Stack<Vector2>();
	}
	
	public Vector2 peek() {
		return mHistory.peek();
	}
	
	public Vector2 peek(int down) {
		
		Vector2 [] temp = new Vector2 [down];
		for(int i = 0; i < temp.length; i++) {
			temp[i] = mHistory.pop();
		}
		
		Vector2 ret = mHistory.peek();
		
		for(int i = temp.length - 1; i >= 0; i--) {
			mHistory.push(temp[i]);
		}
		
		for(Vector2 item : mHistory) {
			System.out.println(item);
		}
		
		return ret;
	}
	
	public void push(Vector2 position) {
		mHistory.push(position);
	}
}
