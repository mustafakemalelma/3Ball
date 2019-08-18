package main;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Utils {
	
	public static Color getRandomColor() {
		Random rnd = new Random();
		int r = rnd.nextInt(256);
		int g = rnd.nextInt(256);
		int b = rnd.nextInt(256);
		
		return new Color(r,g,b);
	}
	

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	
	public static JLabel getImageLabel(String path) {
		JLabel lbl = new JLabel();
		
		BufferedImage image = null;
		try {
			 image = ImageIO.read(Utils.loadResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lbl.setIcon(new ImageIcon(image));
		
		return lbl;
	}
	
	public static JLabel getImageLabel(String path, int w, int h) {
		JLabel lbl = new JLabel();
		BufferedImage image = null;
		try {
			 image = ImageIO.read(Utils.loadResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ImageIcon oldImg = new ImageIcon(image);
		Image scaledImg = oldImg.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
		
		lbl.setIcon(new ImageIcon(scaledImg));
		
		return lbl;
	}
	
	public static InputStream loadResource(String path) {
		InputStream resource = Utils.class.getResourceAsStream(path);
		if(resource == null) {
			resource = Utils.class.getResourceAsStream("/" + path);
		}
		
		
		return resource;
	}

}
