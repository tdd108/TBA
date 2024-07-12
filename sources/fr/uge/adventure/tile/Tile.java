package fr.uge.adventure.tile;

public class Tile {
	private final boolean isCollidable;
	public final TileType tileType;
	
	public Tile(TileType tileType, boolean isCollidable) {
		this.tileType = tileType;
		this.isCollidable = isCollidable;
	}
	
	public TileType tileType() {
		return this.tileType;
	}
	
	public boolean isCollidable() {
		return this.isCollidable;
	}
}
