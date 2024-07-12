package fr.uge.adventure.gamedata;

import java.util.ArrayList;
import java.util.Objects;

public class GameData {
	private final MapData mapData;
	private final PlayerData playerData;
	private final ArrayList<EnemyData> lstEnemyData;
	private final ArrayList<ItemData> lstItemData;
	private final ArrayList<ObjectData> lstObjData;
	private final ArrayList<FriendData> lstFriendData;
	
	public GameData(MapData mapData, PlayerData playerData, ArrayList<EnemyData> lstEnemyData,
					ArrayList<ItemData> lstItemData, ArrayList<ObjectData> lstObjData, ArrayList<FriendData> lstFriendData) {
		Objects.requireNonNull(mapData);
		Objects.requireNonNull(playerData);
		Objects.requireNonNull(lstEnemyData);
		Objects.requireNonNull(lstItemData);
		Objects.requireNonNull(lstFriendData);
		
		this.mapData = mapData;
		this.playerData = playerData;
		this.lstEnemyData = lstEnemyData;
		this.lstItemData = lstItemData;
		this.lstObjData = lstObjData;
		this.lstFriendData = lstFriendData;
	}
	
	public MapData map() {
		return this.mapData;
	}

	public PlayerData playerData() {
		return playerData;
	}

	public ArrayList<EnemyData> lstEnemyData() {
		return lstEnemyData;
	}
	
	public ArrayList<ItemData> lstItemData() {
		return lstItemData;
	}

	public ArrayList<ObjectData> lstObjData() {
		return lstObjData;
	}

	public ArrayList<FriendData> lstFriendData() {
		return lstFriendData;
	}
}
