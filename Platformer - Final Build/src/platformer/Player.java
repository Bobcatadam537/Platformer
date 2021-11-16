package platformer;

import java.awt.Graphics;
import java.awt.Image;

class Player extends GameObject {
	boolean canJump;
	int height;
	boolean facingEast = true;
	float lx, ly, lw, lh, lvx, lockY;
	int cooldown = 30;
	boolean hasAmmo = true;
	int timer = 0;
	boolean canShoot = false;
	int currentPic = 0;
	boolean walking = false;
	Image sprite;

	Player(int X, int Y, int W, int H) {
		super(X, Y, W, H);
		canJump = false;
		height = H;
		lvx = 0;
		lx = x;
		lw = 20;
		lh = 10;
		ly = y;
	}

	public void renderWalkCycle(Graphics g, Image[] arr, Image[] arrR) {
		if (invincible % 10 < 5) {
			if (canJump && walking) {
				sprite = !facingEast ? arr[currentPic] : arrR[currentPic];
				if (timer % 10 == 0) {
					timer = 0;
					currentPic++;
					if (currentPic > 3)
						currentPic = 0;
				}
			} else {
				sprite = !facingEast ? arr[0] : arrR[0];
				currentPic = 0;
			}

			if (facingEast)
				g.drawImage(sprite, (int) x, (int) y, (int) w + 10, (int) h, null);
			else
				g.drawImage(sprite, (int) x - 10, (int) y, (int) w + 10, (int) h, null);
		}
	}

	public void render(Graphics g) {
		timer++;
		if (canJump) {
			if (!space)
				renderWalkCycle(g, Game.playerWalkLeft, Game.playerWalkRight);
			else
				renderWalkCycle(g, Game.playerWalkLeftS, Game.playerWalkRightS);
		} else {
			if (!space && !facingEast) {
				g.drawImage(Game.playerJump[0], (int) x - 10, (int) y, (int) w + 10, (int) h, null);
			} else if (!space && facingEast) {
				g.drawImage(Game.playerJump[1], (int) x - 10, (int) y, (int) w + 10, (int) h, null);
			} else if (space && !facingEast) {
				g.drawImage(Game.playerJump[2], (int) x - 10, (int) y, (int) w + 10, (int) h, null);
			} else if (space && facingEast) {
				g.drawImage(Game.playerJump[3], (int) x - 10, (int) y, (int) w + 10, (int) h, null);
			}
		}
		if (lvx == 0) {
			lockY = y;
		}
		if (!hasAmmo && lx != x && lvx != 0) {
			// g.setColor(Color.red);
			g.drawImage(Game.lazerPic, (int) lx, (int) ly, (int) lw * 2, (int) lh * 4, null);

		} else {
			lx = x;
			ly = y;
		}

		tick();
	}

	public void attack() {
		if (x == lx && y == ly)
			hasAmmo = true;

		if (lx > Game.WIDTH || lx < 0) {
			lx = x;
			ly = y;
		}

		if (hasAmmo) {
			// lockY = y;
			lx = x + w;
			fire();
			hasAmmo = false;
			// cooldown = 30;
		}

	}

	public int locateDirectionE(Enemy e) {
		return (int) (x - e.x);
	}

	public void fire() {

		if (!facingEast) {
			lvx = -10;
			lx = x - lw;
		} else {
			lvx = 10;
			lx = x + w;
		}
	}

	public void jump() {
		if (canJump && vy <= 0) {
			vy = -Game.jumpPower;
			canJump = false;
		}
	}

	public void tick() {
		if (lx >= Game.WIDTH || lx <= 0) {
			lx = x;
			ly = y;
			lvx = 0;
		}
		if (canJump) {
			vxtra = 0;
		}
		vx = (left ? -5 : 0) + (right ? 5 : 0) + vxtra;
		if (invincible > 0)
			invincible--;
		if (vx < 0) {
			facingEast = false;
			walking = true;
		} else if (vx > 0) {
			facingEast = true;
			walking = true;
		} else {
			walking = false;
		}
	}

	public void respawn() {
		x = 10 * 20;
		y = 18 * 20;
		vx = 0;
		vy = 0;
		health = 100;
		invincible = 0;
		pause = false;
	}

	public int damage(int d) {
		if (invincible == 0) {
			health -= d;
			invincible = 100;
		}
		return health;
	}

	public int damage(int d, int i) {
		if (invincible == 0) {
			health -= d;
			invincible = i;
		}
		return health;
	}
}
