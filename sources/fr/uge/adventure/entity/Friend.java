package fr.uge.adventure.entity;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.element.Element;
import fr.uge.adventure.element.ElementType;
import fr.uge.adventure.gamedata.EnemyData;
import fr.uge.adventure.gamedata.FriendData;
import fr.uge.adventure.gamedata.ItemData;
import fr.uge.adventure.gamedata.Zone;
import fr.uge.adventure.item.Food;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.item.Key;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.main.GameState;
import fr.uge.adventure.renderer.Timer;
import fr.uge.adventure.ulti.Direction;

public class Friend implements Element, Entity{
	private double wrldX; private double wrldY;
	private double scrX; private double scrY;
	private double speed; private double xSpd; private double ySpd;
	private double xDir = 0, yDir = 0;
	private int step = 0;
	
	private final Zone zone;
	
	private double health;
	private final String name;
	private final String skin;
	
	private boolean collision;
	private Direction direction;
	private final Game game;
	private final HitBox hitBox;
	
	private final HashMap<String, ArrayList<ItemData>> lstTrade;
	private final ArrayList<Item> inventory;
	
	public Friend(FriendData friendData, Game game) {
		Objects.requireNonNull(friendData);
		Objects.requireNonNull(game);
		this.game = game;
		this.setHealth(friendData.health());
		this.speed = 2;
		this.name = friendData.name();
		this.skin = friendData.skin();
		this.setHealth(friendData.health());
		this.lstTrade = friendData.lstTrade();
		this.inventory = new ArrayList<Item>();
		loadInventory();
		
		this.wrldX = (double) friendData.pos().x() * game.tileSize();
		this.wrldY = (double) friendData.pos().y() * game.tileSize();
		this.direction = Direction.UP;
		this.zone = friendData.zone();
		this.hitBox = new HitBox(15, 20, game.tileSize() - 25, game.tileSize() - 20);
	}
	
	public void update() {		
		move();
		
		hitBox.update(wrldX, wrldY);
		
		game.coliCheck().checkTile(this);
		
		game.coliCheck().checkPlayer(this);
		
		if (game.tileSize() * zone.x() < wrldX + xSpd && 
			wrldX + xSpd < game.tileSize() * (zone.x() + zone.width()))
			wrldX += xSpd;
		if (game.tileSize() * (zone.y() - 1) < wrldY + ySpd && 
			wrldY + ySpd < game.tileSize() * (zone.y() + zone.height() + 1))
			wrldY += ySpd;
	}
	
	private void loadInventory() {
		if (lstTrade == null)
			return;
		for (var lstItem : lstTrade.values()) {
			for (var item : lstItem) {
				switch (item.skin()) {
				case "KEY":
					inventory.add(new Key(item.name(), item.skin()));
					break;
				case "PIZZA", "CAKE":
					inventory.add(new Food(item.name(), item.skin()));
					break;
				}
				
			}
		}
	}
	
	private void move() {		
		double normalizedX, normalizedY, length;
		xSpd = 0; ySpd = 0;
		
		int[] dir = {-1, 0, 1};
		int index;
		Random random = new Random();
		
		if (step == 0) {
			step = random.nextInt(100, 200);
	        index = random.nextInt(3);
	        xDir = dir[index];
	        index = random.nextInt(3);
	        yDir = dir[index];
		}
		else {
			step--;
		}
		
		if (xDir > 0) {
			direction = Direction.RIGHT;
		}
		if (xDir < 0) {
			direction = Direction.LEFT;
		}
		if (yDir < 0) {
			direction = Direction.UP;
		}
		if (yDir > 0) {
			direction = Direction.DOWN;
		}
		
		length = Math.sqrt(xDir * xDir + yDir * yDir);
		
		if (length != 0) {
			normalizedX = xDir / length;
			normalizedY = yDir / length;
			xSpd = normalizedX * speed();
			ySpd = normalizedY * speed();
		}
	}
	
	public void trade() {
		Item item1, item2;
		item1 = game.uiMng().item1();
		item2 = game.uiMng().item2();
		
		var lstItem = lstTrade.getOrDefault(item2.skin(), null);
		System.out.println(item2.skin());
		
		if (lstItem != null) {
			for (var item : lstItem) {
				System.out.println(item.skin());
				if (item.skin().equals(item1.skin())) {
					game.player().inventory().add(item1);
					game.player().inventory().remove(item2);
					
					inventory.add(item2);
					inventory.remove(item1);
					game.uiMng().setName(name);
					game.uiMng().setContentTextBox("Thank you!");
					game.setGameState(GameState.dialogueScr);
					return;
				}
			}
		}else {
			game.uiMng().setName(name);
			game.uiMng().setContentTextBox("I don't want this!");
			game.setGameState(GameState.dialogueScr);	
			return;
		}
	}
	
	public void event() {
		if(inventory().size() == 0) {
			game.uiMng().setName(name);
			game.uiMng().setContentTextBox("Hello There, Welcome to our village!");
			game.setGameState(GameState.dialogueScr);
			return;
		}
		
		if (game.player().inventory().size() == 0) {
			game.uiMng().setName(name);
			game.uiMng().setContentTextBox("You dont have anything to trade. Go around find treasure and come back");
			game.setGameState(GameState.dialogueScr);
			return;
		}
		game.uiMng().setName(name);
		game.uiMng().setContentTextBox("Do you want to trade");
		game.setGameState(GameState.dialogueScr);
		game.uiMng().setOption(new ArrayList<String>(List.of("yes", "no")));
		game.uiMng().setChooseOption(true);
	}
	
	@Override
	public double wrldX() {
		return this.wrldX;
	}

	@Override
	public double wrldY() {
		return this.wrldY;
	}

	@Override
	public double scrX() {
		return this.scrX;
	}

	@Override
	public double scrY() {
		return this.scrY;
	}

	@Override
	public double speed() {
		return this.speed;
	}

	@Override
	public boolean collision() {
		return this.collision;
	}

	@Override
	public ElementType type() {
		return ElementType.Entity;
	}

	@Override
	public Direction direction() {
		return this.direction;
	}
	
	public void setDirection(Direction dir) {
		this.direction = dir;
	}
	
	public double xSpd() {
		return this.xSpd;
	}
	
	public double ySpd() {
		return ySpd;
	}

	@Override
	public void setXSpd(double xSpd) {
		this.xSpd = xSpd;
	}

	@Override
	public void setYSpd(double ySpd) {
		this.ySpd = ySpd;
	}

	@Override
	public void setScrX(double scrX) {
		this.scrX = scrX;
	}

	@Override
	public void setScrY(double scrY) {
		this.scrY = scrY;
	}

	public String skin() {
		return this.skin;
	}

	public double health() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public String name() {
		return name;
	}

	@Override
	public HitBox hitBox() {
		return this.hitBox;
	}

	@Override
	public Item item() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItem(Item item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Item> inventory() {
		// TODO Auto-generated method stub
		return inventory;
	}

	@Override
	public double attackRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Zone zone() {
		return zone;
	}
	
	@Override
	public EntityType entityType() {
		return EntityType.friend;
	}
}
