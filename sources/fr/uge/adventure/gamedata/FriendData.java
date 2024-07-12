package fr.uge.adventure.gamedata;

import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record FriendData(String name, String skin, Position pos, Zone zone, double health, String behavior, HashMap<String, ArrayList<ItemData>>  lstTrade) implements ElementData{
	public FriendData {
		Objects.requireNonNull(name);
		Objects.requireNonNull(skin);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(zone);
		Objects.requireNonNull(behavior);
	}

	@Override
	public DataType type() {
		return DataType.Friend;
	}
}