package fr.uge.adventure.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.entity.Enemy;
import fr.uge.adventure.entity.Friend;
import fr.uge.adventure.ulti.Direction;
import fr.uge.adventure.ulti.Utilities;

public class FriendRenderer {
	private final ArrayList<Friend> lstFriend;
	private final GameRenderer gameRenderer;
	private final ArrayList<Timer> lstAnimTimers;
	private long animationTime = 100; // milliseconds
	private int[] animIndexes;
	
	public FriendRenderer(ArrayList<Friend> lstFriend, GameRenderer gameRenderer) {
		Objects.requireNonNull(lstFriend);
		Objects.requireNonNull(gameRenderer);
		this.gameRenderer = gameRenderer;
		this.lstFriend = lstFriend;
		this.animIndexes = new int[lstFriend.size()];
		this.lstAnimTimers = new ArrayList<Timer>();
		initializeTimers();
	}
	
	public void update() {
	}
	
	public void render(Graphics2D g2) {
		Objects.requireNonNull(g2);
		for (int i = 0; i < lstFriend.size(); i++) {
			Friend currentFriend = lstFriend.get(i);
			if (!gameRenderer.cam().isEntityInRange(currentFriend)) {
				continue;
			}
			animateFriend();
			int currentIndexAnim = animIndexes[i];
			var texture = gameRenderer.texture().lstEnemyTextureScaled().get(currentFriend.skin());
			BufferedImage currentTexture = texture.get(currentFriend.direction()).get(currentIndexAnim);
			g2.drawImage(currentTexture, null, (int) (currentFriend.wrldX() - gameRenderer.cam().camX()), 
						(int) (currentFriend.wrldY() - gameRenderer.cam().camY()));
		}
	}
	
	private void initializeTimers() {
		for (int i = 0; i < lstFriend.size(); i++) {
			lstAnimTimers.add(new Timer());
		}
	}
	
	private void animateFriend() {
		for (int i = 0; i < lstFriend.size(); i++) {
			Friend currentFriend = lstFriend.get(i);
			Timer currentTimer = lstAnimTimers.get(i);
			currentTimer.update();
			
			if (currentFriend.xSpd() == 0 && currentFriend.ySpd() == 0) {
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
