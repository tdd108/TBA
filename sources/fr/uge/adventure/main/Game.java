package fr.uge.adventure.main;

import java.io.IOException;
import java.util.ArrayList;

import fr.uge.adventure.camera.Camera;
import fr.uge.adventure.collision.CollisionChecker;
import fr.uge.adventure.entity.Enemy;
import fr.uge.adventure.entity.EnemyManager;
import fr.uge.adventure.entity.Friend;
import fr.uge.adventure.entity.FriendManager;
import fr.uge.adventure.entity.Player;
import fr.uge.adventure.fileloader.Parser;
import fr.uge.adventure.gamedata.GameData;
import fr.uge.adventure.input.InputHandler;
import fr.uge.adventure.item.Item;
import fr.uge.adventure.item.ItemManager;
import fr.uge.adventure.object.GameObject;
import fr.uge.adventure.object.ObjectManager;
import fr.uge.adventure.renderer.GameRenderer;
import fr.uge.adventure.tile.TileManager;
import fr.uge.adventure.tile.TileMap;
import fr.uge.adventure.ui.UIManager;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;

public class Game {
	private static double ogSprSize = 25;
	private final double tileSize;
	//FPS
	private final int fps = 120; //set a little more than 60 because the method
								//to calculate the waiting time is not 100% accurate

	//SCREEN SET UP
	private final int maxScrCol = 32; //32
	private final int maxScrRow = 18; //18
	private final double scrWidth;
	private final double scrHeight;
	private final ApplicationContext context;

	private String mapName;
	private GameData data;

	private final Player player;
	private TileMap tileMap;
	private ArrayList<Enemy> lstEnemy;
	private ArrayList<Friend> lstFriend;
	private ArrayList<Item> lstItem;
	private ArrayList<GameObject> lstObject;

	private final TileManager tileMng;
	private final EnemyManager enemyMng;
	private final ItemManager itemMng;
	private final ObjectManager objMng;
	private final FriendManager frMng;

	private final Camera cam;

	private final InputHandler input;
	private final CollisionChecker coliCheck;
	private final UIManager uiMng;

	private final GameRenderer renderer;
	
	private GameState gameState;

	//DEBUG
	private int drawCount = 0;
	private long lastTime = System.nanoTime();
	private long currentTime;
	private long timerFps = 0;

	public Game(ApplicationContext context, String mapName) throws IOException {
		this.gameState = GameState.running;
		this.context = context;
		this.scrHeight = context.getScreenInfo().getHeight();
		this.scrWidth = context.getScreenInfo().getWidth();
		this.mapName = mapName;
		this.tileSize =  ogSprSize * scaleCalc();
		this.data = loadGameData();

		this.player = new Player(this);
		this.tileMap = new TileMap(data.map());
		this.lstEnemy = new ArrayList<Enemy>();
		this.lstFriend = new ArrayList<Friend>();
		this.lstItem = new ArrayList<Item>();
		this.lstObject = new ArrayList<GameObject>();

		this.input = new InputHandler(this);
		this.coliCheck = new CollisionChecker(this);
		this.uiMng = new UIManager(this);

		this.tileMng = new TileManager(this);
		this.enemyMng = new EnemyManager(this);
		this.itemMng = new ItemManager(this);
		this.objMng = new ObjectManager(this);
		this.frMng = new FriendManager(this);

		this.cam = new Camera(player, scrWidth, scrHeight, this);
		this.renderer = new GameRenderer(this);
	}
	
	public void reload(String mapName) throws IOException {
		this.mapName = mapName;
		this.data = loadGameData();
		this.tileMap = new TileMap(data.map());
		this.lstEnemy = new ArrayList<Enemy>();
		this.lstFriend = new ArrayList<Friend>();
		this.lstItem = new ArrayList<Item>();
		this.lstObject = new ArrayList<GameObject>();
	}

	public void update() {
		//check input
		Event event = context.pollOrWaitEvent(1000 / fps);
		input.eventType(event);
		if (input.exitPressed) {
			context.exit(0);
			return;
		}

		uiMng.update();

		if (gameState != GameState.running)
			return;

		//update events
		player.update();
		cam.update();
		enemyMng.update();
		tileMng.update();
		itemMng.update();
		objMng.update();
		frMng.update();

		Item item;
		if ((item = player.pickUpItem())!= null) {
			itemMng.deleteItem(item);
		}

		//update animation
		renderer.update();

		//debug

		if (input.debug) {
			player.setHealth(player.health() - 1);
			input.debug = false;
		}
//		showFPS();
	}

	public void render() {
		renderer.render();
		drawCount++;
	}

	//DEBUG FUNCTIONS
	private void showFPS() {
		currentTime = System.nanoTime();
		timerFps += (currentTime - lastTime);
		lastTime = currentTime;
		if (timerFps >= 1000000000) { //equal to 1 second
			System.out.println("FPS : " + drawCount);
			drawCount = 0;
			timerFps = 0;
		}
	}

	public double scaleCalc() {
		double heightScale = scrHeight / (maxScrRow * ogSprSize);
		double widthScale = scrWidth / (maxScrCol * ogSprSize);
		return Math.max(heightScale, widthScale);
	}

	public GameData loadGameData() throws IOException {
		GameData data = new Parser(mapName, this).parse();
		return data;
	}

	public ApplicationContext context() {
		return this.context;
	}

	public UIManager uiMng() {
		return this.uiMng;
	}

	public TileManager tileManager() {
		return this.tileMng;
	}

	public GameData data() {
		return this.data;
	}

	public String mapName() {
		return this.mapName;
	}

	public double scrWidth() {
		return this.scrWidth;
	}

	public double scrHeight() {
		return this.scrHeight;
	}

	public int maxScrCol() {
		return this.maxScrCol;
	}

	public int maxScrRow() {
		return this.maxScrRow;
	}

	public GameRenderer renderer() {
		return this.renderer;
	}

	public Player player() {
		return player;
	}

	public InputHandler input() {
		return this.input;
	}

	public double tileSize() {
		return this.tileSize;
	}

	public TileMap tileMap() {
		return this.tileMap;
	}

	public CollisionChecker coliCheck() {
		return coliCheck;
	}

	public Camera camera() {
		return this.cam;
	}

	public ArrayList<Enemy> lstEnemy() {
		return lstEnemy;
	}

	public ArrayList<Item> lstItem() {
		return lstItem;
	}

	public ArrayList<GameObject> lstObject() {
		return lstObject;
	}

	public GameState gameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public ArrayList<Friend> lstFriend() {
		return lstFriend;
	}
}
