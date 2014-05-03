package game.minesweeper.core;

import game.minesweeper.core.Square.SquareState;

import java.util.HashSet;
import java.util.Random;

import com.game.minesweeper.view.Location;

import android.util.Log;

public class Grid {
	
	public static final String TAG = "MinesweeperGrid";

	public enum DifficultLevel {
		BEGINNER(10), INTERMEDIATE(20), EXPERT(40), DEFAULT(15);

		private int mineRatio;

		private DifficultLevel(int c) {
			mineRatio = c;
		}

		public int getDifficultyRatio() {
			return mineRatio;
		}
	}

	// represents n of n*n matrix
	private int gridSize;

	// Represents the squares in the grid
	private Square[][] squares;

	// Represents the difficulty level;
	private DifficultLevel difficultyLevel;

	// Used to generate the random numbers.
	Random generator = new Random();

	HashSet<Location> bombLocationMap = new HashSet<Location>();

	// Number of bombs in the grid
	int noOfBombs;

	public int getNoOfBombs() {
		return noOfBombs;
	}

	// Constructor which initializes the
	// squares array to the given param n value.
	public Grid(int n) {
		this.gridSize = n;
		squares = new Square[n][n];
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Square GetSquare(int x, int y){
		return this.squares[x][y];
	}

	public boolean initializeGrid(final int row, final int column,
			DifficultLevel diffLevel) {
		this.difficultyLevel = diffLevel;
		populateGridSquares();
		noOfBombs = (int) (((squares.length * squares.length) * difficultyLevel.mineRatio) / 100);
		Log.d(TAG,noOfBombs + " <-- number of bombs..");
		Log.d(TAG,"User touched at location :: row :: column " + row
				+ " " + column);
		deployBombs(row, column);
		return false;
	}

	/**
	 * Method to find the position as where to deploy bombs and set the state of
	 * the squares/mines as bombs.
	 * 
	 * @param row: the user selected row index column: the user selected column
	 *        index
	 */
	private void deployBombs(final int row, final int column) {
		int randRowIndex = 0, randColIndex = 0;
		boolean flag = true;
		for (int i = 0; i < noOfBombs; i++) {
			while (flag) {
				randRowIndex = generator.nextInt(squares.length);
				randColIndex = generator.nextInt(squares.length);
				if (doesSatisfyRange(randRowIndex, randColIndex, row, column)
						&& (bombLocationMap.add(new Location(randRowIndex,
								randColIndex)))) {
					// flag = false;
					break;
				}
			}
			bombLocationMap.add(new Location(randRowIndex, randColIndex));
			squares[randRowIndex][randColIndex] = new Square(true,
					SquareState.BOMB);
			squares[randRowIndex][randColIndex].setHasBomb(true);
			populateOctate(randRowIndex, randColIndex);
			Log.d(TAG,"Deploying bombs at:: " + randRowIndex + " "
					+ randColIndex);
		}

	}

	private int[] GetOctate(int row, int gridSize) {
		int[] rowArr;
		if (row != 0 && row != (gridSize - 1))
			rowArr = new int[] { row - 1, row, row + 1 };
		else if (row == 0)
			rowArr = new int[] { row, row + 1 };
		else
			rowArr = new int[] { row - 1, row };

		return rowArr;
	}

	/**
	 * Method to populate the Octate
	 * @param row
	 * @param column
	 */
	private void populateOctate(int row, int column) {
		int[] rowArr = null, colArr = null;
		rowArr = GetOctate(row,gridSize);
		colArr = GetOctate(column, gridSize);
		for (int i = 0; i < rowArr.length; i++)
			for (int j = 0; j < colArr.length; j++) {
				if (squares[rowArr[i]][colArr[j]] == null)
					squares[rowArr[i]][colArr[j]] = new Square();
				if (squares[rowArr[i]][colArr[j]].getStatus() != SquareState.BOMB)
					squares[rowArr[i]][colArr[j]].count++;
				Log.d(TAG,"Neighbor bomb count:: [" + rowArr[i]
						+ "] [" + colArr[j] + "] "
						+ squares[rowArr[i]][colArr[j]].count);
			}
	}

	/**
	 * Checks the range of the generated row and column indexes. ***** LOGIC
	 * ******* We dont want to put bombs in the near locations of the location
	 * where user clicked.
	 * 
	 * @param randRowIndex
	 * @param randColIndex
	 * @param row
	 *            -- user clicked row index
	 * @param column
	 *            -- user clicked column index
	 * 
	 * @return true if able to compute such location else false.
	 */
	private boolean doesSatisfyRange(int randRowIndex, int randColIndex,
			int row, int column) {
		boolean statisfyRowCondition = false, satisfyColIndex = false;
		int[] rowArr, colArr;

		// checks if the generated value is equal to the clicked value
		if (randRowIndex == row && randColIndex == column)
			return false;

		if (randRowIndex == (gridSize - 1) && randColIndex == (gridSize - 1))
			return false;

		if (row != 0)
			rowArr = new int[] { row - 1, row, row + 1 };
		else
			rowArr = new int[] { row, row + 1 };

		if (column != 0)
			colArr = new int[] { column - 1, column, column + 1 };
		else
			colArr = new int[] { column, column + 1 };

		// checks that the generated value is from the
		// clicked matrix
		for (int i = 0; i < rowArr.length; i++)
			for (int j = 0; j < colArr.length; j++) {
				if (rowArr[i] == randRowIndex && colArr[j] == randColIndex)
					return false;
			}
		return true;
	}

	/**
	 * Method to explore the location.
	 * @param row
	 * @param column
	 */
	public void exploreLocation(int row, int column) {
		int[] rowArr = null, colArr = null;
		//GetOctate(row, column, rowArr, colArr);
		rowArr = GetOctate(row,gridSize);
		colArr = GetOctate(column, gridSize);
		for (int i = 0; i < rowArr.length; i++) {
			for (int j = 0; j < colArr.length; j++) {
				Log.d(TAG,"i: " + i + " j: " + j + "  ");
				if(null == squares[i][j]){
					squares[i][j] = new Square();
					
					//System.out.println("square intilaized...");
				}
				
				Log.d(TAG,squares[i][j].count+""  );
			}
		}
	}

	private void populateGridSquares() {
		if (null != squares && squares.length > 0) {
			for (int i = 0; i < squares.length; i++) {
				for (int j = 0; j < squares.length; j++) {
					if (null != squares[i][j]) {
						Square sq = new Square();
						// determineSquareState(sq);
						squares[i][j] = sq;
					}
				}
			}

		} else {
			System.out.println("Please intilaise grid first...");
		}
	}

	
}
