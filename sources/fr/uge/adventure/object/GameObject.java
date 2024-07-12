package fr.uge.adventure.object;

import fr.uge.adventure.collision.HitBox;
import fr.uge.adventure.entity.Entity;

public interface GameObject {
	GameObjectType objType();
	String name();
	String skin();
	double wrldX();
	double wrldY();
	HitBox hitBox();
	String openWith();
	boolean isCollidable();
	void event(Entity entity, boolean player);
}
