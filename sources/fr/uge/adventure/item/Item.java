package fr.uge.adventure.item;

import java.awt.Rectangle;

import fr.uge.adventure.collision.HitBox;

public interface Item {
	ItemType itemType();
	String name();
	String skin();
	double wrldX();
	double wrldY();
	HitBox hitBox();
}
