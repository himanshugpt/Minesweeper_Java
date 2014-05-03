package com.game.minesweeper.view;

/**
 * Class to keep the bomb location.
 * @author Himanshu
 */
public class Location {
	public int row, column;

	public Location(int rowIndex, int columnIndex) {
		this.row = rowIndex;
		this.column = columnIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Location loc = (Location) obj;
		if (loc.row == this.row && loc.column == this.column)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return row + column * 17;
	}

	@Override
	public String toString() {
		return "row " + row + " column " + column;
	}
}