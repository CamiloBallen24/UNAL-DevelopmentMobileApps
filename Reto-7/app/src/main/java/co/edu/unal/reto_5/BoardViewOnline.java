package co.edu.unal.reto_5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BoardViewOnline extends View {

    public static final int GRID_WIDTH = 6;

    private Bitmap mPlayer1Bitmap;
    private Bitmap mPlayer2Bitmap;

    private Paint mPaint;
    public String[] mGame = {"0","0","0","0","0","0","0","0","0"};

    public BoardViewOnline(Context context) {
        super(context);
        initialize();
    }
    public BoardViewOnline(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }
    public BoardViewOnline(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(String[] game) {
        mGame = game;
    }

    public void initialize() {
        mPlayer1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mPlayer2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String a = "";
        for (int i=0; i<mGame.length; i++){
            a = a + "-" + mGame[i];
        }
        Log.d("PEPEA", a);

        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        // Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);

        // Draw the two vertical board lines
        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);

        // Draw the two horizontal board lines
        canvas.drawLine(0, cellWidth, boardWidth, cellWidth, mPaint);
        canvas.drawLine(0, cellWidth*2, boardWidth, cellWidth*2, mPaint);

        // Draw all the X and O images
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int left = (col*cellWidth) + (col+GRID_WIDTH);
            int top = (row*cellWidth) + (row+GRID_WIDTH);
            int right = ((col+1)*cellWidth) - ((col+1)*GRID_WIDTH);
            int bottom = ((row+1)*cellWidth) - ((row+1)*GRID_WIDTH);

            if (mGame != null && mGame[i].equals("1") == true)
                canvas.drawBitmap(mPlayer1Bitmap, null, new Rect(left, top, right, bottom), null);


            else if (mGame != null && mGame[i].equals("2") == true)
                canvas.drawBitmap(mPlayer2Bitmap, null, new Rect(left, top, right, bottom), null);

        }


    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }
    public int getBoardCellHeight() {
        return getHeight() / 3;
    }


}
