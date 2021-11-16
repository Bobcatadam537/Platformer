package platformer;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

class Window extends Canvas {
	public Window(int w, int h, String t, Game game) {
		JFrame frame = new JFrame(t);

		frame.setPreferredSize(new Dimension(w + 16, h + 39));
		frame.setMinimumSize(new Dimension(w + 16, h + 39));
		frame.setMaximumSize(new Dimension(w + 16, h + 39));

		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		frame.setVisible(true);
		game.start();
	}
}