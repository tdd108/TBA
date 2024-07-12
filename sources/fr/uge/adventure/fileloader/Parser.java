package fr.uge.adventure.fileloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.uge.adventure.gamedata.ElementData;
import fr.uge.adventure.gamedata.EnemyData;
import fr.uge.adventure.gamedata.FriendData;
import fr.uge.adventure.gamedata.GameData;
import fr.uge.adventure.gamedata.ItemData;
import fr.uge.adventure.gamedata.MapData;
import fr.uge.adventure.gamedata.ObjectData;
import fr.uge.adventure.gamedata.PlayerData;
import fr.uge.adventure.gamedata.Position;
import fr.uge.adventure.gamedata.Size;
import fr.uge.adventure.gamedata.Zone;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.tile.TileType;

public class Parser {
	private final Game game;
	private final Lexer lexer;
	private int lineno;
	
	private MapData mapData;
	private PlayerData playerData;
	private ArrayList<EnemyData> lstEnemyData;
	private ArrayList<ItemData> lstItemData;
	private ArrayList<ObjectData> lstObjData;
	private ArrayList<FriendData> lstFriendData;

	public Parser(String mapName, Game game) throws IOException {
		var path = Path.of("maps", mapName + ".map");
		var text = Files.readString(path);
		this.lexer = new Lexer(text);
		this.lineno = 1;
		this.game = game;
		this.lstEnemyData = new ArrayList<EnemyData>();
		this.lstItemData = new ArrayList<ItemData>();
		this.lstObjData = new ArrayList<ObjectData>();
		this.lstFriendData = new ArrayList<FriendData>();
	}

	private Result next(ArrayList<Result> tokens) {
		if (!tokens.isEmpty()) {
			Result token = tokens.getFirst();
			tokens.removeFirst();
			return token;
		}
		return null;
	}

	private Result peek(ArrayList<Result> tokens) {
		if (!tokens.isEmpty())
			return tokens.getFirst();
		return null;
	}

	private boolean skip(ArrayList<Result> tokens, Token token) {
		if (peek(tokens).token() != token)
			return false;
		next(tokens);
		return true;
	}

	private void skipTo(ArrayList<Result> tokens, Token tokenToSkipTo) {
		if (peek(tokens) == null)
			return;
		if (peek(tokens).token() == tokenToSkipTo)
			return;
		Result tok;
		do {
			tok = next(tokens);
			if (tok == null)
				break;
		} while (!tok.isToken(tokenToSkipTo));
		lineno++;
	}

	private void undefinedErrorHandler(Result unExpectedResult) {
		System.out.println(
				"Undefined \"" + unExpectedResult.token() + " " + unExpectedResult.content() + "\" at line: " + lineno);
	}

	private void syntaxErrorHandler(Token expectedToken) {
		System.out.println("Syntax error, expected \"" + expectedToken + "\" at line " + lineno);
	}

	public GameData parse() throws IOException {
		ArrayList<Result> tokens = new ArrayList<Result>();

		Result result;

		while ((result = lexer.nextResult()) != null) {
			tokens.add(result);
		}

		while (tokens.size() > 0) {
			parseSection(tokens);
		}
		
		return new GameData(mapData, playerData, lstEnemyData, lstItemData, lstObjData, lstFriendData);
	}

	private void parseSection(ArrayList<Result> tokens) {
		String sectionType = "unknown";
		
		Result pointerToken = next(tokens);

		if (pointerToken.isToken(Token.NEWLINE)) {
			lineno++;
			return;
		}

		if (!pointerToken.isToken(Token.LEFT_BRACKET)) {
			return;
		}

		if ((pointerToken = next(tokens)) != null && !pointerToken.isToken(Token.IDENTIFIER))
			return;

		sectionType = pointerToken.content();

		if (!skip(tokens, Token.RIGHT_BRACKET)) {
			syntaxErrorHandler(Token.RIGHT_BRACKET);
			return;
		}

		switch (sectionType) {
		case "grid":
			mapData = parseMap(tokens);
			break;

		case "element":
			ElementData eleData = parseElement(tokens);
			if (eleData == null)
				break;
			switch (eleData.type()) {
			case Player:
				playerData = (PlayerData) eleData;
				break;
			case Enemy:
				lstEnemyData.add((EnemyData) eleData);
				break;
			case Item:
				lstItemData.add((ItemData) eleData);
				break;
			case Object:
				lstObjData.add((ObjectData) eleData);
				break;
			case Friend:
				lstFriendData.add((FriendData) eleData);
			default:
				break;
			}
			break;

		default:
			System.out.println("Undefined \"" + pointerToken.content() + "\"");
			break;
		}
	}

	private MapData parseMap(ArrayList<Result> tokens) {
		Result pointerToken;
		Size sizeData = null;
		var encodingsData = new HashMap<String, TileType>();
		String[] data = null;

		while (peek(tokens) != null && !peek(tokens).isToken(Token.LEFT_BRACKET)) {
			pointerToken = next(tokens);

			if (pointerToken.isToken(Token.NEWLINE)) {
				lineno++;
				continue;
			}

			if (!pointerToken.isToken(Token.IDENTIFIER)) {
				syntaxErrorHandler(Token.IDENTIFIER);
				continue;
			}

			switch (pointerToken.content()) {
			case "size":
				sizeData = parseSize(tokens);
				break;
			case "encodings":
				encodingsData = parseEncodings(tokens);
				break;
			case "data":
				data = parseData(tokens);
				break;
			default:
				undefinedErrorHandler(pointerToken);
				break;
			}
		}

		if (sizeData == null) {
			System.out.println("Missing size data");
			return null;
		}
		if (encodingsData == null) {
			System.out.println("Missing encodings data");
			return null;
		}
		if (data == null) {
			System.out.println("Missing map data");
			return null;
		}

		return new MapData(sizeData, encodingsData, data);
	}

	private Size parseSize(ArrayList<Result> tokens) {
		Result pointerToken;
		int col, row;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (!skip(tokens, Token.LEFT_PARENS)) {
			syntaxErrorHandler(Token.LEFT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		col = Integer.parseInt(pointerToken.content());

		if (!peek(tokens).content().equals("x") || !skip(tokens, Token.IDENTIFIER)) {
			syntaxErrorHandler(Token.IDENTIFIER);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		row = Integer.parseInt(pointerToken.content());

		if (!skip(tokens, Token.RIGHT_PARENS)) {
			syntaxErrorHandler(Token.RIGHT_PARENS);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}

		return new Size(col, row);
	}

	private HashMap<String, TileType> parseEncodings(ArrayList<Result> tokens) {
		Result pointerToken;
		String encodingChar;
		HashMap<String, TileType> encodingsData = new HashMap<String, TileType>();
		TileType tile;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		// stop when there is a new section or a new data field of map ("[something]" or
		// "something :")
		while (!((tokens.get(0).content().equals("data") || tokens.get(0).content().equals("size")
				|| tokens.get(0).isToken(Token.IDENTIFIER) && tokens.get(0).isToken(Token.LEFT_BRACKET)
				|| tokens.get(0).isToken(Token.LEFT_BRACKET)))) {

			if ((pointerToken = next(tokens)) != null && pointerToken.isToken(Token.NEWLINE)) {
				lineno++;
				continue;
			}

			if (!pointerToken.isToken(Token.IDENTIFIER)) {
				syntaxErrorHandler(Token.IDENTIFIER);
				continue;
			}

			try {
				tile = TileType.valueOf(pointerToken.content());
			} catch (IllegalArgumentException e) {
				System.out.println("Unknown encoding : \"" + pointerToken.content() + "\" at line " + lineno);
				continue;
			}

			if (!skip(tokens, Token.LEFT_PARENS)) { // check left parens
				syntaxErrorHandler(Token.LEFT_PARENS);
				continue;
			}

			if ((pointerToken = next(tokens)) != null && !pointerToken.isToken(Token.IDENTIFIER)) {
				syntaxErrorHandler(Token.IDENTIFIER);
				continue;
			}

			encodingChar = pointerToken.content();
			if (encodingChar.length() > 1) {
				System.out.println("Syntax error, encoding is not a character at line " + lineno);
				skipTo(tokens, Token.RIGHT_PARENS);
				continue;
			}

			if (!peek(tokens).isToken(Token.RIGHT_PARENS)) { // check right parens
				syntaxErrorHandler(Token.RIGHT_PARENS);
				continue;
			}

			tokens.removeFirst();
			encodingsData.put(encodingChar, tile);
		}

		return encodingsData;
	}

	private String[] parseData(ArrayList<Result> tokens) {
		Result pointerToken;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			if (peek(tokens).token() == Token.QUOTE) {
				pointerToken = next(tokens);
				String[] map = Arrays.stream(pointerToken.content().strip().split("\n"))
						.filter(str -> !str.contains("\"\"\"")).map(String::strip).toArray(String[]::new);
				lineno += map.length;
			} else {
				while (!peek(tokens).content().equals("size") && !peek(tokens).content().equals("encodings")
						&& peek(tokens).token() != Token.LEFT_BRACKET) {
					if (peek(tokens).token() == Token.NEWLINE)
						lineno++;
					next(tokens);
				}
			}
			return null;
		}

		if (peek(tokens).token() != Token.QUOTE) {
			syntaxErrorHandler(Token.QUOTE);
			while (!peek(tokens).content().equals("size") && !peek(tokens).content().equals("encodings")
					&& peek(tokens).token() != Token.LEFT_BRACKET) {
				if (peek(tokens).token() == Token.NEWLINE)
					lineno++;
				next(tokens);
			}
			return null;
		}

		pointerToken = next(tokens);

		String[] map = Arrays.stream(pointerToken.content().strip().split("\n")).filter(str -> !str.contains("\"\"\""))
				.map(String::strip).toArray(String[]::new);

		lineno += map.length + 1; // + 1 because already strip to line of """

		return map;
	}

	private ElementData parseElement(ArrayList<Result> tokens) {
		Result pointerToken;

		HashMap<String, String> stringData = new HashMap<String, String>();
		HashMap<String, Integer> intData = new HashMap<String, Integer>();
		Position position = null; Zone zone = null;
		HashMap<String, ArrayList<ItemData>>  lstTrade = new HashMap<String, ArrayList<ItemData>>(); 
		

		while (peek(tokens) != null && !peek(tokens).isToken(Token.LEFT_BRACKET)) {
			pointerToken = next(tokens);

			if (pointerToken != null && pointerToken.isToken(Token.NEWLINE)) {
				lineno++;
				continue;
			}

			if (!pointerToken.isToken(Token.IDENTIFIER)) {
				syntaxErrorHandler(Token.IDENTIFIER);
				skipTo(tokens, Token.NEWLINE);
				continue;
			}

			switch (pointerToken.content()) {
			case "player":
			case "name":
			case "skin":
			case "behavior":
			case "kind":
			case "color":
			case "locked":
			case "flow":
				stringData.put(pointerToken.content(), parseAttributeString(pointerToken.content(), tokens));
				break;
			
			case "trade":
				lstTrade = parseTrade(tokens);
				if (lstTrade != null)
					for (var lsItemData : lstTrade.values()) {
						for (var itemData : lsItemData) {
							lstItemData.add(itemData);
						}
					}
				break;

			case "health":
			case "damage":
				intData.put(pointerToken.content(), parseAttributeNumber(pointerToken.content(), tokens));
				break;

			case "position":
				position = parsePosition(tokens);
				break;

			case "zone":
				zone = parseZone(tokens);
				break;

			default:
				undefinedErrorHandler(pointerToken);
				skipTo(tokens, Token.NEWLINE);
				break;
			}
		}
		return createElement(lstTrade, stringData, intData, position, zone);
	}

	private String parseAttributeString(String attribute, ArrayList<Result> tokens) {
		String nameData;
		Result pointerToken;

		HashMap<String, List<String>> allowedAttribute = new HashMap<String, List<String>>(
				Map.of("kind", List.of("friend", "enemy", "item", "obstacle"), 
					   "behavior", List.of("shy", "stroll", "agressive"), 
					   "player", List.of("true", "false"), 
					   "locked", List.of("KEY", "LEVER")
						));

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.IDENTIFIER) {
			syntaxErrorHandler(Token.IDENTIFIER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		nameData = pointerToken.content();

		if (allowedAttribute.containsKey(attribute) && !allowedAttribute.get(attribute).contains(nameData)) {
			undefinedErrorHandler(pointerToken);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}
		
		if (attribute.equals("locked")) {
			if (peek(tokens).token() != Token.IDENTIFIER) {
				syntaxErrorHandler(Token.IDENTIFIER);
				skipTo(tokens, Token.NEWLINE);
				return null;
			}
			pointerToken = next(tokens);
			nameData += " " + pointerToken.content();
		}

		return nameData;
	}
	
	private HashMap<String, ArrayList<ItemData>> parseTrade(ArrayList<Result> tokens) {
		Result pointerToken;
		HashMap<String, ArrayList<ItemData>> lstTrade = new HashMap<String, ArrayList<ItemData>>();
		String firstItem, secondItem, secondItemName;
		
		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}
		
		while (peek(tokens) != null && !peek(tokens).isToken(Token.NEWLINE)) {
			
			firstItem = ""; 
			secondItem = "";
			secondItemName = "";
			ArrayList<Map<String, String>> map = new ArrayList<Map<String, String>>();
			
			if (peek(tokens).token() != Token.IDENTIFIER) {
				syntaxErrorHandler(Token.IDENTIFIER);
				skipTo(tokens, Token.NEWLINE);
				return null;
			}
			
			pointerToken = next(tokens);
			firstItem = pointerToken.content();

			if (peek(tokens).token() != Token.IDENTIFIER) {
				syntaxErrorHandler(Token.IDENTIFIER);
				skipTo(tokens, Token.NEWLINE);
				return null;
			}
			
			pointerToken = next(tokens);
			secondItem = pointerToken.content();
			
			if (peek(tokens) == null || peek(tokens).isToken(Token.NEWLINE) || peek(tokens).isToken(Token.COMMA)) {
				if (!lstTrade.containsKey(firstItem)) {
					var lstItemData = new ArrayList<ItemData>();
					lstItemData.add(new ItemData(secondItemName, secondItem));
					lstTrade.put(firstItem, lstItemData);
				}
				else {
					lstTrade.get(firstItem).add(new ItemData(secondItemName, secondItem));
				}
			}
			
			else {
				
				if (peek(tokens).token() != Token.IDENTIFIER) {
					syntaxErrorHandler(Token.IDENTIFIER);
					skipTo(tokens, Token.NEWLINE);
					return null;
				}
				pointerToken = next(tokens);
				secondItemName = pointerToken.content();
				
				if (!lstTrade.containsKey(firstItem)) {
					var lstItemData = new ArrayList<ItemData>();
					lstItemData.add(new ItemData(secondItemName, secondItem));
					lstTrade.put(firstItem, lstItemData);
				}
				else {
					lstTrade.get(firstItem).add(new ItemData(secondItemName, secondItem));
				}
				
				if (peek(tokens) == null || peek(tokens).isToken(Token.NEWLINE)) {
					next(tokens);
					break;
				}
				
				if (!skip(tokens, Token.COMMA)) {
					syntaxErrorHandler(Token.COMMA);
					skipTo(tokens, Token.NEWLINE);
					return null;
				}
			}
		}
		
		return lstTrade;
	}

	private Integer parseAttributeNumber(String attribute, ArrayList<Result> tokens) {
		Result pointerToken;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if ((pointerToken = next(tokens)) != null && !pointerToken.isToken(Token.NUMBER)) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return 0;
		}

		return Integer.parseInt(pointerToken.content());
	}

	private Position parsePosition(ArrayList<Result> tokens) {
		Result pointerToken;
		int x, y;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (!skip(tokens, Token.LEFT_PARENS)) {
			syntaxErrorHandler(Token.LEFT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		x = Integer.parseInt(pointerToken.content());

		if (!peek(tokens).content().equals(",") || !skip(tokens, Token.COMMA)) {
			syntaxErrorHandler(Token.IDENTIFIER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		y = Integer.parseInt(pointerToken.content());

		if (!skip(tokens, Token.RIGHT_PARENS)) {
			syntaxErrorHandler(Token.RIGHT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		return new Position(x, y);
	}

	private Zone parseZone(ArrayList<Result> tokens) {
		Result pointerToken;
		int x, y, col, row;

		if (!skip(tokens, Token.COLON)) {
			syntaxErrorHandler(Token.COLON);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (!skip(tokens, Token.LEFT_PARENS)) {
			syntaxErrorHandler(Token.LEFT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		x = Integer.parseInt(pointerToken.content());

		if (!peek(tokens).content().equals(",") || !skip(tokens, Token.COMMA)) {
			syntaxErrorHandler(Token.IDENTIFIER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		y = Integer.parseInt(pointerToken.content());

		if (!skip(tokens, Token.RIGHT_PARENS)) {
			syntaxErrorHandler(Token.RIGHT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (!skip(tokens, Token.LEFT_PARENS)) {
			syntaxErrorHandler(Token.LEFT_PARENS);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		col = Integer.parseInt(pointerToken.content());

		if (!peek(tokens).content().equals("x") || !skip(tokens, Token.IDENTIFIER)) {
			syntaxErrorHandler(Token.IDENTIFIER);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}

		if (peek(tokens).token() != Token.NUMBER) {
			syntaxErrorHandler(Token.NUMBER);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}

		pointerToken = next(tokens);
		row = Integer.parseInt(pointerToken.content());

		if (!skip(tokens, Token.RIGHT_PARENS)) {
			syntaxErrorHandler(Token.RIGHT_PARENS);
			if (peek(tokens).token() != Token.NEWLINE)
				skipTo(tokens, Token.NEWLINE);
			return null;
		}
		
		return new Zone(x, y, col, row);
	}

	private ElementData createElement(HashMap<String, ArrayList<ItemData>>  lstTrade, HashMap<String, String> stringData, HashMap<String, Integer> intData,
			Position position, Zone zone) {
		String player = stringData.get("player"), skin = stringData.get("skin"), name = stringData.get("name"),
				behavior = stringData.get("behavior"), kind = stringData.get("kind");
		int health = 0, damage = 0;
		if (intData.containsKey("health"))
			health = intData.get("health");
		if (intData.containsKey("damage"))
			damage = intData.get("damage");

		if (player != null && player.equals("true")) {
			return new PlayerData(name, skin, position, health);
		}
		else if (kind != null) {
			switch(kind) {
			case "enemy":
				return new EnemyData(name, skin, position, zone, damage, behavior);
			case "friend":
				return new FriendData(name, skin, position, zone, damage, behavior, lstTrade);
			case "item":
				return new ItemData(name, skin, position, stringData, intData);
			case "obstacle":
				return new ObjectData(name, skin, position, stringData, intData);
			}
			
		}

		return null;
	}
}
