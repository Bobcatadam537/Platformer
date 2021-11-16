package platformer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Menu extends MouseAdapter {
	Game game;
	Font font;
	FontMetrics metrics;

	public Menu(Game game) {
		this.game = game;
		font = new Font("courier new", 1, 50);
	}

	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		if (mouseOver(mx, my, 100, 100, Game.WIDTH - 200, 200)) {
			if (game.state == GameState.GAME_OVER || game.state == GameState.WIN) {
				game.state = GameState.MENU;
				game.initialize();
			} else if (game.state == GameState.MENU)
				game.state = GameState.RUNNING;
		}

	}

	public void mouseReleased(MouseEvent e) {

	}

	public boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
		if (mx > x && mx < x + width)
			if (my > y && my < y + height)
				return true;
		return false;
	}

	public void render(Graphics g) {
		metrics = g.getFontMetrics(font);
		g.drawImage(Game.menuBack, 0, 0, Game.WIDTH + 50, Game.HEIGHT + 35, null);
		// g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT + 35);
		g.setFont(new Font("courier new", 1, 50));
		g.setColor(new Color(100, 100, 100, 100));
		// g.fillRect(100, 100, Game.WIDTH - 200, 200);
		g.setColor(Color.red);
		g.drawString("Play", Game.WIDTH / 2 - metrics.stringWidth("Play") / 2,
				Game.HEIGHT / 2 - metrics.getHeight() / 2);
	}

	public void renderDeath(Graphics g) {
		metrics = g.getFontMetrics(font);
		g.setFont(new Font("courier new", 1, 50));
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(100, 100, Game.WIDTH - 200, 200);
		g.setColor(Color.red);
		g.drawString("YOU DIED", Game.WIDTH / 2 - metrics.stringWidth("YOU DIED") / 2, 100);
		g.drawString("Menu", Game.WIDTH / 2 - metrics.stringWidth("Menu") / 2,
				Game.HEIGHT / 2 - metrics.getHeight() / 2);
	}

	public void renderWin(Graphics g) {
		metrics = g.getFontMetrics(font);
		g.setFont(new Font("courier new", 1, 50));
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(100, 100, Game.WIDTH - 200, 200);
		g.setColor(Color.red);
		g.drawString("YOU ESCAPED", Game.WIDTH / 2 - metrics.stringWidth("YOU ESCAPED") / 2, 100);
		g.drawString("Menu", Game.WIDTH / 2 - metrics.stringWidth("Menu") / 2,
				Game.HEIGHT / 2 - metrics.getHeight() / 2);
	}
}