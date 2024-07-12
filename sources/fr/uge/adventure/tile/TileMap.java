package fr.uge.adventure.tile;

import java.util.Objects;

import fr.uge.adventure.gamedata.MapData;

public class TileMap {
	private final int col;
	private final int row;
	private double xOffSet;
	private double yOffSet;
	private final Tile[][] tiles;
	
	public TileMap(MapData data) {
		Objects.requireNonNull(data);
		
		this.col = data.col();
		this.row = data.row();
		tiles = new Tile[row][col];
		for (int row = 0; row < this.row(); row++) {
			for (int col = 0; col < this.col(); col++) {
				if (data.tiles()[row][col] == null) {
					tiles[row][col] = null;
					continue;
				}
				tiles[row][col] = new Tile(data.tiles()[row][col], data.tiles()[row][col].isCollidable());
			}
		}
	}
	
	public int col() {
		return col;
	}
	
	public int row() {
		return row;
	}
	
	public Tile[][] tiles() {
		return this.tiles;
	}

	public double xOffSet() {
		return xOffSet;
	}

	public void setXOffSet(double xOffSet) {
		this.xOffSet = xOffSet;
	}

	public double yOffSet() {
		return yOffSet;
	}

	public void setYOffSet(double yOffSet) {
		this.yOffSet = yOffSet;
	}
}
