package com.game.minesweeper.view;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import game.minesweeper.core.Grid;
import game.minesweeper.core.Square;
import game.minesweeper.core.Grid.DifficultLevel;

import com.game.minesweeper.Minesweeper;
import com.game.minesweeper.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class MinesweeperView extends View implements
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	public static final String TAG = "MinesweeperView";
	GestureDetector gestureDetector;
	Minesweeper minesweeper;
	Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
	FontMetrics fm = foreground.getFontMetrics();
	Paint selected; // selected rect color
	Map<Rect, Location> selectedRects = new HashMap<Rect, Location>();
	Map<Rect, Location> bombCells = new HashMap<Rect, Location>();
	Grid grid;

	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private final Rect selRect = new Rect();
	private boolean drawFlag = false;
	private boolean drawNumber = false;
	private boolean isFirstTouch = true;
	private boolean displayAlert = true;
	private boolean isGameOver = false;
	private boolean isGameWon = false;
	private long startTime = 0;
	private long endTime = 0;
	private long timeTaken;

	public MinesweeperView(Context context) {
		super(context);
		selected = new Paint();
		this.minesweeper = (Minesweeper) context;
		gestureDetector = new GestureDetector(context, this);
		gestureDetector.setOnDoubleTapListener(this);
		setFocusable(true);
		setFocusableInTouchMode(true);

		// Define color and style for numbers
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);

		grid = new Grid(9);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		Rect r = getRect(selX, selY);
		Log.d("inside onSizeChanged:: ", "onSizeChanged: width " + width
				+ ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private Rect getRect(int x, int y) {
		Rect rect = new Rect();
		rect.set((int) (x * width), (int) (y * height),
				(int) (x * width + width), (int) (y * height + height));
		return rect;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		PaintMatrix(canvas);

		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		
		Log.d("Minesweeper", "selRect=" + selRect);
		if (drawFlag) {
		
			canvas.drawText("F", selX * width + x, selY * height + y,
					foreground);
			selected.setColor(getResources().getColor(R.color.puzzle_hint_2));
			canvas.drawRect(getRect(selX, selY), selected);
			drawNumber = false;
		}

		Log.d(TAG, "Current Color of Selected Paint Object:: "
				+ new Integer(selected.getColor()).toString());

		if (isGameOver) {
			ShowWholeArena(canvas);
		}
		if(isGameWon){
			ShowWinAlert(canvas);
		}

		// paint selected rects
		PaintSelectedRects(canvas);
		drawFlag = false;
		drawNumber = false;

	}

	private void ShowWinAlert(Canvas canvas){
		endTime = Calendar.getInstance().getTimeInMillis();
		timeTaken = endTime - startTime;
		AlertDialog.Builder builder = new AlertDialog.Builder(minesweeper);
		builder.setMessage("Congratulations!! You WON. \n"+"Time Taken:: " + timeTaken/1000 + " seconds.")
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								 minesweeper.finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
		isGameWon = false;
	}
	
	private void PaintSelectedRects(Canvas canvas) {
		for (Rect rect : selectedRects.keySet()) {
			if (drawNumber) {
				drawFlag = false;
				Square sq = grid.GetSquare(selectedRects.get(rect).row,
						selectedRects.get(rect).column);
				if (sq == null) {
					sq = new Square();
					Log.d(TAG, " sq null");
				}

				if (sq.isHasBomb()) {
					canvas.drawText("*", rect.centerX(), rect.centerY(),
							foreground);
				} else {
					String count = new Integer(sq.count).toString();
					if (0 != sq.count)
						canvas.drawText(count, rect.centerX(), rect.centerY(),
								foreground);
					else {
						count = "";
						canvas.drawText(count, rect.centerX(), rect.centerY(),
								foreground);
					}
				}
			}

			canvas.drawRect(rect, selected);
		}
	}

	/**
	 * @param canvas
	 */
	private void PaintMatrix(Canvas canvas) {
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		// Draw the minor grid lines
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, light);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1,
					hilite);
			canvas.drawLine(i * width, 0, i * width, getHeight(), light);
			canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(),
					hilite);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("Minesweeper", "onKeyDown: keycode=" + keyCode + ", event="
				+ event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX - 1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX + 1, selY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	private void select(int x, int y) {
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		Log.d(TAG, "in Select:: " + selX + selY);
		Square sq = grid.GetSquare(selX, selY);
		Rect selectedRec = getRect(selX, selY);
		if (null != sq && sq.isHasBomb()) {
			invalidate(selectedRec);
			bombCells.put(selectedRec, new Location(selX, selY));
			Log.d(TAG, "bomb count" + bombCells.keySet().size());
			if (bombCells.keySet().size() == grid.getNoOfBombs()) {
				// game won
				isGameWon = true;
			}
			return;
		}
		invalidate(selectedRec);
		selectedRects.put(selectedRec, new Location(selX, selY));
	}


	public boolean onDown(MotionEvent e) {
		Log.d(TAG, "onDown");
		return false;
	}

	public boolean onFling(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {
		Log.d(TAG, "onFling");
		return false;
	}

	public void onLongPress(MotionEvent e) {
		Log.d(TAG, "onLongPress");
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d(TAG, "onScroll");
		return false;
	}

	public void onShowPress(MotionEvent e) {
		Log.d(TAG, "onShowPress");
	}

	public boolean onSingleTapUp(MotionEvent e) {
		Log.d(TAG, "onSingleTapUp");
		return false;
	}

	// implements GestureDetector.OnDoubleTapListener interface

	public boolean onDoubleTap(MotionEvent e) {
		Log.d(TAG, "onDoubleTap: x " + selX + ", y " + selY);
		return true;
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
	 * Used to uncover the cell.
	 */
	public boolean onDoubleTapEvent(MotionEvent e) {
		select((int) (e.getX() / width), (int) (e.getY() / height));
		selected.setColor(getResources().getColor(R.color.puzzle_light));
		Log.d(TAG, "onDoubleTapEvent: x " + selX + ", y " + selY);
		this.drawNumber = true;
		if (isFirstTouch) {
			startTime = Calendar.getInstance().getTimeInMillis();
			grid.initializeGrid(selX, selY, DifficultLevel.BEGINNER);
			int[] rowArr = GetOctate(selX, 9);
			int[] colArr = GetOctate(selY, 9);
			for (int i = 0; i < rowArr.length; i++) {
				for (int j = 0; j < colArr.length; j++) {
					select(rowArr[i], colArr[j]);
					Log.d(TAG, " Selecting  " + " i " + rowArr[i] + " j "
							+ colArr[j]);
				}
			}
			grid.exploreLocation(selX, selY);
			isFirstTouch = false;
		} else {
			Square sq = grid.GetSquare(selX, selY);
			if (sq != null && sq.isHasBomb()) {
				if (displayAlert) {
					// displayAlert = false;
					isGameOver = true;
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							select(i, j);
						}
					}
				}
			}
		}
		return true;
	}

	private void ShowWholeArena(Canvas canvas) {
		for (Rect r : bombCells.keySet()) {
			Paint p = new Paint();
			p.setColor(Color.CYAN);
			canvas.drawRect(r, p);
			canvas.drawText("*", r.centerX(), r.centerY(), foreground);
		}
		ShowGameOverDialog();
	}

	private void ShowGameOverDialog() {
		if (displayAlert) {
			AlertDialog.Builder builder = new AlertDialog.Builder(minesweeper);
			builder.setMessage("BOOM... Game Over.. !!! ")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// minesweeper.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			displayAlert = false;
		}
	}

	/**
	 * Used to flag the cell
	 */
	public boolean onSingleTapConfirmed(MotionEvent event) {
		select((int) (event.getX() / width), (int) (event.getY() / height));
		selected.setColor(getResources().getColor(R.color.puzzle_hint_2));
		this.drawFlag = true;
		Log.d(TAG, "onSingleTapConfirmed: x " + selX + ", y " + selY);
		return true;
	}
}
