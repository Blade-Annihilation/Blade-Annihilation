package com.bladeannihilation.main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.bladeannihilation.state.GameState;
import com.bladeannihilation.state.GameStateManager;
import com.bladeannihilation.keyboard.KeyboardHandler;

public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener {
	/**
	 * Serial UID randomly generated
	 */
	private static final long serialVersionUID = 3599089849376471699L;

	public static int WIDTH = 720;
	public static int HEIGHT = 540;

	public static Point mouseLocation = new Point(0, 0);
	public static boolean mousePressed = false;
	public static boolean releaseWaiting = false;
	public static boolean sizeChangeUpdate = false;
	public static boolean gameRunning = false;
	public static final Font gameFont = new Font("Monospaced", Font.PLAIN, 12);

	private Thread thread;
	private boolean running = false;
	private int maxFPS = 60;
	private double targetTimeDouble = 1000.0/maxFPS;
	private int msPerFrame = (int)targetTimeDouble;
	private final int msPerTick = 50; //1/20th of a second just kidding 1/60th just kidding 1/20th
	private boolean limitFPS = true;
	private int currentFPS = 0;
	private int second = 0; //stores time elapsed in current second
	private int updatesPerSecond = 0;
	private KeyboardHandler kh;
	public GameState gs;

	private BufferedImage buffer;
	private Graphics2D g;
	private Graphics videoMem;
	public GameStateManager gsm;
	
	public Graphics2D getCurrentGraphics() {
		return g;
	}

	public GamePanel(int width, int height) {
		GamePanel.WIDTH = width;
		GamePanel.HEIGHT = height;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener((kh = new KeyboardHandler(gsm, null)));
		requestFocusInWindow();
		gsm = new GameStateManager(this);
	}

	public void initDisplay() {
		if(buffer != null) {
			buffer.flush();
			buffer = null;
		}
		if(g != null) {
			g.dispose();
		}
		if(gs != null) {
			gs.initRender();
		}
		System.out.println("Display initializing");
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D)buffer.getGraphics();
		g.setFont(gameFont);
		videoMem = getGraphics();
		System.out.println(g + " " + buffer);
		showLoading();
	}

	public void beginRun() {
		if(thread == null) {
			running = true;
			//thread = new Thread(this);
			//thread.start();
			run();
		}
	}
	
	public void showLoading() {
		g.setColor(java.awt.Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(java.awt.Color.WHITE);
		g.drawString("Loading...", 5, HEIGHT - (g.getFontMetrics().getHeight()));
		videoMem.drawImage(buffer, 0, 0, WIDTH, HEIGHT, null);
	}

	/*public static GameState getGameState() {
		return gs;
	}

	public static boolean isGameActive() {
		return gameRunning;
	}

	public static GameStateManager getGSM() {
		return gsm;
	}*/

	@Override
	public void run() {

		initDisplay();

		long previous = System.currentTimeMillis();
		long current;
		long elapsed = 0;
		long wait;
		double lag = 0.0;

		while(running) {
			current = System.currentTimeMillis();
			elapsed = current - previous;
			second += elapsed;
			previous = current;
			lag += elapsed;

			while(lag >= msPerTick) {
				if(gameRunning) {
					gs.update();
				} else {
					gsm.update();
				}
				lag -= msPerTick;
			}

			updatesPerSecond++;

			//draw code
			if(gameRunning) {
				gs.draw(lag/msPerTick, g);
			} else {
				gsm.draw(lag/msPerTick, g);
			}
			g.drawString("FPS: " + currentFPS, 5, 15);
			if(second >= 1000) {
				currentFPS = updatesPerSecond;
				updatesPerSecond = 0;
				second = 0;
			}

			videoMem.drawImage(buffer, 0, 0, WIDTH, HEIGHT, null);

			if(limitFPS) {
				wait = msPerFrame-(System.currentTimeMillis()-current);
				if(wait < 0) {
					wait = 0;
				}
				try {
					Thread.sleep(wait);
				} catch(InterruptedException ie) {
					System.out.println("Thread interrupted!");
					ie.printStackTrace();
				}
			}
		}
	}

	public void setDimensions(Dimension size) {
		WIDTH = size.width;
		HEIGHT = size.height;
		initDisplay();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		releaseWaiting = true;
	}

	public static boolean releaseCheck() {
		if(releaseWaiting) {
			releaseWaiting = false;
			return true;
		}
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseLocation = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseLocation = e.getPoint();
	}

	public static boolean handleSize() {
		if(sizeChangeUpdate) {
			sizeChangeUpdate = false;
			return true;
		}
		return false;
	}

	public void initGame() {
		showLoading();
		System.out.println("--INITIALIZING GAME--");
		Audio.setBackgroundMusic("airlock_door_close_old");
		Resources.loadGameTiles();
		if(gs == null) {
			gs = new GameState(this); //game state is loaded by this class instead of panel because it needs more speed
			kh.setGameState(gs);
		}
	}

	public void endGameState() {
		if(gs != null) {
			gs.dispose();
			gs = null;
		}
		gameRunning = false;
	}
}
