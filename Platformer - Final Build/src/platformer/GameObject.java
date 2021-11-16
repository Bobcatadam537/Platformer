package platformer;

import java.awt.Color;
import java.awt.Graphics;

class GameObject {
	float x, y, w, h, vx, vy, vxtra;
	boolean left, right, space;
	boolean pause = false;
	boolean facingEast, firing, hasAmmo;
	int health = 100;
	int invincible = 0;
	float lx, ly, lw, lh, lvx;
	boolean direction = false;

	public GameObject(int X, int Y, int W, int H) {
		x = X * 20;
		y = Y * 20;
		w = W * 20;
		h = H * 20;
		vx = vy = vxtra = 0;
		left = right = space = false;
	}

	public void render(Graphics g) {
		g.setColor(new Color(100, 172, 47));
		g.fillOval((int) x, (int) y, (int) w, (int) h);
		tick();
	}

	public void tick() {

	}

	public void attack() {
	}

	public void fire() {
	}

	public void jump() {
	}

	public void shiftX(float move) {
		x += move;
	}

	public void shiftY(float move) {
		y += move;
	}

	public boolean touches(GameObject other) {
		if (this.x < other.x + other.w && this.x + this.w > other.x && this.y < other.y + other.h
				&& this.y + this.h > other.y)
			return true;
		return false;
	}

	public boolean touchesLazer(Enemy other) {
		// if (x is inside body or x + w is inside body AND y is inside body or y + h is
		// inside body)
		if ((other.lx > this.x || other.lx + other.lw > this.x)
				&& (other.lx < this.x + this.w || other.lx + other.lw < this.x + this.w))
			if ((other.ly > this.y || other.ly + other.lh > this.y)
					&& (other.ly < this.y + this.h || other.ly + other.lh < this.y + this.h))
				return true;
		return false;
	}

	public boolean touchesLazer(Player other) {
		if (other.lx < this.x + this.w && other.lx + other.w > this.x && other.ly < this.y + this.h
				&& other.ly + other.h > this.y) {
			return true;
		}
		return false;
	}

	public int damage() {
		return health;
	}
}