package fr.uge.adventure.ulti;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utilities {
	public static BufferedImage loadImage(String path, String name) {
		BufferedImage image = null;
		try (var input = Utilities.class.getResourceAsStream(path + name)) {
			if (input != null)
				image = ImageIO.read(input);
		}
		catch (IOException e) {
			System.out.println("file not exist: " + name);
			e.printStackTrace();
		}
		return image;
	}
	
	public static BufferedImage getSpriteFrame(BufferedImage sprite, double ogSprSize, int col, int row) {
		BufferedImage subImage = sprite.getSubimage((int) (col * ogSprSize), (int) (row * ogSprSize), (int) ogSprSize, (int) ogSprSize);
		return subImage;
	}
	
	public static BufferedImage scaleImage(BufferedImage img, double scale) {
		BufferedImage scaledImage = new BufferedImage((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), img.getType());
		Graphics2D g2 = scaledImage.createGraphics();
		g2.drawImage(img, 0, 0, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
		g2.dispose();
		return scaledImage;
	}
	
	public static BufferedImage rotateImage(BufferedImage img, double angle) {
		BufferedImage rotatedImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		double rotationAngle = Math.toRadians(angle);
		Graphics2D g2 = rotatedImage.createGraphics();
		AffineTransform transform = new AffineTransform();
		transform.rotate(rotationAngle, img.getWidth() / 2, img.getHeight() / 2);
		g2.setTransform(transform);
		g2.drawImage(img, 0, 0, null);
		g2.dispose();
		return rotatedImage; 
	}
	
	public static Font loadFont(String path, String name) {
		Font customFont = null;
		try (var input = Utilities.class.getResourceAsStream(path + name)) {
			if (input != null)
				try {
					customFont = Font.createFont(Font.TRUETYPE_FONT, input);
				} catch (FontFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		catch (IOException e) {
			System.out.println("file not exist: " + name);
			e.printStackTrace();
		}
		return customFont;
	}
	
	public static int sign(int number) {
		return number < 0 ? -1 : 1;
	}
}
