package fr.uge.adventure.ui;

import java.util.ArrayList;

import fr.uge.adventure.item.Item;
import fr.uge.adventure.main.Game;
import fr.uge.adventure.main.GameState;

public class UIManager {
	private final Game game;
	
	private boolean showText;
	
	private static int gridCol = 3;
	private static int gridRow = 4;
	private int xCursorInv = 0;
	private int yCursorInv = 0;
	
	private static int maxLineTextBox = 6;
	private String contentTextBox;
	private String name;
	
	private boolean chooseOption;
	private int cursorOption = 0;
	private ArrayList<String> option;
	
	private boolean textBox;
	
	private Item item1 = null;
	private Item item2 = null;
	
	public UIManager(Game game) {
		this.game = game;
	}
	
	public void update() {	
			uiInventoryControl();
			uiTextBoxControl();
	}
	
	public void uiInventoryControl() {
		if (game.gameState() == GameState.inventoryScr) {
			if (game.input().rightTouch) {
				xCursorInv = Math.min(gridCol - 1, xCursorInv + 1);
				game.input().rightTouch = false;
			}
			else if (game.input().leftTouch) {
				xCursorInv = Math.max(0, xCursorInv - 1);
				game.input().leftTouch = false;
			}
			else if (game.input().upTouch) {
				yCursorInv = Math.max(0, yCursorInv - 1);
				game.input().upTouch = false;
			}
			else if (game.input().downTouch) {
				yCursorInv = Math.min(gridRow - 1, yCursorInv + 1);
				game.input().downTouch = false;
			}
			
			game.player().useItem();
		}
		else if (game.gameState() == GameState.tradingScr) {
			if (game.input().rightTouch) {
				game.input().rightTouch = false;
				xCursorInv = Math.min(gridCol - 1, xCursorInv + 1);
			}
			else if (game.input().leftTouch) {
				game.input().leftTouch = false;
				xCursorInv = Math.max(0, xCursorInv - 1);
			}
			else if (game.input().upTouch) {
				game.input().upTouch = false;
				yCursorInv = Math.max(0, yCursorInv - 1);
			}
			else if (game.input().downTouch) {
				game.input().downTouch = false;
				yCursorInv = Math.min(gridRow - 1, yCursorInv + 1);
			}
			else if (game.input().spaceTouch) {
				game.input().spaceTouch = false;
				if (item1 == null) {
					int index = yCursorInv * gridCol + xCursorInv;
					if (index < game.player().trader().inventory().size())	
						item1 = game.player().trader().inventory().get(index);
				}
				else if (item2 == null) {
					int index = yCursorInv * gridCol + xCursorInv;
					if (index < game.player().inventory().size())	
						item2 = game.player().inventory().get(index);
				}
				else {
					chooseOption = false;
					game.setGameState(GameState.running);
					game.player().trader().trade();
				}
					
			}
		}
		else if (game.gameState() == GameState.running) {
			xCursorInv = 0;
			yCursorInv = 0;
			item1 = null;
			item2 = null;
			option = null;
		}
	}
	
	public void uiTextBoxControl() {
		if (game.gameState() == GameState.dialogueScr) {
			if (!chooseOption && game.input().spaceTouch) {
				if (game.renderer().ui().textCursor() + maxLineTextBox < game.renderer().ui().contentAdapt().size() - 2)
					game.renderer().ui().setTextCursor(game.renderer().ui().textCursor() + maxLineTextBox);
				else {
					game.renderer().ui().setTextCursor(0);
					game.setGameState(GameState.running);
				}
				game.input().spaceTouch = false;
			}
			else if (chooseOption) {
				if (game.input().downPressed)
					cursorOption = Math.max(cursorOption, option.size() - 1);
				if (game.input().upPressed)
					cursorOption = Math.min(cursorOption, 0);
				if (game.input().spaceTouch) {
					game.input().spaceTouch = false;
					if (option.get(cursorOption).equals("no"))
						game.setGameState(GameState.running);
					else
						game.setGameState(GameState.tradingScr);
					chooseOption = false;
					option = null;
					cursorOption = 0;
				}
			}
		} 
	}
	
	public boolean chooseOption() {
		return this.chooseOption;
	}
	
	public void setChooseOption(boolean option) {
		this.chooseOption = option;
	}
	
	public int cursorOption() {
		return this.cursorOption;
	}
	
	public ArrayList<String> option() {
		return this.option;
	}
	
	public void setOption(ArrayList<String> option) {
		this.option = option;
	}
	
	public void textBox(String content) {
		
	}
	
	public int xCursorInv() {
		return this.xCursorInv;
	}
	
	public int yCursorInv() {
		return this.yCursorInv;
	}

	public boolean textBox() {
		return textBox;
	}

	public void setTextBox(boolean textBox) {
		this.textBox = textBox;
	}

	public String contentTextBox() {
		return contentTextBox;
	}

	public void setContentTextBox(String contentTextBox) {
		this.contentTextBox = contentTextBox;
	}

	public boolean showText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Item item1() {
		return item1;
	}

	public void setItem1(Item item1) {
		this.item1 = item1;
	}

	public Item item2() {
		return item2;
	}

	public void setItem2(Item item2) {
		this.item2 = item2;
	}
}
