package me.versteege.games.libgdx.tenmonsters;

import com.badlogic.gdx.Game;

public class TenMonstersGame extends Game {
	
	@Override
	public void create() {		
		setScreen(new GameScreen());
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void render() {		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
