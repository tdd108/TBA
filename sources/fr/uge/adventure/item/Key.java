package fr.uge.adventure.item;

import java.awt.Rectangle;

import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.element.Element;
import fr.uge.adventure.element.ElementType;
import fr.uge.adventure.gamedata.ItemData;
import fr.uge.adventure.main.Game;

public class Key implements Element, Item{
	private final String name;
	private final String skin;
	
	private double wrldX;
	private double wrldY;
	private final HitBox hitBox;
	
	public Key(ItemData data, Game game) {
		this.name = data.name();
		this.skin = data.skin();
		this.wrldX = (double) (data.pos().x() * game.tileSize());
		this.wrldY = (double) (data.pos().y() * game.tileSize());
		this.hitBox = new HitBox(15, 20, game.tileSize() - 25, game.tileSize() - 20);
		this.hitBox.update(wrldX, wrldY);
	}
	
	public Key(String name, String skin) {
		this.name = name;
		this.skin = skin;
		this.hitBox = null;
	}

	@Override
	public ItemType itemType() {
		return ItemType.key;
	}

	@Override
	public ElementType type() {
		return ElementType.GameObj;
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
	public String name() {
		return this.name;
	}

	@Override
	public String skin() {
		return this.skin;
	}

	@Override
	public HitBox hitBox() {
		return this.hitBox;
	}
}
