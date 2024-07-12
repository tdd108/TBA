package fr.uge.adventure.gamedata;

import java.util.Objects;

public record PlayerData(String name, String skin, Position pos, double health) implements ElementData{
	public PlayerData {
		Objects.requireNonNull(name);
		Objects.requireNonNull(skin);
		Objects.requireNonNull(pos);
	}

	@Override
	public DataType type() {
		return DataType.Player;
	}
}
