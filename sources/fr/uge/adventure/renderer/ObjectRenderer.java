package fr.uge.adventure.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

import fr.uge.adventure.object.GameObject;

public class ObjectRenderer {
	private final ArrayList<GameObject> lstObject;
	private final GameRenderer gameRenderer;
	private final ArrayList<Timer> lstAnimTimers;
	private long animationTime = 100; // milliseconds
	private int[] animIndexes;
	
	public ObjectRenderer(ArrayList<GameObject> lstObject, GameRenderer gameRenderer) {
		Objects.requireNonNull(lstObject);
		Objects.requireNonNull(gameRenderer);
		
		this.gameRenderer = gameRenderer;
		this.lstObject = lstObject;
		this.lstAnimTimers = new ArrayList<Timer>();
		this.animIndexes = new int[lstObject.size()];
		
		initializeTimers();
	}
	
	public void update() {
		
	}
	
	public void render(Graphics2D g2) {
		Objects.requireNonNull(g2);
		
		for (int i = 0; i < lstObject.size(); i++) {
			GameObject currentItem = lstObject.get(i);
			if (!gameRenderer.cam().isObjectInRange(currentItem)) {
				continue;
			}
			animateItem();
			int currentIndexAnim = animIndexes[i];
			var texture = gameRenderer.texture().lstItemTextureScaled().get(currentItem.skin());
			BufferedImage currentTexture = texture.get(currentIndexAnim);
			g2.drawImage(currentTexture, null, (int) (currentItem.wrldX() - gameRenderer.cam().camX()), 
						(int) (currentItem.wrldY() - gameRenderer.cam().camY()));
		}
	}
	
	private void initializeTimers() {
		for (int i = 0; i < lstObject.size(); i++) {
			lstAnimTimers.add(new Timer());
		}
	}
	
	private void animateItem() {
		for (int i = 0; i < lstObject.size(); i++) {
			Timer currentTimer = lstAnimTimers.get(i);
			currentTimer.update();
			
			if (currentTimer.tick() >= animationTime * 1000000) {
				currentTimer.reset();
				animIndexes[i]++;
				if (animIndexes[i] >= 3)
					animIndexes[i] = 0;
			}
		}
	}
}
