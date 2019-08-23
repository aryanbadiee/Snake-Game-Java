package snake;

public class Food {

	private Snake snake = new Snake();
	private int foodX; // Stores X pos of our food
	private int foodY; // Stores Y pos of our food

	// Used to determine random position of food
	private final int RANDOMPOSITION = 50;
	public void createFood() {

		// Set our food's x & y position to a random position

		int location = (int) (Math.random() * RANDOMPOSITION);
		foodX = ((location * Board.getDotSize()));
		if(foodX > 1500) {
			foodX = 1500;
		}
		location = (int) (Math.random() * RANDOMPOSITION);
		foodY = ((location * Board.getDotSize()));
		if(foodY > 800) {
			foodY = 800;
		}
		if ((foodX == snake.getSnakeX(0)) && (foodY == snake.getSnakeY(0))) {
			createFood();
		}
	}

	public int getFoodX() {
		return foodX;
	}

	public int getFoodY() {
		return foodY;
	}
}
