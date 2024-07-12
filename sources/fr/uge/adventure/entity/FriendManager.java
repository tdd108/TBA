package fr.uge.adventure.entity;

import java.util.ArrayList;

import fr.uge.adventure.gamedata.FriendData;
import fr.uge.adventure.main.Game;

public class FriendManager {
	private final ArrayList<Friend> lstFriend;
	private Game game;
	
	public FriendManager(Game game) {
		this.game = game;
		this.lstFriend = game.lstFriend();
		loadFriend(game.data().lstFriendData(), game);
	}
	
	public void update() {
		for (var friend : lstFriend) {
			if (!game.camera().isEntityInRange(friend)) {
				continue;
			}
			friend.update();
		}
	}
	
	private void loadFriend(ArrayList<FriendData> data, Game game) {
		for (var friendData : data) {
			Friend newFriend = new Friend(friendData, game);
			this.lstFriend.add(newFriend);
		}
	}
}
