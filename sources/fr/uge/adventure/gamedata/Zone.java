package fr.uge.adventure.gamedata;

public record Zone(int x, int y, int width, int height) {
	public Zone {
		if (x < 0 || y < 0 || width < 0 || height < 0)
			throw new IllegalArgumentException("Zone attribute can't be negative");
	}
}
