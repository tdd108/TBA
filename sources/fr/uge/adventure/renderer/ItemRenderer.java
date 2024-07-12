package fr.uge.adventure.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.ulti.Utilities;

public class ItemRenderer {
	private final ArrayList<Item> lstItem;
	private final GameRenderer gameRenderer;
	private final ArrayList<Timer> lstAnimTimers;
	private long animationTime = 100; // milliseconds
	private int[] animIndexes;
	
	public ItemRenderer(ArrayList<Item> lstItem, GameRenderer gameRenderer) {
		Objects.requireNonNull(lstItem);
		Objects.requireNonNull(gameRenderer);
		
		this.gameRenderer = gameRenderer;
		this.lstItem = lstItem;
		this.lstAnimTimers = new ArrayList<Timer>();
		this.animIndexes = new int[lstItem.size()];
		
		initializeTimers();
	}
	
	public void update() {
		animateItem();
	}
	
	public void render(Graphics2D g2) {
		Objects.requireNonNull(g2);
		
		for (int i = 0; i < lstItem.size(); i++) {
			Item currentItem = lstItem.get(i);
			if (!gameRenderer.cam().isItemInRange(currentItem)) {
				continue;
			}
			int currentIndexAnim = animIndexes[i];
			var texture = gameRenderer.texture().lstItemTextureScaled().get(currentItem.skin());
			BufferedImage currentTexture = texture.get(currentIndexAnim);
			g2.drawImage(currentTexture, null, (int) (currentItem.wrldX() - gameRenderer.cam().camX()), 
						(int) (currentItem.wrldY() - gameRenderer.cam().camY()));
		}
	}
	
	private void initializeTimers() {
		for (int i = 0; i < lstItem.size(); i++) {
			lstAnimTimers.add(new Timer());
		}
	}
	
	private void animateItem() {
		for (int i = 0; i < lstItem.size(); i++) {
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
