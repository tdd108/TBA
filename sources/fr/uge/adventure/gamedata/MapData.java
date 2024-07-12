package fr.uge.adventure.gamedata;

import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.tile.TileType;

public class MapData {
	private final int col;
	private final int row;
	private final TileType[][] tiles;
	private final HashMap<String, TileType> encodings;

	public MapData(Size size, HashMap<String, TileType> encodings, String[] gridData) {
		Objects.requireNonNull(size);
		Objects.requireNonNull(encodings);
		Objects.requireNonNull(gridData);

		this.col = size.col();
		this.row = size.row();
		this.encodings = encodings;
		this.tiles = loadMap(gridData);
	}

	public TileType[][] loadMap(String[] map) {
		var tiles = new TileType[row][col];

		if (map.length > row || map.length < row)
			throw new IllegalArgumentException("Map height error");

		for (int y = 0; y < row; y++) {
			if (map[y].length() > col || map[y].length() < col) {
				System.out.println(map[y].length());
				throw new IllegalArgumentException("Map width error");
			}
			for (int x = 0; x < col; x++) {
				if (map[y].charAt(x) == ' ') {
					continue;
				}
				tiles[y][x] = encodings.get(String.valueOf(map[y].charAt(x)));
			}
		}

		return tiles;
	}
	
	public int col() {
		return this.col;
	}
	
	public int row() {
		return this.row;
	}

	public TileType[][] tiles() {
		return tiles;
	}
}
