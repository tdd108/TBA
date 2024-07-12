package fr.uge.adventure.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.entity.Enemy;
import fr.uge.adventure.ulti.Direction;
import fr.uge.adventure.ulti.Utilities;

public class EnemyRenderer {
	private final ArrayList<Enemy> lstEnemy;
	private final GameRenderer gameRenderer;
	private final ArrayList<Timer> lstAnimTimers;
	private long animationTime = 100; // milliseconds
	private int[] animIndexes;
	
	public EnemyRenderer(ArrayList<Enemy> lstEnemy, GameRenderer gameRenderer) {
		Objects.requireNonNull(lstEnemy);
		Objects.requireNonNull(gameRenderer);
		this.gameRenderer = gameRenderer;
		this.lstEnemy = lstEnemy;
		this.animIndexes = new int[lstEnemy.size()];
		this.lstAnimTimers = new ArrayList<Timer>();
		initializeTimers();
	}
	
	public void update() {
		
	}
	
	public void render(Graphics2D g2) {
		Objects.requireNonNull(g2);
		for (int i = 0; i < lstEnemy.size(); i++) {
			Enemy currentEnemy = lstEnemy.get(i);
			if (!gameRenderer.cam().isEntityInRange(currentEnemy)) {
				continue;
			}
			animateEnemy();
			int currentIndexAnim = animIndexes[i];
			var texture = gameRenderer.texture().lstEnemyTextureScaled().get(currentEnemy.skin());
			BufferedImage currentTexture = texture.get(currentEnemy.direction()).get(currentIndexAnim);
			g2.drawImage(currentTexture, null, (int) (currentEnemy.wrldX() - gameRenderer.cam().camX()), 
						(int) (currentEnemy.wrldY() - gameRenderer.cam().camY()));
		}
	}
	
	private void initializeTimers() {
		for (int i = 0; i < lstEnemy.size(); i++) {
			lstAnimTimers.add(new Timer());
		}
	}
	
	private void animateEnemy() {
		for (int i = 0; i < lstEnemy.size(); i++) {
			Enemy currentEnemy = lstEnemy.get(i);
			Timer currentTimer = lstAnimTimers.get(i);
			currentTimer.update();
			
			if (currentEnemy.xSpd() == 0 && currentEnemy.ySpd() == 0) {
				currentTimer.reset();
				animIndexes[i] = 0;
			}
			
			if (currentTimer.tick() >= animationTime * 1000000) {
				currentTimer.reset();
				animIndexes[i]++;
				if (animIndexes[i] >= 11)
					animIndexes[i] = 0;
			}
		}
	}
}
