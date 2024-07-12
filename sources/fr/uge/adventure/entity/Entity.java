package fr.uge.adventure.entity;

import java.util.ArrayList;

import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.ulti.Direction;

public interface Entity {
	double wrldX();
	double wrldY();
	double scrX();
	double scrY();
	void setScrX(double scrX);
	void setScrY(double scrY);
	double xSpd();
	double ySpd();
	void setXSpd(double xSpd);
	void setYSpd(double ySpd);
	double speed();
	boolean collision();
	HitBox hitBox();
	Item item();
	void setItem(Item item);
	ArrayList<Item> inventory();
	double attackRange();
	Direction direction();
	EntityType entityType();
}
