package fr.uge.adventure.entity;

import java.util.ArrayList;
import java.util.Objects;
import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.element.Element;
import fr.uge.adventure.element.ElementType;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.item.ItemType;
import fr.uge.adventure.item.Weapon;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.object.Door;
import fr.uge.adventure.object.GameObject;
import fr.uge.adventure.renderer.Timer;
import fr.uge.adventure.ulti.Direction;

public class Player implements Element, Entity{
	private double wrldX; private double wrldY;
	private double scrX; private double scrY;
	private double speed; private double xSpd; private double ySpd;
	
	private double health;
	private final String name;
	private final String skin;
	
	private boolean collision;
	private Direction direction;
	private final Game game;
	private final HitBox hitBox;
	
	private PlayerState playerState;
	
	private final double attackRange = 50;
	private final double interactRange = 50;
	
	private final Timer hurtTimer;
	private static long hurtTime = 300;
	
	private Weapon weapon = null;
	private Item item = null;
	
	private final ArrayList<Item> inventory;
	private Friend trader;
	private int cash = 0;
	
	public Player(Game game) {
		Objects.requireNonNull(game);
		this.game = game;
		this.setHealth(game.data().playerData().health());
		this.speed = 4;
		this.name = game.data().playerData().name();
		this.skin = game.data().playerData().skin();
		this.setHealth(game.data().playerData().health());
		this.inventory = new ArrayList<Item>();
		this.playerState = PlayerState.normal;
		
		this.wrldX = (double) game.data().playerData().pos().x() * game.tileSize();
		this.wrldY = (double) game.data().playerData().pos().y() * game.tileSize();
		this.scrX = game.scrWidth() / 2;
		this.scrY = game.scrHeight() / 2;
		this.direction = Direction.UP;
		this.hitBox = new HitBox(15, 20, game.tileSize() - 25, game.tileSize() - 20);
		
		this.hurtTimer = new Timer();
	}
	
	public void update() {
		if (playerState == PlayerState.normal || playerState == PlayerState.hurt)
			move();
		hitBox.update(wrldX, wrldY);
		if (playerState != PlayerState.attack && playerState != PlayerState.hurt && weapon != null)
			attack();
		if (playerState == PlayerState.hurt)
			hurt();
		if (game.input().spaceTouch) {
			game.input().spaceTouch = false;
			interactFriend();
		}
		interact();
		
		game.coliCheck().checkTile(this);
		game.coliCheck().checkEntity(this);
		
		wrldX += xSpd;
		wrldY += ySpd;
	}
	
	private void move() {
		double xDir = 0, yDir = 0;
		double normalizedX, normalizedY;
		xSpd = 0;
		ySpd = 0;
		double length;
		
		if (game.input().rightPressed) {
			xDir = 1;
			direction = Direction.RIGHT;
		}
		if (game.input().leftPressed) {
			xDir = -1;
			direction = Direction.LEFT;
		}
		if (game.input().upPressed) {
			yDir = -1;
			direction = Direction.UP;
		}
		if (game.input().downPressed) {
			yDir = 1;
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
	
	public void attack() {
		if (game.input().spaceTouch) {
			game.input().spaceTouch = false;
			playerState = PlayerState.attack;
			Entity entity = game.coliCheck().hitDetectEnemy(this);
			if (entity != null && entity.entityType() == EntityType.enemy) {
				Enemy enemy = (Enemy) entity;
				enemy.setEnemyState(EnemyState.hurt);
				enemy.setHealth(enemy.health() - weapon.damage());
				if (enemy.health() < 0)
					game.lstEnemy().remove(enemy);
				switch (direction) {
				case UP:
					enemy.setYSpd(-100);
					break;
				case DOWN:
					enemy.setYSpd(100);
					break;
				case LEFT:
					enemy.setXSpd(-100);
					break;
				case RIGHT:
					enemy.setXSpd(100);
					break;
				default:
					break;
				}
			}
		}
	}
	
	public Item pickUpItem() {
		Item item = game.coliCheck().checkItem(this);
		if (item != null) {
			inventory.add(item);
			if (item.skin().equals("CASH"))
				cash++;
		}
		return item;
	}
	
	public void interact() {
		GameObject object = game.coliCheck().checkObject(this);
		
		if (object != null) {
			object.event(this, true);
		}
	}
	
	public void interactFriend() {
		Entity entity = game.coliCheck().checkFriend();

		if (entity != null && entity.entityType() == EntityType.friend) {
			System.out.println(entity.entityType());
			System.out.println("hell");
			Friend friend = (Friend) entity;
			trader = friend;
			friend.event();
			switch(direction) {
			case UP:
				friend.setDirection(Direction.DOWN);
				break;
			case DOWN:
				friend.setDirection(Direction.UP);
				break;
			case RIGHT:
				friend.setDirection(Direction.LEFT);
				break;
			case LEFT:
				friend.setDirection(Direction.RIGHT);
				break;
			}
		}
	}
	
	public void useItem() {
		int index;
		if (!game.input().spaceTouch)
			return;
		game.input().spaceTouch = false;
		index = game.uiMng().yCursorInv() * 3 + game.uiMng().xCursorInv();
		if (index >= inventory.size())
			return;
		
		Item item = inventory.get(index);
		
		if (item == null)
			return;
			
		switch(item.itemType()) {
		case weapon:
			if (this.weapon != item)
				setWeapon((Weapon) item);
			else 
				setWeapon(null);
			break;
		case food:
			inventory.remove(item);
			health += 1;
			break;
		case key:
			if (this.item != item)
				setItem(item);
			else 
				setItem(null);
			break;
		default:
			break;
		}			
	}
	
	private void hurt() {
		if (hurtTimer.tick() > hurtTime * 10000000) {
			hurtTimer.reset();
			playerState = PlayerState.normal;
		}
		else {
			hurtTimer.update();
		}
	}
	
	public PlayerState playerState() {
		return this.playerState;
	}
	
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}
	
	public ArrayList<Item> inventory() {
		return this.inventory;
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

	public String name() {
		return name;
	}

	public double health() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	@Override
	public HitBox hitBox() {
		return this.hitBox;
	}

	public Weapon weapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Item item() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public double attackRange() {
		return attackRange;
	}

	@Override
	public EntityType entityType() {
		return EntityType.player;
	}

	public int cash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public Friend trader() {
		return trader;
	}

	public void setTrader(Friend trader) {
		this.trader = trader;
	}

	public double interactRange() {
		return interactRange;
	}
}
