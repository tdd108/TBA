package fr.uge.adventure.renderer;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.uge.adventure.camera.Camera;
import fr.uge.adventure.element.Element;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.main.GameState;

public class GameRenderer {
	private static double ogSprSize = 25;
	private final double scale;
	private final double tileSize;
	private final Game game;
	
	private final Texture texture;
	
	private final MapRenderer mapRenderer;
	private final PlayerRenderer pRenderer;
	private final EnemyRenderer eRenderer;
	private final ItemRenderer iRenderer; 
	private final ObjectRenderer oRenderer; 
	private final FriendRenderer fRenderer;
	private final UI ui;
	
	private final Camera cam;
	private BufferedImage bufferImage;
	private Graphics2D bufferGraphics;
	
	public GameRenderer(Game game) throws IOException {
		this.game = game;
		this.scale = scaleCalc();
		this.tileSize = ogSprSize * scale;
		bufferImage = new BufferedImage((int) game.scrWidth(), (int) game.scrHeight(), BufferedImage.TYPE_INT_RGB);
		bufferGraphics = bufferImage.createGraphics();
		
		this.texture = new Texture(game, scale);
		
		this.mapRenderer = new MapRenderer(game.tileMap(), this);
		this.pRenderer = new PlayerRenderer(game.player(), this);
		this.eRenderer = new EnemyRenderer(game.lstEnemy(), this);
		this.iRenderer = new ItemRenderer(game.lstItem(), this);
		this.oRenderer = new ObjectRenderer(game.lstObject(), this);
		this.fRenderer = new FriendRenderer(game.lstFriend(), this);
		this.ui = new UI(game, this);
		this.cam = game.camera();
	}
	
	public void update() {
		mapRenderer.update();
		pRenderer.update();
		eRenderer.update();
		iRenderer.update();
		oRenderer.update();
		fRenderer.update();
		ui.update();
	}
	
	public void render() {
		this.game.context().renderFrame(graphics -> {	
			
			//draw all the element to the bufferImage off screen
			clearScreen(bufferGraphics);
			mapRenderer.render(bufferGraphics);
			iRenderer.render(bufferGraphics);
			pRenderer.render(bufferGraphics);
			eRenderer.render(bufferGraphics);
			oRenderer.render(bufferGraphics);
			fRenderer.render(bufferGraphics);
//			game.player().hitBox().draw(bufferGraphics, cam.camX(), cam.camY());
			
			//UI
			if (game.gameState() == GameState.inventoryScr)
				ui.inventoryGrid(bufferGraphics);
			else if (game.gameState() == GameState.inventoryScr || game.gameState() == GameState.running) {
				ui.healthBar(bufferGraphics);
				ui.equipment(bufferGraphics);
				ui.weapon(bufferGraphics);
				ui.cash(bufferGraphics);
			}
			else if (game.gameState() == GameState.dialogueScr) {
				ui.textBox(bufferGraphics);
				ui.textBoxString(bufferGraphics, game.uiMng().name(), game.uiMng().contentTextBox());
				if (game.uiMng().chooseOption())
					ui.option(bufferGraphics, game.uiMng().name(), game.uiMng().option());
			}
			else if (game.gameState() == GameState.tradingScr) {
				ui.inventoryGridTrading(bufferGraphics);
			}
			
			//when all the elements are drawn, draw the buffer image
			graphics.drawImage(bufferImage, null, 0, 0);
		});
	}
	
	public double scaleCalc() {
		double heightScale = (double) this.game.scrHeight() / (double) (game.maxScrRow() * ogSprSize);
		double widthScale = (double) this.game.scrWidth() / (double) (game.maxScrCol() * ogSprSize);
		return Math.max(heightScale, widthScale);
	}
	
	public void clearScreen(Graphics2D graphics) {
		Rectangle2D rec = new Rectangle2D.Double(0, 0, game.scrWidth(), game.scrHeight());
		graphics.setColor(Color.BLACK);
		graphics.fill(rec);
	}
	
	public ItemRenderer iRenderer() {
		return this.iRenderer;
	}
	
	public UI ui() {
		return this.ui;
	}
	
	public Camera cam() {
		return this.cam;
	}
	
	public double scale() {
		return this.scale;
	}
	
	public double tileSize() {
		return this.tileSize;
	}
	
	public double ogSprSize() {
		return 25;
	}
	
	public Texture texture() {
		return this.texture;
	}
}
