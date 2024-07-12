package fr.uge.adventure.object;

import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.element.Element;
import fr.uge.adventure.element.ElementType;
import fr.uge.adventure.entity.Enemy;
import fr.uge.adventure.entity.Entity;
import fr.uge.adventure.entity.PlayerState;
import fr.uge.adventure.gamedata.ObjectData;
import fr.uge.adventure.gamedata.Position;
import fr.uge.adventure.gamedata.Zone;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.item.ItemType;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.main.GameState;

public class Water implements Element, GameObject{
	private double wrldX;
	private double wrldY;
	private final Game game;
	private final String name;
	private final String skin;
	private final HitBox hitBox;
	private final String flow;
//	private final Zone zone;
//	private final Position pos;
	
	public Water(ObjectData data, Game game) {
		this.game = game;
		this.name = data.name();
		this.skin = data.skin();
		this.flow = data.strData().get("flow");
		this.wrldX = (double) (data.pos().x() * game.tileSize());
		this.wrldY = (double) (data.pos().y() * game.tileSize());
		this.hitBox = new HitBox(0, 0, game.tileSize(), game.tileSize() + 15);
		this.hitBox.update(wrldX, wrldY);
	}
	
	@Override
	public void event(Entity entity, boolean player) {
		switch (flow) {
//		case ""
		}
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public GameObjectType objType() {
		return GameObjectType.fire;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String skin() {
		return this.skin;
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
	public HitBox hitBox() {
		return this.hitBox;
	}

	@Override
	public ElementType type() {
		return ElementType.GameObj;
	}

	@Override
	public String openWith() {
		return null;
	}
}
