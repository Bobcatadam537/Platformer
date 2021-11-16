package platformer;

import java.awt.Color;
import java.awt.Graphics;

class Enemy extends GameObject {
	boolean hasAmmo = false;
	int cooldown = 30;
	boolean direction = false; // false is left, true is right
	float lx, ly, lvx, lw, lh;
	boolean alive = true;
	final float X, Y;

	public Enemy(int ex, int why, int wid, int hid) {
		super(ex, why, wid, hid);
		lvx = 0;
		lx = x;
		lw = 20;
		lh = 10;
		ly = y;
		health = 30;
		X = x;
		Y = y;
	}

	public int damageE(int val) {
		health -= val;
		return health;
	}

	public void render(Graphics g) {
		ly = y + 25;
		if (!direction) {
			if (cooldown < 50) {
				// g.setColor(Color.green);
				g.drawImage(Game.alien, (int) x, (int) y, (int) 50, (int) 100, null);
			} else {
				g.drawImage(Game.alienShoot, (int) x, (int) y, (int) 50, (int) 100, null);
			}
			if (lvx != 0) {
				g.setColor(Color.red);
				g.drawImage(Game.lazerPic, (int) lx, (int) ly, (int) lw * 2, (int) lh * 4, null);
			} else {
				lx = x;
			}
		} else {
			if (cooldown < 50) {
				// g.setColor(Color.green);
				g.drawImage(Game.alienR, (int) x, (int) y, (int) 50, (int) 100, null);
			} else {
				g.drawImage(Game.alienShootR, (int) x, (int) y, (int) 50, (int) 100, null);
			}
			if (lvx != 0) {
				g.setColor(Color.red);
				g.drawImage(Game.lazerPic, (int) lx, (int) ly, (int) lw * 2, (int) lh * 4, null);
			} else {
				lx = x;
			}
		}
		tick();
	}

	public boolean rightOfPlayer(Player p) {
		if (this.x > p.x)
			return true;
		return false;

	}

	public void attack(Player p) {
		if (hasAmmo) {
			fire();
			hasAmmo = false;
			cooldown = 120;
		} else {
			if (cooldown == 0) {
				hasAmmo = true;
			} else {
				cooldown--;
			}
		}
		if (p.x < x + w / 2) {
			direction = false;
		} else {
			direction = true;
		}
	}

	public void fire() {
		if (!direction) {
			lvx = -5;
			lx = x - lw;
		} else {
			lvx = 5;
			lx = x + w;
		}
	}

	public void respawn() {
		if (alive = false) {
			health = 40;
			alive = true;
			// x = X;
			// y = Y;
		}
	}

	public void shiftX(float move) {
		x += move;
		lx += move;
	}

	public void shiftY(float move) {
		y += move;
		ly += move;
	}

	public void damage(int val) {
		health -= val;
	}

	public boolean onScreen() {
		if (((x > 0 && x < Game.WIDTH) || (x + w > 0 && x + w < Game.WIDTH))
				&& ((y > 0 && y < Game.HEIGHT) || (y + h > 0 && y + h < Game.HEIGHT))) {
			return true;
		}
		return false;
	}
}