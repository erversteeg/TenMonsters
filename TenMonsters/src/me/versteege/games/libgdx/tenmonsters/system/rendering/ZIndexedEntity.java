package me.versteege.games.libgdx.tenmonsters.system.rendering;

import com.artemis.Entity;

public class ZIndexedEntity implements Comparable<ZIndexedEntity> {
	
	private Entity mEntity;
	private int mZIndex;
	
	public ZIndexedEntity(Entity entity) {
		mEntity = entity;
		mZIndex = 0;
	}
	
	public int getZIndex() {
		return mZIndex;
	}
	
	public void setZIndex(int zIndex) {
		mZIndex = zIndex;
	}
	
	public Entity getEntity() {
		return mEntity;
	}

	@Override
	public int compareTo(ZIndexedEntity other) {
		return mZIndex - other.getZIndex();
	}
	
	@Override
	public boolean equals(Object o) {
		return ((ZIndexedEntity) o).getEntity().equals(mEntity);
	}
}
