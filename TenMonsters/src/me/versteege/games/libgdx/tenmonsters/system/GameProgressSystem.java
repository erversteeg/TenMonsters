package me.versteege.games.libgdx.tenmonsters.system;

import me.versteege.games.libgdx.tenmonsters.component.FontComponent;
import me.versteege.games.libgdx.tenmonsters.component.HUDComponent;
import me.versteege.games.libgdx.tenmonsters.component.PositionComponent;
import me.versteege.games.libgdx.tenmonsters.component.TextComponent;
import me.versteege.games.libgdx.tenmonsters.component.TimerComponent;
import me.versteege.games.libgdx.tenmonsters.world.TenMonstersWorld;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class GameProgressSystem extends VoidEntitySystem {

	private Vector2 mEndTile;
	
	public GameProgressSystem() {
		super();
	}

	@Override
	protected void processSystem() {
		if(mEndTile != null) {
			Entity player = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(0);
			if(player.getComponent(PositionComponent.class).get().equals(mEndTile)) {
				addEndingTextEntities();
				this.setPassive(true);
			}
		}
	}
	
	private void addEndingTextEntities() {
		float finalTime = ((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(2).getComponent(TimerComponent.class).getElapsedTime();
		Entity finalTimeText = world.createEntity();
		finalTimeText.addComponent(new HUDComponent());
		finalTimeText.addComponent(new FontComponent(5).setColor(Color.YELLOW));
		finalTimeText.addComponent(new TextComponent(String.format("%.1f", finalTime)));
		finalTimeText.addComponent(new PositionComponent(Gdx.graphics.getWidth() * 0.5f - 35, Gdx.graphics.getHeight() * 0.5f));
		finalTimeText.addToWorld();
		
		Entity restartMsg = world.createEntity();
		restartMsg.addComponent(new HUDComponent());
		restartMsg.addComponent(new FontComponent().setColor(Color.YELLOW));
		restartMsg.addComponent(new TextComponent("Press 'r' to restart."));
		restartMsg.addComponent(new PositionComponent(Gdx.graphics.getWidth() * 0.5f - 40, Gdx.graphics.getHeight() * 0.5f - 75));
		restartMsg.addToWorld();
		
		stopTimer();
	}
	
	private void stopTimer() {
		((TenMonstersWorld) world).getPlayerManager().getEntitiesOfPlayer("player").get(2).deleteFromWorld();
	}
	
	public void setEndTile(Vector2 tilePos) {
		mEndTile = tilePos;
	}
}
