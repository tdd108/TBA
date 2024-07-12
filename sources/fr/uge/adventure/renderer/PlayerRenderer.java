package fr.uge.adventure.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.camera.Camera;
import fr.uge.adventure.entity.Player;
import fr.uge.adventure.entity.PlayerState;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.ulti.Direction;
import fr.uge.adventure.ulti.Utilities;

public class PlayerRenderer {
	private final HashMap<Direction, ArrayList<BufferedImage>> pTexture;
	private ArrayList<BufferedImage> weaponTexture;
	private final Player player;
	private final GameRenderer gameRenderer;
	private final Timer animTimer;
	private final Timer blinkTimer;
	private long animationTime = 100000000; // 0.1 sec
	private static long blinkTime = 100000000;
	private int animIndex;
	private double weaponAngle = 0;
	private boolean attack = false;
	private double weaponX = 0;
	private double weaponY = 0;
	
	public PlayerRenderer(Player player, GameRenderer gameRenderer) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(gameRenderer);
		
		this.pTexture = new HashMap<Direction, ArrayList<BufferedImage>>();
		this.gameRenderer = gameRenderer;
		this.player = player;
		this.setWeaponTexture(new ArrayList<BufferedImage>());
		loadPlayerTexture(gameRenderer.ogSprSize());
		
		this.animTimer = new Timer();
		this.blinkTimer = new Timer();
	}
	
	public void update() {
		if (player.playerState() == PlayerState.attack) {
			weaponAnimation();
		}
		
		if (player.playerState() == PlayerState.hurt) {
			blinkTimer.update();
		}
		
		animatePlayer();
		
		updateWeaponDirection();
	}
	
	private void weaponAnimation() {
		switch(player.direction()) {
		case RIGHT:
		case LEFT:
			if (weaponX == 0 && !attack) {
				weaponX = 30;
				attack = true;
			}
			else 
				weaponX -= 5;
			break;
		case UP:
		case DOWN:
			if (weaponY == 0 && !attack) {
				weaponY = 30;
				attack = true;
			}
			else 
				weaponY -= 5;
			break;
		}
		if (weaponX == 0 && weaponY == 0 && attack) {
			attack = false;
			if (player.playerState() != PlayerState.hurt)
				player.setPlayerState(PlayerState.normal);
			else
				player.setPlayerState(PlayerState.hurt);
		}
	}
	
	public void render(Graphics2D g2) {
		
		Objects.requireNonNull(g2);
		Camera cam = gameRenderer.cam();
		
		if (player.playerState() == PlayerState.normal || player.playerState() == PlayerState.attack)
			g2.drawImage(pTexture.get(player.direction()).get(animIndex), null, (int) (player.wrldX() - cam.camX()), 
					(int) (player.wrldY() - cam.camY()));	
		
		if (player.playerState() == PlayerState.hurt)
			if (blinkTimer.tick() < blinkTime) {
				g2.drawImage(pTexture.get(player.direction()).get(animIndex), null, (int) (player.wrldX() - cam.camX()), 
						(int) (player.wrldY() - cam.camY()));
			}
			else 
				blinkTimer.reset();
		
		renderWeapon(g2);						
	}
	
	private void updateWeaponDirection() {
		switch(player.direction()) {
		case UP:
			weaponAngle = 0;
			break;
		case DOWN:
			weaponAngle = 180;
			break;
		case RIGHT:
			weaponAngle = 90;
			break;
		case LEFT:
			weaponAngle = 270;
			break;
		default:
			break;
		}
	}
	
	public void renderWeapon(Graphics2D g2) {
		Objects.requireNonNull(g2);
		Camera cam = gameRenderer.cam();
		double offSetX = 35, offSetY = 35;
		
		int dirX = 1, dirY = 1;
		switch(player.direction()) {
		case RIGHT:
			dirX = 1;
			offSetX = 35;
			offSetY = 10;
			break;
		case LEFT:
			dirX = -1;
			offSetX = 35;
			offSetY = 10;
			break;
		case UP:
			dirY = -1;
			offSetX = 10;
			offSetY = 35;
			break;
		case DOWN:
			dirY = 1;
			offSetX = 10;
			offSetY = 35;
			break;
		}
		
		if (player.weapon() != null) {
			BufferedImage texture = gameRenderer.texture().lstItemTextureScaled().get(player.weapon().skin()).get(0);
			g2.drawImage(Utilities.rotateImage(texture, weaponAngle), null, (int) (player.wrldX() - cam.camX() + dirX * (offSetX - weaponX)), 
					(int) (player.wrldY() - cam.camY() + dirY * (offSetY - weaponY)));
		}
	}
	
	private void loadPlayerTexture(double ogSprSize) {
		BufferedImage sprite = null;
		for (var dir : Direction.values()) {
			String pngName = player.skin().toLowerCase() + "_" + dir.toString().toLowerCase() + ".png";
			System.out.println(pngName);
			sprite = Utilities.loadImage("/fr/images/player/", pngName);
			var textureList = new ArrayList<BufferedImage>();

			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 4; col++) {
					BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row);
					BufferedImage sclFrm = Utilities.scaleImage(sprFrm, gameRenderer.scale());
					textureList.add(sclFrm);
				}
			}
			pTexture.put(dir, textureList);
		}
	}
	
	
	private void animatePlayer() {
		animTimer.update();
		
		if (player.xSpd() == 0 && player.ySpd() == 0) {
			animTimer.reset();
			animIndex = 0;
		}
		
		if (animTimer.tick() >= animationTime) {
			animTimer.reset();
			animIndex++;
			if (animIndex >= 11)
				animIndex = 0;
		}
	}

	public ArrayList<BufferedImage> weaponTexture() {
		return weaponTexture;
	}

	public void setWeaponTexture(ArrayList<BufferedImage> weaponTexture) {
		this.weaponTexture = weaponTexture;
	}
}
