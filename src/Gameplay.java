import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
	private boolean play = false;
	private int score = 0;
	private int totalBricks;
	private final Timer timer;

    private int playerX = 310;
	private final int playerWidth = 100;
	private final int playerHeight = 8;

	private int ballPositionX = 120;
	private int ballPositionY = 350;
	private int ballDirectionX = -1;
	private int ballDirectionY = -2;

	private MapGenerator map;

	public Gameplay() {
		map = new MapGenerator(4, 12);
		totalBricks = map.map.length * map.map[0].length;
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
        int delay = 8;
        timer = new Timer(delay, this);
		timer.start();
	}

	public void paint(Graphics g) {
		// Background
		g.setColor(Color.black);
		g.fillRect(1, 1, 692, 592);

		// Drawing map
		map.draw((Graphics2D) g);

		// Borders
		g.setColor(Color.green);
		g.fillRect(0, 0, 5, 600);
		g.fillRect(0, 0, 700, 5);
		g.fillRect(700, 0, 5, 600);

		// Score
		g.setColor(Color.CYAN);
		g.setFont(new Font("serif", Font.BOLD, 30));
		g.drawString("" + score, 600, 30);

		// Paddle
		g.setColor(Color.ORANGE);
		g.fillRect(playerX, 550, playerWidth, playerHeight);

		// Ball
		g.setColor(Color.yellow);
		g.fillOval(ballPositionX, ballPositionY, 20, 20);

		// Winning condition
		if (totalBricks <= 0) {
			gameOver(g, "You Won");
		}

		// Losing condition
		if (ballPositionY > 570) {
			gameOver(g, "Game Over, Score: " + score);
		}

		g.dispose();
	}

	private void gameOver(Graphics g, String message) {
		play = false;
		ballDirectionX = 0;
		ballDirectionY = 0;
		g.setColor(Color.red);
		g.setFont(new Font("serif", Font.BOLD, 30));
		g.drawString(message, 200, 300);
		g.setFont(new Font("serif", Font.BOLD, 20));
		g.drawString("Press Enter to Restart", 230, 350);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		if (play) {
			ballMovement();
			handlePaddleCollision();
			handleBrickCollision();
			repaint();
		}
	}

	private void ballMovement() {
		ballPositionX += ballDirectionX;
		ballPositionY += ballDirectionY;

		// Ball collision with left, top, and right borders
		if (ballPositionX < 0 || ballPositionX > 670) {
			ballDirectionX = -ballDirectionX;
		}
		if (ballPositionY < 0) {
			ballDirectionY = -ballDirectionY;
		}
	}

	private void handlePaddleCollision() {
		if (new Rectangle(ballPositionX, ballPositionY, 20, 20).intersects(new Rectangle(playerX, 550, playerWidth, playerHeight))) {
			ballDirectionY = -ballDirectionY;
		}
	}

	private void handleBrickCollision() {
		A: for (int i = 0; i < map.map.length; i++) {
			for (int j = 0; j < map.map[0].length; j++) {
				if (map.map[i][j] > 0) {
					int brickX = j * map.brickWidth + 80;
					int brickY = i * map.brickHeight + 50;
					Rectangle brickRect = new Rectangle(brickX, brickY, map.brickWidth, map.brickHeight);

					if (new Rectangle(ballPositionX, ballPositionY, 20, 20).intersects(brickRect)) {
						map.setBrickValue(0, i, j);
						score += 5;
						totalBricks--;

						// Ball bouncing logic
						if (ballPositionX + 19 <= brickRect.x || ballPositionX + 1 >= brickRect.x + brickRect.width) {
							ballDirectionX = -ballDirectionX;
						} else {
							ballDirectionY = -ballDirectionY;
						}
						break A;
					}
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 600) {
			moveRight();
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 10) {
			moveLeft();
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
			restartGame();
		}
	}

	public void moveRight() {
		play = true;
		playerX += 20;
	}

	public void moveLeft() {
		play = true;
		playerX -= 20;
	}

	private void restartGame() {
		play = true;
		ballPositionX = 120;
		ballPositionY = 350;
		ballDirectionX = -1;
		ballDirectionY = -2;
		playerX = 310;
		score = 0;
		map = new MapGenerator(4, 12);
		totalBricks = map.map.length * map.map[0].length;
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
