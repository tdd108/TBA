package fr.uge.adventure.gamedata;

import java.util.HashMap;
import java.util.Objects;

public record ObjectData(String name, String skin, Position pos, HashMap<String, String> strData, 
						HashMap<String, Integer> intData) implements ElementData{
	public ObjectData {
		Objects.requireNonNull(name);
		Objects.requireNonNull(skin);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(strData);
		Objects.requireNonNull(intData);
	}

	@Override
	public DataType type() {
		return DataType.Object;
	}
}
