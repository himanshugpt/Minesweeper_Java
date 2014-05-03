package com.game.minesweeper;

import com.game.minesweeper.data.MinesweeperData;
import com.game.minesweeper.view.MinesweeperView;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Minesweeper extends Activity{

	private MinesweeperData minesweeperData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View minesweeperView = new MinesweeperView(this);
		setContentView(minesweeperView);
		setTitle("Minesweeper Game");
		
		
	}
}
