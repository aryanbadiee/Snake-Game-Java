package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.*;
import java.util.Formatter;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

public int t = 0; // for your score

// get screen size:
private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

//Holds height and width of the window
public final static int BOARDWIDTH = (int) screenSize.getWidth();     //casting double to int
public final static int BOARDHEIGHT = (int) screenSize.getHeight();   //casting double to int

//Used to represent pixel size of food & our snake's joints
public final static int PIXELSIZE = 25;
//The total amount of pixels the game could possibly have.
//We don't want less, because the game would end prematurely.
//We don't more because there would be no way to let the player win.

private final static int TOTALPIXELS = (BOARDWIDTH * BOARDHEIGHT) / (PIXELSIZE * PIXELSIZE);

private Image aks ; //image

//Check to see if the game is running
private boolean inGame = true;

//Timer used to record tick times
private Timer timer;

//Used to set game speed, the lower the #, the faster the snake travels
//which in turn
//makes the game harder.
private static int speed = 60;

//Instances of our snake & food so we can use their methods
private Snake snake = new Snake();
private Food food = new Food();

public Board() 
{
  addKeyListener(new Keys());
  setBackground(Color.black);
  setFocusable(true);

  setPreferredSize(new Dimension(BOARDWIDTH, BOARDHEIGHT));
  
  initializeGame();
}

//Used to paint our components to the screen
@Override
protected void paintComponent(Graphics g){
	super.paintComponent(g);
	try {
        draw(g);
    }catch (Exception e){
	    e.printStackTrace();
    }
}

// Draw our Snake & Food (Called on repaint()).
void draw(Graphics g) throws Exception {
  // Only draw if the game is running / the snake is alive
  if (inGame == true) {
	  Font font = new Font("Arial", Font.ITALIC, 20);
	  g.setFont(font);

	  int best = Save();
	  if(best < t) {
		  best = t;
	  }

	  g.setColor(Color.yellow);
	  g.drawString("Score: " + t , 0 , 20);
	  g.drawString("Best Score: " + best , 0 , 40);
	  
	  g.setColor(Color.orange);
	  g.fillOval(food.getFoodX(), food.getFoodY(), PIXELSIZE, PIXELSIZE); // food
	  
      // Draw our snake.
      for (int i = 0; i < snake.getJoints(); i++) {
          if (i == 0) {
            // Snake's head :
            aks = ImageIO.read(new File("img/head.png"));
            g.drawImage(aks, snake.getSnakeX(i), snake.getSnakeY(i), null);
          } else {
            // Body of snake :
        	  g.setColor(Color.green); // color of body!
            g.fillOval(snake.getSnakeX(i), snake.getSnakeY(i), PIXELSIZE, PIXELSIZE);
          }
      }
      // Sync our graphics together
      Toolkit.getDefaultToolkit().sync();
  } 
  else {
      // If we're not alive, then we end our game
      endGame(g);
      t = 0;
  }
}

void initializeGame() {
  snake.setJoints(3); // set our snake's initial size

  // Create our snake's body
  for (int i = 0; i < snake.getJoints(); i++) {
      snake.setSnakeX(BOARDWIDTH / 2);
      snake.setSnakeY(BOARDHEIGHT / 2);
  }
  // Start off our snake moving right
  snake.setMovingLeft(true);

  // Generate our first 'food'
  food.createFood();
  // set the timer to record our game's speed / make the game move
  timer = new Timer(speed,this);
  timer.start();
}

//if our snake is in the close proximity of the food..
void checkFoodCollisions() {

  if ((proximity(snake.getSnakeX(0), food.getFoodX(), 20))
          && (proximity(snake.getSnakeY(0), food.getFoodY(), 20))) {

      System.out.println("intersection");
      // Add a 'joint' to our snake
      snake.setJoints(snake.getJoints() + 1);
      // Create new food
      food.createFood();
      t = snake.getJoints() -3;
      
  }
}

//Used to check collisions with snake's self and board edges
void checkCollisions() {

  // If the snake hits its' own joints..
  for (int i = snake.getJoints(); i > 0; i--) {

      // Snake can not intersect with itself if it's not larger than 5
      if ((i > 5)
              && (snake.getSnakeX(0) == snake.getSnakeX(i) && (snake
                      .getSnakeY(0) == snake.getSnakeY(i)))) {
          inGame = false; // then the game ends
      }
  }

  // If the snake intersects with the board edges..
  if (snake.getSnakeY(0) >= (BOARDHEIGHT - 30)) {
      inGame = false;
  }

  if (snake.getSnakeY(0) < 0) {
      inGame = false;
  }

  if (snake.getSnakeX(0) >= BOARDWIDTH) {
      inGame = false;
  }

  if (snake.getSnakeX(0) < 0) {
      inGame = false;
  }

  // If the game has ended, then we can stop our timer
  if (!inGame) {
      timer.stop();
  }

}

public int Save(int score) {
	try {
		String save;
		Scanner fileIn = new Scanner(new FileReader("save.txt"));
		save = fileIn.nextLine();
    fileIn.close();
		int number = Integer.parseInt(save);
		if(number < score) {
			number = score;
			Integer forChangeToString = new Integer(number);
			String best = forChangeToString.toString();
      Formatter fileOut = new Formatter(new FileWriter("save.txt"));
      fileOut.format(best);
      fileOut.close();
		}
		return number;
	} catch(IOException e) {
		e.printStackTrace();
		return -1;
	}
}

public int Save() {
	return Save(0);
}

void endGame(Graphics g) {
	
  int best = Save(t); //save your Score
  
  // Create a message telling the player the game is over
  String message = "Your Score : " + t;
  String record = "Best Score : " + best;
  String info = "Create by Aryan Badiee.";
  
  // Create a new font instance
  Font font = new Font("Arial", Font.BOLD, 70);
  FontMetrics metrics = getFontMetrics(font);

  //set the font
  g.setFont(font);
  
  // Draw the message to the board
  //{
  //line 1
  g.setColor(Color.red);
  g.drawString(message, (BOARDWIDTH - metrics.stringWidth(message)) / 2,BOARDHEIGHT /5);
  
  //line 2
  g.setColor(Color.yellow);
  g.drawString(record, (BOARDWIDTH - metrics.stringWidth(message)) / 2,BOARDHEIGHT /3);
  
  //line 3
  g.setColor(Color.green);
  g.drawString(info, (BOARDWIDTH - metrics.stringWidth(info)) / 2,BOARDHEIGHT /2);
  //}
  
  //finish
  System.out.println("Game Ended");

}

//Run constantly as long as we're in game.
@Override
public void actionPerformed(ActionEvent e) {
  if (inGame == true) {

      checkFoodCollisions();
      checkCollisions();
      snake.move();

      System.out.println(snake.getSnakeX(0) + " " + snake.getSnakeY(0)
              + " " + food.getFoodX() + ", " + food.getFoodY());
  }
  // Repaint or 'render' our screen
  repaint();
}

private class Keys extends KeyAdapter {
@Override
public void keyPressed(KeyEvent e) {
  int key = e.getKeyCode();
      
  if (((key == KeyEvent.VK_A) || (key == KeyEvent.VK_LEFT)) && (!snake.isMovingRight())) {
    snake.setMovingLeft(true);
    snake.setMovingUp(false);
    snake.setMovingDown(false);
  }

  if (((key == KeyEvent.VK_D) || (key == KeyEvent.VK_RIGHT)) && (!snake.isMovingLeft())) {
    snake.setMovingRight(true);
    snake.setMovingUp(false);
    snake.setMovingDown(false);
  }

  if (((key == KeyEvent.VK_W) || (key == KeyEvent.VK_UP)) && (!snake.isMovingDown())) {
    snake.setMovingUp(true);
    snake.setMovingRight(false);
    snake.setMovingLeft(false);
  }

  if (((key == KeyEvent.VK_S) || (key == KeyEvent.VK_DOWN)) && (!snake.isMovingUp())) {
    snake.setMovingDown(true);
    snake.setMovingRight(false);
    snake.setMovingLeft(false);
  }

  if ((key == KeyEvent.VK_ENTER) && (inGame == false)) {
    inGame = true;
    snake.setMovingDown(false);
    snake.setMovingRight(false);
    snake.setMovingLeft(false);
    snake.setMovingUp(false);
    initializeGame();
  }
}
}

private boolean proximity(int a, int b, int closeness) {
  return Math.abs((long) a - b) <= closeness;
}

public static int getAllDots() {
  return TOTALPIXELS;
}

public static int getDotSize() {
  return PIXELSIZE;
}
}
