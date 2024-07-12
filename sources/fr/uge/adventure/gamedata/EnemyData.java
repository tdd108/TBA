package fr.uge.adventure.gamedata;

import java.util.Objects;

import fr.uge.adventure.element.ElementType;
import fr.uge.adventure.entity.NpcBehavior;

public record EnemyData(String name, String skin, Position pos, Zone zone, double health, String behavior) implements ElementData{
	public EnemyData {
		Objects.requireNonNull(name);
		Objects.requireNonNull(skin);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(zone);
		Objects.requireNonNull(behavior);
	}

	@Override
	public DataType type() {
		return DataType.Enemy;
	}
}