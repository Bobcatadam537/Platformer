package platformer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Game extends Canvas implements Runnable {
	private Thread thread;
	private boolean running = false;

	Menu menu = new Menu(this);
	GameState state = GameState.MENU;
	static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	static final int bWIDTH = 32, bHEIGHT = 24;
	static final int Y_BORDER = 80, X_BORDER = 160;
	static final int speed = 14;
	static final int jumpPower = 15;
	static final float gravity = .5f;
	static final long TIMER = 90 * 1000;
	boolean rumble = false;
	long timePassed, timeStart = 0;
	private ArrayList<GameObject> movable = new ArrayList<GameObject>();
	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<Platform> doors = new ArrayList<Platform>();
	static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Room> rooms = new ArrayList<Room>();
	Room currentRoom;
	private ArrayList<GameObject> scrolling = new ArrayList<GameObject>();
	private Player player = new Player(10, 10, 2, 5);
	Random random = new Random();

	static Image doorPic, doorPicV, smallPlatform, medPlatform, bigPlatform, hugePlatform, floor, crackedFloor, pillar,
			alien, alienShoot, alienR, alienShootR, artifactBasePlain;
	static Image[] pillarBig, lava, artifactBase, artifact;
	static Image r0_0, r1_0, r1_1, r2_12, r5_12, r9_12;
	static Image playerShoot;
	static Image[] playerWalkRight, playerWalkLeft, playerWalkRightS, playerWalkLeftS, playerJump;
	static Image menuBack, lazerPic;

	public Game() {
		movable.add(player);
		initializeImages();
		initialize();
		new Window(WIDTH, HEIGHT + 35, "Game", this);
		this.addKeyListener(new KeyInput(movable));
		this.addMouseListener(menu);
		requestFocus();
	}

	public void initialize() {
		timeStart = 0;
		player.pause = false;
		platforms.removeAll(platforms);
		doors.removeAll(doors);
		enemies.removeAll(enemies);
		scrolling.removeAll(scrolling);
		rooms.removeAll(rooms);

		room0_0();
		room1_0();
		room1_1();
		room2_12();
		room5_12();
		room9_12();

		platforms.addAll(doors);
		scrolling.addAll(platforms);
		scrolling.addAll(enemies);
		scrolling.addAll(rooms);
		player.respawn();
		currentRoom = rooms.get(0);
	}

	public void shaftSegment(int h) {
		h = bHEIGHT - 2 + 32 * h;
		switch (random.nextInt(5) + 1) {
		case 1:
			platforms.add(new Platform(bWIDTH + 17, h + 4, 2, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 23, h + 9, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 6, h + 11, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 19, h + 16, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 9, h + 18, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 16, h + 23, 3, 1, platformType.THIN));
			break;
		case 2:
			platforms.add(new Platform(bWIDTH + 6, h + 6, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 19, h + 10, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 10, h + 16, 4, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 21, h + 21, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 6, h + 23, 3, 1, platformType.THIN));
			break;
		case 3:
			platforms.add(new Platform(bWIDTH + 21, h + 5, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 17, h + 10, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 6, h + 16, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 16, h + 21, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 22, h + 24, 3, 1, platformType.THIN));
			break;
		case 4:
			platforms.add(new Platform(bWIDTH + 16, h + 4, 2, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 20, h + 10, 2, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 10, h + 16, 2, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 18, h + 18, 2, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 6, h + 24, 2, 1, platformType.THIN));
			break;
		case 5:
			platforms.add(new Platform(bWIDTH + 8, h + 7, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 18, h + 9, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 8, h + 16, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 18, h + 18, 3, 1, platformType.THIN));
			platforms.add(new Platform(bWIDTH + 6, h + 24, 3, 1, platformType.THIN));
			break;
		}
		platforms.add(new Platform(bWIDTH + 11, h + 30, 4, 1, platformType.THIN));// keep constant
	}

	public void gameOver() {
		state = GameState.GAME_OVER;
		initialize();
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (running) {
			tick();
		}
		stop();
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		switch (state) {
		case RUNNING:
			renderGame(g);
			break;
		case GAME_OVER:
			menu.renderDeath(g);
			break;
		case WIN_ANI:
			renderGame(g);
			break;
		case WIN:
			renderGame(g);
			menu.renderWin(g);
			break;
		case MENU:
			menu.render(g);
		}

		g.dispose();
		bs.show();

		try {
			Thread.sleep(speed);
		} catch (Exception e) {
		}
	}

	public void renderGame(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if (state != GameState.MENU) {
			// rumble on
			if (timePassed != 0 && !player.pause && timePassed % 10 == 0) {
				for (GameObject o : scrolling)
					o.x += 2;
				player.x += 2;
				for (GameObject o : scrolling)
					o.y += 2;
				player.y += 2;
				rumble = true;
			}
			for (Room r : rooms) {
				r.render(g);
				if (r.touches(player))
					currentRoom = r;
			}
			player.render(g);
			for (Enemy e : enemies) {
				if (e.alive)
					e.render(g);
				else
					e.y = e.x = 2000;
			}
			for (Platform p : platforms) {
				p.render(g);
			}
			if (rumble) {
				for (GameObject o : scrolling)
					o.x -= 2;
				player.x -= 2;
				for (GameObject o : scrolling)
					o.y -= 2;
				player.y -= 2;
				rumble = false;
			}
			if (state == GameState.RUNNING)
				newCam();
		}
		// GUI
		g.setColor(Color.darkGray);
		g.fillRect(0, HEIGHT, WIDTH, 35);
		g.setFont(new Font("TimesRoman", Font.BOLD, 20));
		int health = player.damage();
		g.setColor(Color.black);
		g.fillRect(4, HEIGHT + 4, 302, 27);
		g.fillRect(WIDTH - 105, HEIGHT + 5, 90, 25);
		if (health > 30)
			g.setColor(Color.green);
		else if (health > 10)
			g.setColor(Color.yellow);
		else
			g.setColor(Color.red);
		g.fillRect(5, HEIGHT + 5, player.damage() * 3, 25);
		g.setColor(Color.red);
		String timer = "00:00:00";
		String goal = "Find artifact";
		if (timeStart != 0) {
			timePassed = System.currentTimeMillis() - timeStart;
			long minutes = (TIMER - timePassed) / 1000 / 60;
			long seconds = (TIMER - timePassed) / 1000 % 60;
			long millis = (TIMER - timePassed) / 10 % 100;
			timer = (minutes < 10 ? "0" + minutes : "" + minutes) + ":" + (seconds < 10 ? "0" + seconds : "" + seconds)
					+ ":" + (millis < 10 ? "0" + millis : "" + millis);
			goal = "ESCAPE";
			if (TIMER - timePassed <= 0) {
				timer = "00:00:00";
				gameOver();
			}
		}
		if (state == GameState.RUNNING) {
			g.drawString(timer, WIDTH - 100, HEIGHT + 25);
			g.setColor(Color.black);
			g.drawString("Objective: " + goal, 310, HEIGHT + 25);
		}
	}

	public void tick() {
		switch (state) {
		case RUNNING:
			tickGame();
			break;
		case GAME_OVER:
			// placeholder
			break;
		case WIN_ANI:
			tickAni();
			tickGame();
			break;
		case WIN:
			tickWin();
		case MENU:
			// placeholder
		}
		render();
	}

	public void tickGame() {
		// CHECK FOR GAME OVER
		if (player.damage() <= 0) {
			gameOver();
		}
		// PLAYER MOVEMENT
		player.x += player.vx;
		if (player.lvx != 0) {
			player.lx += player.lvx;

		} else {
			player.lx = player.x;
			player.ly = player.y;
		}
		for (Enemy e : enemies) {
			if (e.touchesLazer(player)) {
				e.damage(10);
				player.lvx = 0;
			}
		}
		for (Platform p : platforms) {
			if (player.touches(p)) {
				switch (p.type) {
				case KILL:
					player.damage(10);
					player.x -= player.vx / 2;
					player.lx = player.x;
					break;
				case DOOR:
					player.lvx = 0;
					for (Enemy e : enemies)
						e.lvx = 0;
					break;
				case TRIGGER:
					startEscape();
					p.type = platformType.TRIGGERED;
					player.x -= player.vx;
					player.lvx = 0;
					// player.lx -= player.vx;
					break;
				case WIN:
					if (timeStart != 0)
						state = GameState.WIN_ANI;
					timeStart = 0;
					break;
				default:
					player.x -= player.vx;
					// player.lx = player.x;
					break;
				}
			}
		}

		if (player.vy <= jumpPower) {
			player.vy += gravity;
		}
		player.y += player.vy;
		for (Platform p : platforms) {
			if (player.touches(p)) {
				switch (p.type) {
				case KILL:
					player.damage(10);
					player.y -= player.vy / 2;
					break;
				case DOOR:
					if (player.vy < 0)
						player.vy = -12;
					player.lvx = 0;
					for (Enemy e : enemies)
						e.lvx = 0;
					break;
				case TRIGGER:
					startEscape();
					p.type = platformType.TRIGGERED;
					player.x -= player.vx;
					player.lvx = 0;
					break;
				case WIN:
					if (timeStart != 0)
						state = GameState.WIN_ANI;
					timeStart = 0;
					break;
				default:
					player.y -= player.vy;
					player.ly = player.y;
					if (player.y < p.y) {
						player.y = p.y - player.h;
						player.canJump = true;
					} else {
						player.y = p.y + p.h;
					}
					player.vy = 0;
					break;
				}
			}
		}
		// ENEMY BEHAVIOR
		for (Enemy e : enemies) {
			if (e.health <= 0)
				e.alive = false;
			if (e.lvx != 0) {
				e.lx += e.lvx;
			} else {
				e.lx = e.x;
			}
			if (player.touches(e)) {
				player.damage(10);
				if (player.x + (player.w / 2) <= e.x + (e.w / 2)) {
					player.vxtra = -7;
				} else if (player.x + (player.w / 2) > e.x + (e.w / 2)) {
					player.vxtra = 7;
				}
				player.vy = -5;
			} else if (player.touchesLazer(e)) {
				player.damage(10);
				e.lvx = 0;
			}
			if (e.onScreen()) {
				e.attack(player);
			}
		}

//		if (timePassed != 0 && !player.pause) {
//			if (timePassed % 10 == 0) {
//				for (GameObject o : scrolling) {
//					o.x += 2;
//					o.y += 2;
//				}
//				player.x += 2;
//				// player.lx = player.x;
//				player.y += 2;
//				// player.ly = player.y;
//				rumble = true;
//			} else if (rumble) {
//				for (GameObject o : scrolling) {
//					o.x -= 2;
//					o.y -= 2;
//				}
//				player.x -= 2;
//				player.y -= 2;
//				rumble = false;
//				// for(Enemy e : enemies){
//				// e.respawn();
//				// }
//			}
//
//		}
	}

	public void newCam() {
		int x = (int) currentRoom.x;
		int y = (int) currentRoom.y;
		int w = (int) currentRoom.w;
		int h = (int) currentRoom.h;

		while (player.x + player.w > WIDTH - X_BORDER) {
			if (x - 1 > 0 || x + w - 1 < WIDTH)
				break;
			player.x -= 1;
			for (GameObject o : scrolling)
				o.x -= 1;
			for (Enemy e : enemies)
				e.lx -= 1;
		}
		if (x > 0) {
			if (x + 20 > 0) {
				player.x -= 20;
				for (GameObject o : scrolling)
					o.x -= 20;
				for (Enemy e : enemies)
					e.lx -= 20;
			} else {
				player.x += x;
				for (GameObject o : scrolling)
					o.x += x;
				for (Enemy e : enemies)
					e.lx += x;
			}
		}

		while (player.x < X_BORDER) {
			if (x + 1 > 0 || x + w + 1 < WIDTH)
				break;
			player.x += 1;
			for (GameObject o : scrolling)
				o.x += 1;
			for (Enemy e : enemies)
				e.lx += 1;
		}
		if (x + w < WIDTH) {
			if (x + w + 20 < WIDTH) {
				player.x += 20;
				for (GameObject o : scrolling)
					o.x += 20;
				for (Enemy e : enemies)
					e.lx += 20;
			} else {
				player.x -= x + w - WIDTH;
				for (GameObject o : scrolling)
					o.x -= x + w - WIDTH;
				for (Enemy e : enemies)
					e.lx -= x + w - WIDTH;
			}
		}

		if (y > 0) {
			if (y + 20 > 0) {
				player.y -= 20;
				if (player.lvx != 0)
					player.ly -= 20;
				for (GameObject o : scrolling)
					o.y -= 20;
			} else {
				player.y += y;
				if (player.lvx != 0)
					player.ly += y;
				for (GameObject o : scrolling)
					o.y += y;
			}
		} else {
			while (player.y + player.h > HEIGHT - Y_BORDER) {
				player.y -= 1;
				if (player.lvx != 0)
					player.ly -= 1;
				for (GameObject o : scrolling)
					o.y -= 1;
			}
		}

		if (y + h < HEIGHT) {
			if (y + h + 20 < HEIGHT) {
				player.y += 20;
				if (player.lvx != 0)
					player.ly += 20;
				for (GameObject o : scrolling)
					o.y += 20;
			} else {
				player.y -= y + h - HEIGHT;
				if (player.lvx != 0)
					player.ly -= y + h - HEIGHT;
				for (GameObject o : scrolling)
					o.y -= y + h - HEIGHT;
			}
		} else {
			while (player.y < Y_BORDER) {
				player.y += 1;
				if (player.lvx != 0)
					player.ly += 1;
				for (GameObject o : scrolling)
					o.y += 1;
			}
		}
	}

	public void tickAni() {
		Platform winPlatform = platforms.get(0);
		player.pause = true;
		if (winPlatform.x + winPlatform.w / 2 > player.x + player.w / 2) {
			player.facingEast = true;
			player.vx = 1;
		} else if (winPlatform.x + winPlatform.w / 2 < player.x + player.w / 2) {
			player.facingEast = false;
			player.vx = -1;
		} else if (winPlatform.x + winPlatform.w / 2 == player.x + player.w / 2) {
			if (Math.abs(player.x + player.w / 2 - WIDTH / 2) > 2
					|| Math.abs(player.y + player.h / 2 - HEIGHT / 2) > 2) {
				if (Math.abs(player.x + player.w / 2 - WIDTH / 2) > 2) {
					if (player.x + player.w / 2 > WIDTH / 2) {
						for (GameObject o : scrolling)
							o.shiftX(-2);
						player.shiftX(-2);
					} else if (player.x + player.w / 2 < WIDTH / 2) {
						for (GameObject o : scrolling)
							o.shiftX(2);
						player.shiftX(2);
					}
				}

				if (Math.abs(player.y + player.h / 2 - HEIGHT / 2) > 2) {
					if (player.y + player.h / 2 > HEIGHT / 2) {
						for (GameObject o : scrolling)
							o.shiftY(-2);
						player.shiftY(-2);
					} else if (player.y + player.h / 2 < HEIGHT / 2) {
						for (GameObject o : scrolling)
							o.shiftY(2);
						player.shiftY(2);
					}
				}
			} else {
				state = GameState.WIN;
			}
		}
	}

	public void tickWin() {
		Platform winPlatform = platforms.get(0);
		if (player.y + player.h > -20) {
			player.y -= 2;
			winPlatform.shiftY(-2);
		}
	}

	public static void main(String[] args) {
		new Game();
	}

	public void startEscape() {
		timeStart = System.currentTimeMillis();

	}

	private void initializeImages() {
		try {
			doorPic = ImageIO.read(getClass().getResource("/platformer/textures/Door.png"));
			doorPicV = ImageIO.read(getClass().getResource("/platformer/textures/VerticalDoor.png"));
			smallPlatform = ImageIO.read(getClass().getResource("/platformer/textures/SmallPlatform.png"));
			medPlatform = ImageIO.read(getClass().getResource("/platformer/textures/MediumPlatform.png"));
			bigPlatform = ImageIO.read(getClass().getResource("/platformer/textures/LargePlatform.png"));
			hugePlatform = ImageIO.read(getClass().getResource("/platformer/textures/HugePlatform.png"));
			floor = ImageIO.read(getClass().getResource("/platformer/textures/TileableBrick.png"));
			crackedFloor = ImageIO.read(getClass().getResource("/platformer/textures/TileableCrackedBrick.png"));
			pillar = ImageIO.read(getClass().getResource("/platformer/textures/SmallPillar.png"));
			pillarBig = new Image[2];
			pillarBig[0] = ImageIO.read(getClass().getResource("/platformer/textures/LargePillarLeft.png"));
			pillarBig[1] = ImageIO.read(getClass().getResource("/platformer/textures/LargePillarRight.png"));
			lava = new Image[4];
			lava[0] = ImageIO.read(getClass().getResource("/platformer/textures/lava/Lava1.png"));
			lava[1] = ImageIO.read(getClass().getResource("/platformer/textures/lava/Lava2.png"));
			lava[2] = ImageIO.read(getClass().getResource("/platformer/textures/lava/Lava3.png"));
			lava[3] = ImageIO.read(getClass().getResource("/platformer/textures/lava/Lava4.png"));
			r0_0 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room0_0.png"));
			r1_0 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room1_0.png"));
			r1_1 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room1_1.png"));
			r2_12 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room2_12.png"));
			r5_12 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room5_12.png"));
			r9_12 = ImageIO.read(getClass().getResource("/platformer/textures/Rooms/Room9_12.png"));

			playerWalkRight = new Image[4];
			playerWalkRight[0] = playerWalkRight[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/Playerright.png"));
			playerWalkRight[1] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkright1.png"));
			playerWalkRight[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkright2.png"));

			playerWalkRightS = new Image[4];
			playerWalkRightS[0] = playerWalkRightS[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playershootR.png"));
			playerWalkRightS[1] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkright1S.png"));
			playerWalkRightS[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkright2S.png"));

			playerWalkLeft = new Image[4];
			playerWalkLeft[0] = playerWalkLeft[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/Playerleft.png"));
			playerWalkLeft[1] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkleft1.png"));
			playerWalkLeft[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkleft2.png"));

			playerWalkLeftS = new Image[4];
			playerWalkLeftS[0] = playerWalkLeftS[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playershoot.png"));
			playerWalkLeftS[1] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkleft1S.png"));
			playerWalkLeftS[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerwalkleft2S.png"));

			playerJump = new Image[4];
			playerJump[0] = ImageIO.read(getClass().getResource("/platformer/textures/PlayerTextures/playerJumpL.png"));
			playerJump[1] = ImageIO.read(getClass().getResource("/platformer/textures/PlayerTextures/playerJumpR.png"));
			playerJump[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerJumpSL.png"));
			playerJump[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/PlayerTextures/playerJumpSR.png"));

			lazerPic = ImageIO.read(getClass().getResource("/platformer/textures/Lazer.png"));

			alien = ImageIO.read(getClass().getResource("/platformer/textures/AlienTextures/Alien.png"));
			alienShoot = ImageIO.read(getClass().getResource("/platformer/textures/AlienTextures/AlienShoot.png"));
			alienR = ImageIO.read(getClass().getResource("/platformer/textures/AlienTextures/AlienR.png"));
			alienShootR = ImageIO.read(getClass().getResource("/platformer/textures/AlienTextures/AlienShootR.png"));

			pillarBig[0] = ImageIO.read(getClass().getResource("/platformer/textures/LargePillarLeft.png"));
			pillarBig[1] = ImageIO.read(getClass().getResource("/platformer/textures/LargePillarRight.png"));

			artifactBase = new Image[4];
			artifactBase[0] = ImageIO
					.read(getClass().getResource("/platformer/textures/ArtifactBase/ArtifactBaseAura1.png"));
			artifactBase[1] = ImageIO
					.read(getClass().getResource("/platformer/textures/ArtifactBase/ArtifactBaseAura2.png"));
			artifactBase[2] = ImageIO
					.read(getClass().getResource("/platformer/textures/ArtifactBase/ArtifactBaseAura3.png"));
			artifactBase[3] = ImageIO
					.read(getClass().getResource("/platformer/textures/ArtifactBase/ArtifactBaseAura4.png"));
			artifactBasePlain = ImageIO
					.read(getClass().getResource("/platformer/textures/ArtifactBase/ArtifactBase.png"));

			artifact = new Image[4];
			artifact[0] = ImageIO.read(getClass().getResource("/platformer/textures/Artifact/Artifact1.png"));
			artifact[1] = ImageIO.read(getClass().getResource("/platformer/textures/Artifact/Artifact2.png"));
			artifact[2] = ImageIO.read(getClass().getResource("/platformer/textures/Artifact/Artifact3.png"));
			artifact[3] = ImageIO.read(getClass().getResource("/platformer/textures/Artifact/Artifact4.png"));

			menuBack = ImageIO.read(getClass().getResource("/platformer/textures/Menu.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Images didn't load");
		}
	}

	private void room0_0() {
		rooms.add(new Room(-bWIDTH, -bHEIGHT, 2 * bWIDTH, 2 * bHEIGHT, r0_0));
		platforms.add(new Platform(-2, bHEIGHT - 13, 4, 1, platformType.WIN));// ship door
		platforms.add(new Platform(-bWIDTH, bHEIGHT - 4, bWIDTH * 2, 4));// floor
		platforms.add(new Platform(-bWIDTH, -bHEIGHT, bWIDTH * 2, 4));// ceiling
		platforms.add(new Platform(-bWIDTH, -bHEIGHT, 4, 2 * bHEIGHT));// left wall
		platforms.add(new Platform(bWIDTH - 4, 0, 8, bHEIGHT - 12));// right wall : 1, 0 right wall
		platforms.add(new Platform(-4, bHEIGHT - 12, 8, 4));// ship body
	}

	private void room1_0() {
		rooms.add(new Room(bWIDTH, 0, bWIDTH, bHEIGHT, r1_0));
		doors.add(new Door(bWIDTH - 2, bHEIGHT - 12, false, rooms.get(0), rooms.get(1)));// door
		platforms.add(new Platform(bWIDTH, 0, bWIDTH, 4));
		platforms.add(new Platform(bWIDTH, bHEIGHT - 4, 12, 8));// left floor : 1, 1 ceiling
		platforms.add(new Platform(bWIDTH + 20, bHEIGHT - 4, 12, 8));// right floor : 1, 1 ceiling
		platforms.add(new Platform(bWIDTH * 2 - 4, 0, 8, bHEIGHT * 12 - 12));// right wall thru 1, 11
	}

	private void room1_1() {
		rooms.add(new Room(bWIDTH, bHEIGHT, bWIDTH, bHEIGHT * 11, r1_1));
		doors.add(new Door(bWIDTH + 12, bHEIGHT - 2, true, rooms.get(1), rooms.get(2)));// door
		platforms.add(new Platform(bWIDTH, bHEIGHT, 4, bHEIGHT * 11));// left wall thru 1, 11
		platforms.add(new Platform(bWIDTH + 4, bHEIGHT - 2 + 12, 3, 1, platformType.THIN));
		platforms.add(new Platform(bWIDTH + 25, bHEIGHT - 2 + 12, 3, 1, platformType.THIN));
		platforms.add(new Platform(bWIDTH + 12, bHEIGHT - 2 + 14, 8, 4));
		platforms.add(new Platform(bWIDTH + 7, bHEIGHT - 2 + 20, 2, 1, platformType.THIN));
		platforms.add(new Platform(bWIDTH + 23, bHEIGHT - 2 + 20, 2, 1, platformType.THIN));
		platforms.add(new Platform(bWIDTH + 9, bHEIGHT - 2 + 29, 3, 1, platformType.THIN));
		platforms.add(new Platform(bWIDTH + 20, bHEIGHT - 2 + 29, 3, 1, platformType.THIN));
		// shaft segments
		shaftSegment(1);
		shaftSegment(2);
		shaftSegment(3);
		shaftSegment(4);
		shaftSegment(5);
		shaftSegment(6);
		shaftSegment(7);
		// shaft bottom
		platforms.add(new Platform(bWIDTH, bHEIGHT * 12 - 4, bWIDTH, 10));// bottom floor
	}

	private void room2_12() {
		rooms.add(new Room(bWIDTH * 2, bHEIGHT * 11, bWIDTH * 3, bHEIGHT * 2, r2_12));
		doors.add(new Door(bWIDTH * 2 - 2, bHEIGHT * 12 - 12, false, rooms.get(2), rooms.get(3)));// door to 2, 12
		platforms.add(new Platform(bWIDTH * 2, bHEIGHT * 11, bWIDTH * 3, 4));// ceiling thru 5, 12
		platforms.add(new Platform(bWIDTH * 2, bHEIGHT * 12 + 2, bWIDTH * 3, 4));// bottom floor thru 5, 12
		platforms.add(new Platform(bWIDTH * 2 + 6, bHEIGHT * 12 - 20, 10, 4, platformType.GHOSTLY));// left object
		platforms.add(new Platform(bWIDTH * 2, bHEIGHT * 12 - 4, 18, 6));// left platform
		platforms.add(new Platform(bWIDTH * 2 + 22, bHEIGHT * 12 - 7, 2, 9, platformType.PILLAR));// 1st hurdle bottom
		platforms.add(new Platform(bWIDTH * 2 + 22, bHEIGHT * 12 - 20, 2, 7, platformType.PILLAR));// 1st hurdle top
		platforms.add(new Platform(bWIDTH * 2 + 28, bHEIGHT * 12 - 6, 6, 1, platformType.THIN));// 1st platform
		enemies.add(new Enemy(bWIDTH * 2 + 30, bHEIGHT * 12 - 11, 2, 5));// 1st enemy
		platforms.add(new Platform(bWIDTH * 2 + 22 + 16, bHEIGHT * 12 - 7, 2, 9, platformType.PILLAR));// 2nd hurdle
																										// bottom
		platforms.add(new Platform(bWIDTH * 2 + 22 + 16, bHEIGHT * 12 - 20, 2, 7, platformType.PILLAR));// 2nd hurdle
																										// top
		platforms.add(new Platform(bWIDTH * 2 + 28 + 16, bHEIGHT * 12 - 6, 6, 1, platformType.THIN));// 2nd platform
		enemies.add(new Enemy(bWIDTH * 2 + 30 + 16, bHEIGHT * 12 - 11, 2, 5));// 2nd enemy
		platforms.add(new Platform(bWIDTH * 2 + 22 + 32, bHEIGHT * 12 - 7, 2, 9, platformType.PILLAR));// 3rd hurdle
																										// bottom
		platforms.add(new Platform(bWIDTH * 2 + 22 + 32, bHEIGHT * 12 - 20, 2, 7, platformType.PILLAR));// 3rd hurdle
																										// top
		platforms.add(new Platform(bWIDTH * 2 + 28 + 32, bHEIGHT * 12 - 6, 6, 1, platformType.THIN));// 3rd platform
		enemies.add(new Enemy(bWIDTH * 2 + 30 + 32, bHEIGHT * 12 - 11, 2, 5));// 3rd enemy
		platforms.add(new Platform(bWIDTH * 2 + 22 + 48, bHEIGHT * 12 - 7, 2, 9, platformType.PILLAR));// 4th hurdle
																										// bottom
		platforms.add(new Platform(bWIDTH * 2 + 22 + 48, bHEIGHT * 12 - 20, 2, 7, platformType.PILLAR));// 4th hurdle
																										// top
		platforms.add(new Platform(bWIDTH * 2 + 28 + 47, bHEIGHT * 12 - 6, 4, 1, platformType.THIN));// 4th platform
		enemies.add(new Enemy(bWIDTH * 2 + 30 + 46, bHEIGHT * 12 - 11, 2, 5));// 4th enemy
		platforms.add(new Platform(bWIDTH * 2 + 22 + 60, bHEIGHT * 12 - 7, 2, 9, platformType.PILLAR));// 5th hurdle
																										// bottom
		platforms.add(new Platform(bWIDTH * 2 + 22 + 60, bHEIGHT * 12 - 20, 2, 7, platformType.PILLAR));// 5th hurdle
																										// top
		platforms.add(new Platform(bWIDTH * 2 + 22 + 66, bHEIGHT * 12 - 4, 8, 6));// right platform
		platforms.add(new Platform(bWIDTH * 5 - 4, bHEIGHT * 11, 8, bHEIGHT - 12));// right wall
	}

	private void room5_12() {
		rooms.add(new Room(bWIDTH * 5, bHEIGHT * 11, bWIDTH * 4, bHEIGHT * 2, r5_12));
		doors.add(new Door(bWIDTH * 5 - 2, bHEIGHT * 12 - 12, false, rooms.get(3), rooms.get(4)));
		platforms.add(new Platform(bWIDTH * 5, bHEIGHT * 12 + 4, bWIDTH * 4, 4));// bottom floor thru 5, 12
		platforms.add(new Platform(bWIDTH * 5, bHEIGHT * 11, bWIDTH * 4, 6));// ceiling thru 5, 12
		platforms.add(new Platform(bWIDTH * 5, bHEIGHT * 12 - 4, 10, 2, platformType.GHOSTLY));// left platform
		platforms.add(new Platform(bWIDTH * 5 + 6, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));// left platform
																									// support
		platforms.add(new Platform(bWIDTH * 5 + 8, bHEIGHT * 12 - 6, 4, 2, platformType.GHOSTLY));// left platform lip
		// platforms.add(new Platform(bWIDTH * 5, bHEIGHT * 12, 2, 2));
		platforms.add(new Platform(bWIDTH * 5 + 20, bHEIGHT * 12 - 8, 2, 2));// start of platforms
		platforms.add(new Platform(bWIDTH * 5 + 28, bHEIGHT * 12 - 6, 2, 2));
		platforms.add(new Platform(bWIDTH * 5 + 36, bHEIGHT * 12 - 4, 2, 2));
		platforms.add(new Platform(bWIDTH * 5 + 44, bHEIGHT * 12 - 2, 2, 2));
		platforms.add(new Platform(bWIDTH * 5 + 52, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));// start of pillars
		platforms.add(new Platform(bWIDTH * 5 + 60, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 5 + 68, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 5 + 76, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 5 + 84, bHEIGHT * 12 + 2, 34, 2, platformType.PILLAR));// start of thick
																									// pillars
		platforms.add(new Platform(bWIDTH * 5 + 84, bHEIGHT * 12 - 4, 4, 8, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 5 + 96, bHEIGHT * 12 - 4, 4, 8, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 5 + 108, bHEIGHT * 12 - 4, 4, 8, platformType.PILLAR));
		platforms.add(new Platform(bWIDTH * 9 - 10, bHEIGHT * 12 - 4, 10, 2, platformType.GHOSTLY));// right platform
		platforms.add(new Platform(bWIDTH * 9 - 8, bHEIGHT * 12 - 2, 2, 6, platformType.PILLAR));// right platform
																									// support
		platforms.add(new Platform(bWIDTH * 9 - 12, bHEIGHT * 12 - 6, 4, 2, platformType.GHOSTLY));// right platform lip
		platforms.add(new Platform(bWIDTH * 5, bHEIGHT * 12, bWIDTH * 4, 8, platformType.KILL));// lava
		platforms.add(new Platform(bWIDTH * 9 - 4, bHEIGHT * 11, 8, bHEIGHT - 12));// right wall
	}

	private void room9_12() {
		rooms.add(new Room(bWIDTH * 9, bHEIGHT * 11, bWIDTH, bHEIGHT, r9_12));
		doors.add(new Door(bWIDTH * 9 - 2, bHEIGHT * 12 - 12, false, rooms.get(4), rooms.get(5)));
		platforms.add(new Platform(bWIDTH * 9, bHEIGHT * 11 - 4, bWIDTH, 8));// ceiling
		platforms.add(new Platform(bWIDTH * 9, bHEIGHT * 12 - 4, bWIDTH, 8));// floor
		platforms.add(new Platform(bWIDTH * 10 - 4, bHEIGHT * 11, 8, bHEIGHT));// right wall
		platforms.add(new Platform(bWIDTH * 9 + 14, bHEIGHT * 12 - 8, 4, 4, platformType.TRIGGER));
	}
}