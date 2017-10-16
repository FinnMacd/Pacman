import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pacman extends JPanel implements KeyListener, Runnable {

	public int[] map;
	private int[] data;

	private Rectangle player = new Rectangle(40, 40, 20, 20);
	private int dir = -1;
	private int nextdir = -1;
	private int lastdir = 0;

	private int cherries = 0;

	private Enemy e1, e2, e3, e4;

	private Point lastSpot = new Point();
	private Point nextSpot1 = new Point();
	private Point nextSpot2 = new Point();
	private Point botLeft = new Point(1, 23);

	public static void main(String[] args) {
		JFrame j = new JFrame("Pacman");
		j.add(new Pacman());
		j.pack();
		j.setLocationRelativeTo(null);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setVisible(true);
	}

	public Pacman() {
		setPreferredSize(new Dimension(640, 500));
		addKeyListener(this);
		setFocusable(true);
		loadLevel();

		e1 = new Enemy(0, 324, 240, new Color(0xffFF5D00), this);
		e2 = new Enemy(1, 300, 240, new Color(0xffFFA8DD), this);
		e3 = new Enemy(2, 300, 220, new Color(0xffFF0000), this);
		e4 = new Enemy(2, 300, 260, new Color(0xff5BE1FF), this);

		new Thread(this, "Main Loop").start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.black);
		g.fillRect(0, 0, 640, 480);

		for (int y = 0; y < 25; y++) {
			for (int x = 0; x < 32; x++) {
				if (map[x + (y * 32)] == 1) {
					g.setColor(Color.blue);
					g.fillRect(x * 20, y * 20, 20, 20);
				} else if (map[x + (y * 32)] == 2) {
					g.setColor(Color.red);
					g.fillRect(x * 20, y * 20, 20, 20);
				} else if (map[x + (y * 32)] == 3) {
					g.setColor(Color.red);
					g.fillOval(x * 20 + 8, y * 20 + 8, 4, 4);
				}
			}
		}
		g.setColor(Color.yellow);
		g.fillRect(player.x, player.y, player.width, player.height);

		g.setColor(e1.color);
		g.fillRect(e1.enemy.x, e1.enemy.y, 20, 20);

		g.setColor(e2.color);
		g.fillRect(e2.enemy.x, e2.enemy.y, 20, 20);

		g.setColor(e3.color);
		g.fillRect(e3.enemy.x, e3.enemy.y, 20, 20);

		g.setColor(e4.color);
		g.fillRect(e4.enemy.x, e4.enemy.y, 20, 20);
	}

	public void loadLevel() {

		try {
			BufferedImage i = ImageIO.read((getClass().getResourceAsStream("/Map.png")));

			data = i.getRGB(0, 0, 32, 25, null, 0, 32);

		} catch (IOException e) {
			e.printStackTrace();
		}

		map = new int[data.length];

		for (int i = 0; i < map.length; i++) {
			if (data[i] == 0xff000000)
				map[i] = 1;
			else if (data[i] == 0xffff0000)
				map[i] = 2;
			else if (data[i] == 0xffffff00) {
				player.x = i % 32 * 20;
				player.y = i / 32 * 20;
				lastSpot.setLocation(i % 32, i / 32);
				nextSpot1.setLocation(i % 32, i / 32);
			} else if (data[i] == 0xffffffff) {
				map[i] = 3;
				cherries++;
			}
		}

	}

	public void run() {

		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 40.0;
		double catchUp = 0;

		while (true) {

			long now = System.nanoTime();
			catchUp += (now - lastTime) / ns;
			lastTime = now;

			while (catchUp >= 1) {
				update();
				catchUp--;
			}

			repaint();
		}

	}

	public void update() {

		if (nextdir != -1) {
			if (player.x % 20 == 0 && player.y % 20 == 0) {
				if (checkDir(nextdir)) {
					dir = nextdir;
					nextdir = -1;
				}
			}
		}

		if (player.x % 20 == 0 && player.y % 20 == 0) {
			lastSpot.setLocation(player.x / 20, player.y / 20);
			int tx = player.x / 20;
			int ty = player.y / 20;
			if (dir != -1) lastdir = dir;
			while (map[tx + ty * 32] != 1) {
				if (lastdir == 0)
					ty -= 1;
				else if (lastdir == 1)
					tx += 1;
				else if (lastdir == 2)
					ty += 1;
				else if (lastdir == 3) tx -= 1;
			}
			if (lastdir == 0)
				ty += 1;
			else if (lastdir == 1)
				tx -= 1;
			else if (lastdir == 2)
				ty -= 1;
			else if (lastdir == 3) tx += 1;
			nextSpot1.setLocation(tx, ty);
			if (!checkDir(dir)) dir = -1;
			if (map[(player.y / 20) * 32 + player.x / 20] == 3) {
				map[(player.y / 20) * 32 + player.x / 20] = 4;
				cherries--;
			}
		}

		if (dir == 0)
			player.y -= 2;
		else if (dir == 1)
			player.x += 2;
		else if (dir == 2)
			player.y += 2;
		else if (dir == 3) player.x -= 2;

		if (cherries == 0) System.out.println("You Won!!");

		if (e1.enemy.intersects(player)) System.out.println("You lose");

		e1.update();
		//e2.update();
		//e3.update();
		//e4.update();
	}

	public boolean checkDir(int dir) {

		if (dir == 0) {
			if (map[(int) ((player.y / 20 - 1) * 32 + player.x / 20)] != 1 && map[(int) ((player.y / 20 - 1) * 32 + player.x / 20)] != 2) return true;
		} else if (dir == 1) {
			if (map[(int) ((player.y / 20) * 32 + player.x / 20) + 1] != 1 && map[(int) ((player.y / 20) * 32 + player.x / 20) + 1] != 2) return true;
		} else if (dir == 2) {
			if (map[(int) ((player.y / 20 + 1) * 32 + player.x / 20)] != 1 && map[(int) ((player.y / 20 + 1) * 32 + player.x / 20)] != 2) return true;
		} else if (dir == 3) {
			if (map[(int) ((player.y / 20) * 32 + player.x / 20) - 1] != 1 && map[(int) ((player.y / 20) * 32 + player.x / 20) - 1] != 2) return true;
		}
		return false;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_UP)
			nextdir = 0;
		else if (key == KeyEvent.VK_DOWN)
			nextdir = 2;
		else if (key == KeyEvent.VK_LEFT)
			nextdir = 3;
		else if (key == KeyEvent.VK_RIGHT) nextdir = 1;

	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

	public class Enemy {

		private int type;

		public Rectangle enemy;

		private Pathfinding path;

		public Color color;

		private int dir = 1;

		private double x, y;

		private double speed = 1.8;

		private Path move = new Path();

		public Enemy(int i, int x, int y, Color c, Pacman p) {
			type = i;
			enemy = new Rectangle(x, y, 20, 20);
			color = c;
			path = new Pathfinding(map, p);
			this.x = x;
			this.y = y;
		}

		public void update() {

			if (enemy.x % 20 == 0 && enemy.y % 20 == 0) {
				if (type == 0) {

					int xd = Math.abs(lastSpot.x - enemy.x);
					System.out.println();
					int yd = Math.abs(lastSpot.y - enemy.y);
					double dist = Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
					System.out.println(move.seeNext() == -1);
					if (move.seeNext() == -1) {
						if (dist > 160)
							move = path.getDir(new Point((int) (enemy.x / 20), (int) (enemy.y / 20)), lastSpot);
						else
							move = path.getDir(new Point((int) (enemy.x / 20), (int) (enemy.y / 20)), botLeft);
					}

				} else if (type == 1) {
					if (move.seeNext() == -1) {
						move = path.getDir(new Point((int) (enemy.x / 20), (int) (enemy.y / 20)), lastSpot);
					}

				} else if (type == 2) {
					if (move.seeNext() == -1) {
						move = path.getDir(new Point((int) (enemy.x / 20), (int) (enemy.y / 20)), nextSpot1);
					}
				}

				dir = move.getDir();
			}
			if (dir == 0)
				y -= speed;
			else if (dir == 1)
				x += speed;
			else if (dir == 2)
				y += speed;
			else if (dir == 3) x -= speed;

			enemy.x = (int) x - (int) x % 2;
			enemy.y = (int) y - (int) y % 2;

		}

		public boolean checkDir(int dir) {

			if (dir == 0) {
				if (map[(int) ((enemy.y / 20 - 1) * 32 + enemy.x / 20)] != 1) return true;
			} else if (dir == 1) {
				if (map[(int) ((enemy.y / 20) * 32 + enemy.x / 20) + 1] != 1) return true;
			} else if (dir == 2) {
				if (map[(int) ((enemy.y / 20 + 1) * 32 + enemy.x / 20)] != 1) return true;
			} else if (dir == 3) {
				if (map[(int) ((enemy.y / 20) * 32 + enemy.x / 20) - 1] != 1) return true;
			}
			return false;
		}

	}

}
