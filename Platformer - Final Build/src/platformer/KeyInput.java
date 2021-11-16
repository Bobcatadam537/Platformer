package platformer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

class KeyInput extends KeyAdapter {
	private ArrayList<GameObject> movable;
	private GameObject player;

	public KeyInput(ArrayList<GameObject> m) {
		movable = m;
		player = movable.get(0);
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (player.pause) {
			return;
		}

		if (key == KeyEvent.VK_LEFT)
			player.left = true;
		if (key == KeyEvent.VK_RIGHT)
			player.right = true;
		if (key == KeyEvent.VK_UP)
			player.jump();
		if (key == KeyEvent.VK_SPACE) {
			// System.out.println("shoot");
			player.attack();
			player.space = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_LEFT)
			player.left = false;
		if (key == KeyEvent.VK_RIGHT)
			player.right = false;
		if (key == KeyEvent.VK_SPACE)
			player.space = false;
	}
}