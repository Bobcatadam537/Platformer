package platformer;

import java.awt.Graphics;
import java.awt.Image;

class Room extends GameObject {
	Image image;

	public Room(int X, int Y, int W, int H, Image i) {
		super(X, Y, W, H);
		image = i;
	}

	public void render(Graphics g) {
		g.drawImage(image, (int) x, (int) y, (int) w, (int) h, null);
	}
}