package me.versteege.games.libgdx.tenmonsters.utils;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import me.versteege.games.libgdx.tenmonsters.world.Direction;

public class WorldUtils {

	public static Direction directionForUnitVector(Vector2 unitVec) {
		if(unitVec.x == 0) {
			if(unitVec.y == -1) {
				return Direction.DOWN;
			}
			else if(unitVec.y == 1) {
				return Direction.UP;
			}
		}
		else if(unitVec.y == 0) {
			if(unitVec.x == -1) {
				return Direction.LEFT;
			}
			else if(unitVec.x == 1) {
				return Direction.RIGHT;
			}
		}
		return null;
	}
	
	public static Vector2 closestTileToPosition(Vector2 position, Vector2 lookAdjacentTo, List<Vector2> exclude, Vector2 tempVec) {
		
		Vector2 unitVecRet = new Vector2();
		Vector2 tempUnitVec = new Vector2();
		float minDist = Float.MAX_VALUE;
		
		// left
		tempUnitVec.set(-1, 0);
		if(!exclude.contains(tempUnitVec)) {
			tempVec.set(lookAdjacentTo).add(tempUnitVec);
			minDist = position.dst(tempVec);
			unitVecRet.set(tempUnitVec);
		}
		
		// right
		tempUnitVec.set(1, 0);
		if(!exclude.contains(tempUnitVec)) {
			tempVec.set(lookAdjacentTo).add(tempUnitVec);
			float dist = position.dst(tempVec);
			if(dist < minDist) {
				minDist = dist;
				unitVecRet.set(tempUnitVec);
			}
		}
		
		// up
		tempUnitVec.set(0, 1);
		if(!exclude.contains(tempUnitVec)) {
			tempVec.set(lookAdjacentTo).add(tempUnitVec);
			float dist = position.dst(tempVec);
			if(dist < minDist) {
				minDist = dist;
				unitVecRet.set(tempUnitVec);
			}
		}
		
		// down
		tempUnitVec.set(0, -1);
		if(!exclude.contains(tempUnitVec)) {
			tempVec.set(lookAdjacentTo).add(tempUnitVec);
			float dist = position.dst(tempVec);
			if(dist < minDist) {
				minDist = dist;
				unitVecRet.set(tempUnitVec);
			}
		}
		
		return unitVecRet;
	}
	
	public static boolean isValidTile(Vector2 position, boolean [][] tileMap) {
		return tileMap[(int) position.x][(int) position.y];
	}
}
