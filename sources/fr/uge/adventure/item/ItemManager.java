package fr.uge.adventure.item;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.adventure.gamedata.ItemData;
import fr.uge.adventure.main.Game;

public class ItemManager {
	private final Game game;
	private final ArrayList<Item> lstItem;
	
	public ItemManager(Game game) {
		this.game = game;
		this.lstItem = game.lstItem();
		loadItemData(game.data().lstItemData(), game);
	}
	
	public void update() {
		
	}
	
	private void loadItemData(ArrayList<ItemData> lstItemData, Game game) {
		Objects.requireNonNull(lstItemData);
		Objects.requireNonNull(game);
		
		for (var itemData : lstItemData) {
			switch (itemData.skin()) {
			case "KEY":
				lstItem.add(new Key(itemData, game));
				break;
			case "SWORD", "STICK":
				lstItem.add(new Weapon(itemData, game));
				break;
			case "BURGER", "PIZZA", "CAKE":
				lstItem.add(new Food(itemData, game));
				break;
			case "CASH":
				lstItem.add(new Cash(itemData, game));
			default:
				break;
			}
		}
	}
	
	public void deleteItem(Item item) {
		lstItem.remove(item);
	}
}
