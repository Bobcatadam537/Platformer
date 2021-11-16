package platformer;

import java.awt.Graphics;
import java.util.Random;

class Platform extends GameObject {
	platformType type;

	public Platform(int X, int Y, int W, int H) {
		super(X, Y, W, H);
		type = platformType.DEFAULT;
	}

	public Platform(int X, int Y, int W, int H, platformType t) {
		super(X, Y, W, H);
		type = t;
	}

	public void render(Graphics g) {
		switch (type) {
		case GHOSTLY:
			break;
		case KILL:
			for (int i = 0; i < w; i += 80) {
				if (x + i + 80 > 0 && x + i - 80 < Game.WIDTH && y + 80 > 0 && y - 80 < Game.HEIGHT)
					g.drawImage(Game.lava[(int) (System.currentTimeMillis() / 300 % 4)], (int) x + i, (int) y, 80,
							(int) h, null);
			}
			break;
		case DOOR:
			if (h > w)
				g.drawImage(Game.doorPic, (int) (x - w / 2), (int) y, (int) w * 2, (int) h, null);
			else
				g.drawImage(Game.doorPicV, (int) x, (int) (y - h / 2), (int) w, (int) h * 2, null);
			break;
		case TRIGGER:
			g.drawImage(Game.artifactBase[(int) (System.currentTimeMillis() / 100 % 4)], (int) x, (int) y, 80, (int) h,
					null);
			g.drawImage(Game.artifact[(int) (System.currentTimeMillis() / 100 % 4)], (int) x, (int) (y - h), 80,
					(int) h * 2, null);
			break;
		case TRIGGERED:
			g.drawImage(Game.artifactBasePlain, (int) x, (int) y, 80, (int) h, null);
			break;
		case WIN:
			g.drawImage(Game.bigPlatform, (int) x, (int) (y + h), (int) w, (int) h, null);
			break;
		case THIN:
			if (w == 40 && h == 20)
				g.drawImage(Game.smallPlatform, (int) x, (int) y, (int) w, (int) h, null);
			else if (w == 60 && h == 20)
				g.drawImage(Game.medPlatform, (int) x, (int) y, (int) w, (int) h, null);
			else if (w == 80 && h == 20)
				g.drawImage(Game.bigPlatform, (int) x, (int) y, (int) w, (int) h, null);
			else if (w == 120 && h == 20)
				g.drawImage(Game.hugePlatform, (int) x, (int) y, (int) w, (int) h, null);
			break;
		case PILLAR:
			for (int j = 0; j < h; j += 40) {
				if (x + 80 > 0 && x - 80 < Game.WIDTH && y + j + 40 > 0 && y + j - 40 < Game.HEIGHT)

					if (j + 40 > h && j + 20 <= h)
						g.drawImage(Game.pillar, (int) x, (int) y + j, (int) w, 20, null);
					else
						g.drawImage(Game.pillar, (int) x, (int) y + j, (int) w, 40, null);
			}
			break;
		case DEFAULT:
			Random random = new Random(80085L);
			for (int i = 0; i < w; i += 40) {
				for (int j = 0; j < h; j += 40) {
					int k = random.nextInt();
					if (x + i + 40 > 0 && x + i - 40 < Game.WIDTH && y + j + 40 > 0 && y + j - 40 < Game.HEIGHT)
						if (k % 10 != 0)
							g.drawImage(Game.floor, (int) x + i, (int) y + j, 40, 40, null);
						else
							g.drawImage(Game.crackedFloor, (int) x + i, (int) y + j, 40, 40, null);
				}
			}
		}
		tick();
	}

	public Room getRoom1() {
		return null;
	}

	public Room getRoom2() {
		return null;
	}
}