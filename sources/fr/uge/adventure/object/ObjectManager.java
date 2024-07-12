package fr.uge.adventure.object;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.adventure.gamedata.ItemData;
import fr.uge.adventure.gamedata.ObjectData;
import fr.uge.adventure.main.Game;

public class ObjectManager {
	private final Game game;
	private final ArrayList<GameObject> lstObject;
	
	public ObjectManager(Game game) {
		this.game = game;
		this.lstObject = game.lstObject();
		loadObjData(game.data().lstObjData(), game);
	}
	
	public void update() {
		
	}
	
	private void loadObjData(ArrayList<ObjectData> lstObjData, Game game) {
		Objects.requireNonNull(lstObjData);
		Objects.requireNonNull(game);
		
		for (var itemData : lstObjData) {
			switch (itemData.skin()) {
			case "DOOR":
				lstObject.add(new Door(itemData, game));
				break;
			case "FIRE":
				lstObject.add(new Fire(itemData, game));
				break;
			case "WATER":
				lstObject.add(new Water(itemData, game));
			default:
				break;
			}
		}
	}
	
	public void deleteItem(Object obj) {
		lstObject.remove(obj);
	}
}
