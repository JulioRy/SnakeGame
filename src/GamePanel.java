import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
	
    char direction = 'D';
    static final int WIDTH = 800;
    static final int HEIGHT = 800;
    int playerX = WIDTH / 2;
    int playerY = HEIGHT / 2;
    static final int playerSize = 20;
    static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (playerSize * playerSize);
    int nivel = 1;
    boolean running = true;
    Random random;
    Timer timer;
    int foodX;
    int foodY;
    int foodSize;
    int foodEatenCounter = 1;
    int length = 5;
    int lifes = 1;
    int spikeX;
    int spikeY;
    int spikeCount;
    int spikeMax;
    int mapa = 1;
    int score;
    final int x[] = new int[NUMBER_OF_UNITS];
    final int y[] = new int[NUMBER_OF_UNITS];
    List<Integer> spikeXList = new ArrayList<>();
    List<Integer> spikeYList = new ArrayList<>();
    List<Rectangle> walls = new ArrayList<>();

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        play();
    }

    public void play() {
        addFood();
        addWalls();
        running = true;
        timer = new Timer(80, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (direction == 'L') {
            x[0] = x[0] - playerSize;
        } else if (direction == 'R') {
            x[0] = x[0] + playerSize;
        } else if (direction == 'U') {
            y[0] = y[0] - playerSize;
        } else {
            y[0] = y[0] + playerSize;
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            length++;
            foodEatenCounter++;
            score++;
            addFood();

            if (nivel == 2) {
                addSpike();
               
            } else if (nivel == 3) {
                addSpike();
                addSpike();
            } else if (nivel == 4) {
                addSpike();
                addSpike();
                addSpike();
            } 
        }
    }

    public void checkSpike() {
        for (int i = 0; i < spikeXList.size(); i++) {
            int spikeX = spikeXList.get(i);
            int spikeY = spikeYList.get(i);
            if (x[0] == spikeX && y[0] == spikeY) {
                length--;
                lifes--;
                foodEatenCounter--;
                System.out.println("hit spike");
            } else if (foodEatenCounter < 0) {
                running = false;
            }
        }
    }

    public void checkWall() {
        Rectangle playerRect = new Rectangle(x[0], y[0], playerSize, playerSize);
        for (Rectangle wall : walls) {
            if (playerRect.intersects(wall)) {
                running = false;
                break;
            }
        }
    }

    public void draw(Graphics graphics) {
        if (running) {
            

            for (int i = 0; i < spikeXList.size(); i++) {
                int spikeX = spikeXList.get(i);
                int spikeY = spikeYList.get(i);
                graphics.setColor(Color.MAGENTA);
                graphics.fillOval(spikeX, spikeY, playerSize, playerSize);
            }

            for (Rectangle wall : walls) {
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.fillRect(wall.x, wall.y, wall.width, wall.height);
            }

            graphics.setColor(Color.cyan);
            graphics.fillOval(x[0], y[0], playerSize, playerSize);

            for (int i = 1; i < length; i++) {
                graphics.setColor(new Color(40, 200, 150));
                graphics.fillOval(x[i], y[i], playerSize, playerSize);
            }
            
            graphics.setColor(Color.red);
            graphics.fillOval(foodX, foodY, playerSize, playerSize);

            graphics.setColor(Color.GREEN);
            graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 25));
            FontMetrics metrics = getFontMetrics(graphics.getFont());
            String scoreText = "Score: " + foodEatenCounter;
            String nivelText = "Nível: " + nivel;
            String lengthText = "Tamanho: " + length;
            String text = scoreText + " " + nivelText + " " + lengthText;
            int textWidth = metrics.stringWidth(text);
            int x = (WIDTH - textWidth) / 2;

            graphics.setColor(Color.GREEN);
            
            graphics.drawRect(0, 0, WIDTH - 2, HEIGHT - 2);

            graphics.drawString(text, x, graphics.getFont().getSize());

            if (foodEatenCounter < 5) {
                nivel = 1;
            } else if (foodEatenCounter < 10) {
            	nivel = 2;
                
                
            } 
            
            else if (foodEatenCounter < 15 && mapa == 1) {
            	nivel = 1;
                length = 5;
                foodEatenCounter = 1;
                timer.setDelay(80);
                mapa = 2;
                addWalls();
                this.setBackground(new Color(20, 40, 20));
            } else if (foodEatenCounter < 20 && mapa == 2) {
            	this.setBackground(new Color(100, 40, 20));
                nivel = 1;
                length = 5;
                foodEatenCounter = 1;
                mapa = 3;
                addWalls();
                addWalls();
                addWalls();
            }
        } else {
        
            gameOver(graphics);
        }
    }

    public void addFood() {
        foodX = random.nextInt((int) (WIDTH / playerSize)) * playerSize;
        foodY = random.nextInt((int) (HEIGHT / playerSize)) * playerSize;
    }

    public void addSpike() {
        spikeX = random.nextInt((int) (WIDTH / playerSize)) * playerSize;
        spikeY = random.nextInt((int) (HEIGHT / playerSize)) * playerSize;
        spikeXList.add(spikeX);
        spikeYList.add(spikeY);
        spikeCount++;
    }
    
    public void removeWalls() {
        walls.clear();
    }

    public void addWalls() {
        removeWalls();
        
        int numWalls = random.nextInt(4) + 4; // Entre 5 e 10 paredes
        for (int i = 0; i < numWalls; i++) {
            int wallX = random.nextInt((int) (WIDTH / playerSize)) * playerSize;
            int wallY = random.nextInt((int) (HEIGHT / playerSize)) * playerSize;
            Rectangle wall = new Rectangle(wallX, wallY, playerSize, playerSize);
            walls.add(wall);
        }
    }
    
    

    public void checkHit() {
        for (int i = length; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics) {
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 50));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        graphics.setColor(Color.GREEN);
        graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 25));
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Score Final: " + score, (WIDTH - metrics.stringWidth("Score: " + score)) / 2, graphics.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (running) {
            move();
            checkFood();
            checkHit();
            checkSpike();
            checkWall();

            if (nivel == 2) {
                timer.setDelay(70);
            } else if (nivel == 3) {
                timer.setDelay(60);
            }else if (nivel == 4) {
            	timer.setDelay(50);
            }
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
