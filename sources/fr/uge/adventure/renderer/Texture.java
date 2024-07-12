package fr.uge.adventure.renderer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.main.Game;
import fr.uge.adventure.ulti.Direction;
import fr.uge.adventure.ulti.Utilities;

public class Texture {
	private final HashMap<String, ArrayList<BufferedImage>> lstItemTexture;
	private final HashMap<String, ArrayList<BufferedImage>> lstItemTextureScaled;
	private final HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>> lstEnemyTexture;
	private final HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>> lstEnemyTextureScaled;
	private final HashMap<String, ArrayList<BufferedImage>> lstUiTexture;
	private final HashMap<String, ArrayList<BufferedImage>> lstItemTextureUI;
	private static double ogSprSize = 25;
	private static int cellSize = 160;
	private final double scale;
	private final Game game;
	
	private static double heartUiScale = 1.5;
	
	public Texture(Game game, double scale) {
		Objects.requireNonNull(game);
		
		this.lstItemTexture = new HashMap<String, ArrayList<BufferedImage>>();
		this.lstItemTextureScaled = new HashMap<String, ArrayList<BufferedImage>>();
		this.lstEnemyTexture = new HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>>();
		this.lstEnemyTextureScaled = new HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>>();
		this.lstUiTexture = new HashMap<String, ArrayList<BufferedImage>>();
		this.lstItemTextureUI = new HashMap<String, ArrayList<BufferedImage>>();
		this.game = game;
		this.scale = scale;
		loadItemTexture();
		loadEnemyTexture();
		loadObjectTexture();
		loadUiTexture("love", heartUiScale);
		loadUiTexture("cash", heartUiScale);
	}
	
	private void loadItemTexture() {
		BufferedImage sprite = null;
		for (var item : game.lstItem()) {
			
			String pngName = item.skin().toLowerCase() + ".png";
			System.out.println(pngName);
			sprite = Utilities.loadImage("/fr/images/object/", pngName);
			var textureList = new ArrayList<BufferedImage>();
			var textureListScaled = new ArrayList<BufferedImage>();
			var textureListScaledUi = new ArrayList<BufferedImage>();

			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 1; col++) {
					BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row); //sprite frame
					if (item.skin().equals("STICK"))
						sprFrm = Utilities.rotateImage(sprFrm, -45);
					BufferedImage sclFrm = Utilities.scaleImage(sprFrm, scale); //scaled frame
					BufferedImage sclFrmUi = Utilities.scaleImage(sprFrm, cellSize / (2 * ogSprSize)); //scaled frame
					textureList.add(sprFrm);
					textureListScaled.add(sclFrm);
					textureListScaledUi.add(sclFrmUi);
				}
			}
			
			lstItemTexture.put(item.skin(), textureList);
			lstItemTextureScaled.put(item.skin(), textureListScaled);
			lstItemTextureUI.put(item.skin(), textureListScaledUi);
		}
	}
	
	private void loadObjectTexture() {
		BufferedImage sprite = null;
		for (var object : game.lstObject()) {
			
			String pngName = object.skin().toLowerCase() + ".png";
			System.out.println(pngName);
			sprite = Utilities.loadImage("/fr/images/object/", pngName);
			var textureList = new ArrayList<BufferedImage>();
			var textureListScaled = new ArrayList<BufferedImage>();
			var textureListScaledUi = new ArrayList<BufferedImage>();

			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 1; col++) {
					BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row); //sprite frame
					BufferedImage sclFrm = Utilities.scaleImage(sprFrm, scale); //scaled frame
					BufferedImage sclFrmUi = Utilities.scaleImage(sprFrm, cellSize / (2 * ogSprSize)); //scaled frame
					textureList.add(sprFrm);
					textureListScaled.add(sclFrm);
					textureListScaledUi.add(sclFrmUi);
				}
			}
			
			lstItemTexture.put(object.skin(), textureList);
			lstItemTextureScaled.put(object.skin(), textureListScaled);
			lstItemTextureUI.put(object.skin(), textureListScaledUi);
		}
	}
	
	private void loadEnemyTexture() {
		BufferedImage sprite = null;
		for (var enemy : game.lstEnemy()) {
			var currentTexture = new HashMap<Direction, ArrayList<BufferedImage>>();
			var currentTextureScaled = new HashMap<Direction, ArrayList<BufferedImage>>();
			for (var dir : Direction.values()) {
				String pngName = enemy.skin().toLowerCase() + "_" + dir.toString().toLowerCase() + ".png";
				System.out.println(pngName);
				sprite = Utilities.loadImage("/fr/images/player/", pngName);
				var textureList = new ArrayList<BufferedImage>();
				var textureListScaled = new ArrayList<BufferedImage>();
	
				for (int row = 0; row < 3; row++) {
					for (int col = 0; col < 4; col++) {
						BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row); //sprite frame
						BufferedImage sclFrm = Utilities.scaleImage(sprFrm, scale); //scaled frame
						textureList.add(sprFrm);
						textureListScaled.add(sclFrm);
					}
				}
				currentTexture.put(dir, textureList);
				currentTextureScaled.put(dir, textureListScaled);
			}
			lstEnemyTexture.put(enemy.skin(), currentTexture);
			lstEnemyTextureScaled.put(enemy.skin(), currentTextureScaled);
		}
	}
	
	private void loadUiTexture(String name, double scale) {
		BufferedImage sprite = null;
		String pngName = name + ".png";
		System.out.println(pngName);
		sprite = Utilities.loadImage("/fr/images/object/", pngName);
		var textureList = new ArrayList<BufferedImage>();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 1; col++) {
				BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row); //sprite frame
				BufferedImage sclFrm = Utilities.scaleImage(sprFrm, scale); //scaled frame
				textureList.add(sclFrm);
			}
		}
		lstUiTexture.put(name, textureList);
	}
	
	public HashMap<String, ArrayList<BufferedImage>> lstItemTexture() {
		return this.lstItemTexture;
	}
	
	public HashMap<String, ArrayList<BufferedImage>> lstItemTextureScaled() {
		return this.lstItemTextureScaled;
	}
	
	public HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>> lstEnemyTexture() {
		return this.lstEnemyTexture;
	}
	
	public HashMap<String, HashMap<Direction, ArrayList<BufferedImage>>> lstEnemyTextureScaled() {
		return this.lstEnemyTextureScaled;
	}
	
	public HashMap<String, ArrayList<BufferedImage>> lstUiTexture() {
		return this.lstUiTexture;
	}

	public HashMap<String, ArrayList<BufferedImage>> lstItemTextureUI() {
		return lstItemTextureUI;
	}
}
