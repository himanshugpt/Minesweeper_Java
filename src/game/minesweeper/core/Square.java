package game.minesweeper.core;

public class Square {

	public enum SquareState {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NOTCHECKED, FLAG, QUESTION, BOMB
	}

	private boolean hasBomb;
	private SquareState status;
	public int count;

	public Square(boolean hasBomb, SquareState stat) {
		this.status = stat;
	}

	public Square() {

	}

	/**
	 * @return the hadBomb
	 */
	public boolean isHasBomb() {
		return hasBomb;
	}

	/**
	 * @param hadBomb
	 *            the hadBomb to set
	 */
	public void setHasBomb(boolean hasBomb) {
		this.hasBomb = hasBomb;
	}

	/**
	 * @return the status
	 */
	public SquareState getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(SquareState status) {
		this.status = status;
	}

	/*
	 * @Override public boolean equals(Object obj){
	 * 
	 * return hadBomb;
	 * 
	 * }
	 */
}
