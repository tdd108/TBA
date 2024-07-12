package fr.uge.adventure.collision;

import fr.uge.adventure.entity.Entity;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.object.GameObject;
import fr.uge.adventure.tile.Tile;

public class CollisionChecker {
	private final Game game;

	public CollisionChecker(Game game) {
		this.game = game;
	}

	public void checkTile(Entity entity) {
		int entityHorizontalTile = 0;
		int entityVerticalTile = 0;
		int entityLeftWorldX = (int) (entity.hitBox().wrldX() / game.tileSize());
		int entityRightWorldX = (int) ((entity.hitBox().wrldX() + entity.hitBox().width()) / game.tileSize());
		int entityTopWorldY = (int) (entity.hitBox().wrldY() / game.tileSize());
		int entityBottomWorldY = (int) ((entity.hitBox().wrldY() + entity.hitBox().height()) / game.tileSize());
		
		//predict if the next position of player is in a tile thats is solid
		if (entity.xSpd() > 0) {
			entityHorizontalTile = (int) ((entity.hitBox().wrldX() + entity.hitBox().width() + entity.xSpd()) / game.tileSize());
		} else if (entity.xSpd() < 0)
			entityHorizontalTile = (int) ((entity.hitBox().wrldX() + entity.xSpd()) / game.tileSize());
		
		if (entity.ySpd() > 0) {
			entityVerticalTile = (int) ((entity.hitBox().wrldY() + entity.hitBox().height() + entity.ySpd()) / game.tileSize());
		} else if (entity.ySpd() < 0)
			entityVerticalTile = (int) ((entity.hitBox().wrldY() + entity.ySpd()) / game.tileSize());
		
		Tile tileNum1, tileNum2, tileNum3, tileNum4;
		
		//check two tiles for each direction
		tileNum1 = game.tileMap().tiles()[entityTopWorldY][entityHorizontalTile];
		tileNum2 = game.tileMap().tiles()[entityBottomWorldY][entityHorizontalTile];
		tileNum3 = game.tileMap().tiles()[entityVerticalTile][entityLeftWorldX];
		tileNum4 = game.tileMap().tiles()[entityVerticalTile][entityRightWorldX];
		
		if ((tileNum1 != null && tileNum1.isCollidable()) ||
			(tileNum2 != null && tileNum2.isCollidable())) {
			entity.setXSpd(0);
		}
		
		if ((tileNum3 != null && tileNum3.isCollidable())||
			(tileNum4 != null && tileNum4.isCollidable())) {
			entity.setYSpd(0);
		}
	}
	
	public Item checkItem(Entity entity) {
		for (var item : game.lstItem()) {
			if (item.hitBox() == null)
				continue;
			if (entity.hitBox().intersectInDistance(item.hitBox(), entity.xSpd(), entity.ySpd()))
				return item;
		}
		return null;
	}
	
	public GameObject checkObject(Entity entity) {
		for (var obj : game.lstObject()) {
			if (obj.hitBox() == null)
				continue;
			switch(game.player().direction()) {
			case UP:
			case DOWN:
				if (entity.hitBox().intersectInDistance(obj.hitBox(), 0, entity.ySpd())) {
					if (obj.isCollidable()) {
						entity.setYSpd(0);
					}
					return obj;
				}
			case LEFT:
			case RIGHT:
				if (entity.hitBox().intersectInDistance(obj.hitBox(), entity.xSpd(), 0)) {
					if (obj.isCollidable()) {
						entity.setXSpd(0);
					}
					return obj;
				}
			}
		}
		return null;
	}
	
	public Entity checkEntity(Entity entity) {
		for (var enemy : game.lstEnemy()) {
			if (entity == enemy)
				continue;
			
			if (enemy.hitBox() == null) 
				continue;
			switch(game.player().direction()) {
			case UP:
			case DOWN:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), 0, entity.ySpd())) {
					entity.setYSpd(0);
					return enemy;
				}
			case LEFT:
			case RIGHT:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), entity.xSpd(), 0)) {
					entity.setXSpd(0);
					return enemy;
				}
			}
		}
		
		for (var enemy : game.lstFriend()) {
			if (entity == enemy)
				continue;
			
			if (enemy.hitBox() == null) 
				continue;
			switch(game.player().direction()) {
			case UP:
			case DOWN:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), 0, entity.ySpd())) {
					entity.setYSpd(0);
					return enemy;
				}
			case LEFT:
			case RIGHT:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), entity.xSpd(), 0)) {
					entity.setXSpd(0);
					return enemy;
				}
			}
		}
		
		return null;
	}
	
	public Entity checkFriend() {
		for (var friend : game.lstFriend()) {
			if (friend.hitBox() == null) 
				continue;
			switch(game.player().direction()) {
			case UP:
				if (game.player().hitBox().intersectInDistance(friend.hitBox(), 0, -1 * game.player().interactRange())) {
					game.player().setYSpd(0);
					return friend;
				}
				break;
			case DOWN:
				if (game.player().hitBox().intersectInDistance(friend.hitBox(), 0, game.player().interactRange())) {
					game.player().setYSpd(0);
					return friend;
				}
				break;
			case LEFT:
				if (game.player().hitBox().intersectInDistance(friend.hitBox(), -1 * game.player().interactRange(), 0)) {
					game.player().setXSpd(0);
					return friend;
				}
				break;
			case RIGHT:
				if (game.player().hitBox().intersectInDistance(friend.hitBox(), game.player().interactRange(), 0)) {
					game.player().setXSpd(0);
					return friend;
				}
				break;
			}
		}
		return null;
	}
	
	public boolean checkPlayer(Entity entity) {
		if (game.player().hitBox() == null) 
			return false;
		if (entity.hitBox().intersectInDistance(game.player().hitBox(), entity.xSpd(), entity.ySpd())) {
			entity.setXSpd(0);
			entity.setYSpd(0);
			return true;
		}
		return false;
	}
	
	public Entity hitDetectEnemy(Entity entity) {
		for (var enemy : game.lstEnemy()) {
			if (enemy.hitBox() == null) 
				continue;
			switch(game.player().direction()) {
			case UP:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), 0, -1 * game.player().attackRange())) {
					return enemy;
				}
				break;
			case DOWN:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), 0, game.player().attackRange())) {
					return enemy;
				}
				break;
			case LEFT:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), -1 * game.player().attackRange(), 0)) {
					return enemy;
				}
				break;
			case RIGHT:
				if (entity.hitBox().intersectInDistance(enemy.hitBox(), game.player().attackRange(), 0)) {
					return enemy;
				}
				break;
			}
		}
		return null;
	}
}
