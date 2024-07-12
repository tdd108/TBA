package fr.uge.adventure.tile;

import fr.uge.adventure.main.Game;

public class TileManager {
	private final Game game;
	private final TileMap tileMap;
	
	public TileManager(Game game) {
		this.game = game;
		this.tileMap = game.tileMap();
	}
	
	public void update() {
	}

	public TileMap tileMap() {
		return tileMap;
	}
}
