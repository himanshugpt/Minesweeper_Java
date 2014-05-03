/**
 * 
 */
package com.game.minesweeper;

import com.game.minesweeper.data.MinesweeperData;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * @author Himanshu
 * 
 */
public class Userid extends Activity implements OnClickListener {

	private EditText textInput;
	MinesweeperData minesweeperData;
	String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userid);
		View strtGameButton = findViewById(R.id.StartGameButton);
		strtGameButton.setOnClickListener(this);
		textInput = (EditText) findViewById(R.id.EditText01);
		textInput.setText("");
		minesweeperData = new MinesweeperData(this);
	}

	private boolean validateInput() {
		userId = textInput.getText().toString();
		if (userId == "")
			return false;

		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.StartGameButton:
			if (validateInput()) {
				if (!minesweeperData.doesUserIdPresent(userId))
					insertId(userId);
				Intent mine = new Intent(this, Minesweeper.class);
				startActivity(mine);
			}

			break;
		}
	}

	private void insertId(String uid) {
		minesweeperData = new MinesweeperData(this);
		SQLiteDatabase db = minesweeperData.getWritableDatabase();
		try {
			minesweeperData.AddUserId(uid, db);
		} catch (Exception e) {
			Log.d("Minesweeper", "inside catch");
		}
	}
}
