package fr.uge.adventure.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.adventure.camera.Camera;
import fr.uge.adventure.tile.TileMap;
import fr.uge.adventure.tile.TileType;
import fr.uge.adventure.ulti.Utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MapRenderer {
	private final HashMap<TileType, ArrayList<BufferedImage>> tileTexture;
	private final BufferedImage[][] mapTexture;
	private final TileMap tileMap;
	private final GameRenderer gameRenderer;
	
	private final Timer animTimer;
	private long animationTime = 1000000000; 
	private int animIndex = 0;
	
	private int[][] animOgIndex;
	
	
	public MapRenderer(TileMap tileMap, GameRenderer gameRenderer) throws IOException {
		Objects.requireNonNull(tileMap);
		Objects.requireNonNull(gameRenderer);
		
		this.tileTexture = new HashMap<TileType, ArrayList<BufferedImage>>();
		this.gameRenderer = gameRenderer;
		this.tileMap = tileMap;
		this.mapTexture = new BufferedImage[tileMap.row()][tileMap.col()];
		this.animTimer = new Timer();
		this.animOgIndex = new int[tileMap.row()][tileMap.col()];
		loadTileTexture(tileMap, gameRenderer.ogSprSize());
		loadMapTexture();
	}
	
	public void update() {
		animateTile();
	}
	
	public void render(Graphics2D g2) {
		Objects.requireNonNull(g2);
		
		int x, y;
		Camera cam = gameRenderer.cam();
		for (int row = cam.cameraStartRow(); row < cam.cameraEndRow(); row++) {
			for (int col = cam.cameraStartCol(); col < cam.cameraEndCol(); col++) {
				x = (int) ((col * gameRenderer.tileSize()) - cam.camX());
				y = (int) ((row * gameRenderer.tileSize()) - cam.camY());
				if (tileMap.tiles()[row][col] != null) {
//					g2.drawImage(mapTexture[row][col], null, x, y);
//					animOgIndex[row][col] + animIndex * 16
					g2.drawImage(tileTexture.get(
								tileMap.tiles()[row][col].tileType).get(animOgIndex[row][col] + animIndex * 16), null, x, y);
				}
			}
		}
	}
	
	private void animateTile() {
		animTimer.update();
		
		if (animTimer.tick() >= animationTime) {
			animTimer.reset();
			animIndex++;
			if (animIndex >= 3)
				animIndex = 0;
		}
	}
	
	private void loadTileTexture(TileMap map, double ogSprSize) throws IOException {
		for (var tileType : TileType.values()) {
			String imgName = tileType.name().toLowerCase() + ".png";
			BufferedImage sprite = Utilities.loadImage("/fr/images/tiles/", imgName);
			
			if (sprite == null) {
				System.out.println("Image not found : " + imgName);
				continue;
			}
			
			int width = sprite.getWidth();
			int height = sprite.getHeight();
			int sprCol = (int) (width / ogSprSize);
			int sprRow = (int) (height / ogSprSize);
			
			var sprFrmList = new ArrayList<BufferedImage>();
			
			for (int row = 0; row < sprRow; row++) {
				for (int col = 0; col < sprCol; col++) {
					BufferedImage sprFrm = Utilities.getSpriteFrame(sprite, ogSprSize, col, row);
					BufferedImage sclFrm = Utilities.scaleImage(sprFrm, gameRenderer.scale());
					sprFrmList.add(sclFrm);
				}
			}
			
			tileTexture.put(tileType, sprFrmList);
		}
	}
	
	private void loadMapTexture() {
		TileType type, typeRight = null, typeLeft = null, typeUp = null, typeDown = null;
		int sum;
		
		for (int row = 0; row < tileMap.row(); row++) {
			for (int col = 0; col < tileMap.col(); col++) {
				if (tileMap.tiles()[row][col] == null) 
					continue;
				
				typeRight = null; typeLeft = null; typeUp = null; typeDown = null;
				sum = 0;
				type = tileMap.tiles()[row][col].tileType;
				
				if (col + 1 < tileMap.col() && tileMap.tiles()[row][col + 1] != null)
					typeRight = tileMap.tiles()[row][col + 1].tileType;
				if (col - 1 >= 0 && tileMap.tiles()[row][col - 1] != null)
					typeLeft = tileMap.tiles()[row][col - 1].tileType;
				if (row - 1 >= 0 && tileMap.tiles()[row - 1][col] != null)
					typeUp = tileMap.tiles()[row - 1][col].tileType;
				if (row + 1 < tileMap.row() && tileMap.tiles()[row + 1][col] != null)
					typeDown = tileMap.tiles()[row + 1][col].tileType;

				if (typeRight != null && typeRight == type) {
					sum += 1;
				}
				if (typeLeft != null && typeLeft == type) {
					sum += 10;
				}
				if (typeUp != null && typeUp == type) {
					sum += 100;
				}
				if (typeDown != null && typeDown == type) {
					sum += 1000;
				}
				
				int index = getIndexSpriteFrame(sum);
//				mapTexture[row][col] = tileTexture.get(type).get(index);
				animOgIndex[row][col] = index;
			}
		}
	}
	
	private int getIndexSpriteFrame(int sum) {
		int index = 0;
		switch (sum) {
		case 0:
			index = 0;
			break;
		case 1:
			index = 1;
			break;
		case 100:
			index = 2;
			break;
		case 101:
			index = 3;
			break;
		case 10:
			index = 4;
			break;
		case 11:
			index = 5;
			break;
		case 110:
			index = 6;
			break;
		case 111:
			index = 7;
			break;
		case 1000:
			index = 8;
			break;
		case 1001:
			index = 9;
			break;
		case 1100:
			index = 10;
			break;
		case 1101:
			index = 11;
			break;
		case 1010:
			index = 12;
			break;
		case 1011:
			index = 13;
			break;
		case 1110:
			index = 14;
			break;
		case 1111:
			index = 15;
			break;
		default:
			index = 0;
			break;
		}
		return index;
	}
}
