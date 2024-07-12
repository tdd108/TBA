package fr.uge.adventure.entity;

import java.util.ArrayList;

import fr.uge.adventure.gamedata.EnemyData;
import fr.uge.adventure.main.Game;

public class EnemyManager {
	private final ArrayList<Enemy> lstEnemy;
	private Game game;
	
	public EnemyManager(Game game) {
		this.game = game;
		this.lstEnemy = game.lstEnemy();
		loadEnemies(game.data().lstEnemyData(), game);
	}
	
	public void update() {
		for (var enemy : lstEnemy) {
			if (!game.camera().isEntityInRange(enemy)) {
				continue;
			}
			enemy.update();
		}
	}
	
	private void loadEnemies(ArrayList<EnemyData> data, Game game) {
		for (var enemyData : data) {
			Enemy newEnemy = new Enemy(enemyData, game);
			this.lstEnemy.add(newEnemy);
		}
	}
}
