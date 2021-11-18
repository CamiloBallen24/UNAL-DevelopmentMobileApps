package co.edu.unal.reto_5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private Boolean winnerFlag;
    private Boolean androidTurn;

    private Integer counterComputer = 0;
    private Integer counterPlayer = 0;
    private Integer counterTie = 0;

    private TextView mInfoTextView;
    private TextView mCounterTextView;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private BoardView mBoardView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mCounterTextView = (TextView) findViewById(R.id.counter);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};


                int selected = 2;
                builder.setSingleChoiceItems(levels, selected,
                        (dialog1, item) -> {
                            dialog1.dismiss();

                            switch (item){
                                case 0:
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                    break;
                                case 1:
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                    break;
                                case 2:
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                    break;
                            }

                            Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                        });
                dialog = builder.create();

                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {MainActivity.this.finish(); }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;

            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
    }

    private void startNewGame() {
        mGame.clearBoard();
        this.winnerFlag = false;
        this.androidTurn = false;
        mBoardView.invalidate();
        mInfoTextView.setText(R.string.first_human);

    }

    void updateScoreBoard(){
        int winner = mGame.checkForWinner();
        if (winner == 1){
            mInfoTextView.setText(R.string.result_tie);
            winnerFlag = true;
            counterTie++;
            mCounterTextView.setText("Player:"+ counterPlayer  + "  Ties:" + counterTie + "  Android:" + counterComputer);
        }

        else if (winner == 2){
            mInfoTextView.setText(R.string.result_human_wins);
            winnerFlag = true;
            counterPlayer++;
            mCounterTextView.setText("Player:"+ counterPlayer  + "  Ties:" + counterTie + "  Android:" + counterComputer);
        }
        else{
            mInfoTextView.setText(R.string.result_computer_wins);
            winnerFlag = true;
            counterComputer++;
            mCounterTextView.setText("Player:"+ counterPlayer  + "  Ties:" + counterTie + "  Android:" + counterComputer);
        }
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!winnerFlag && !androidTurn && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){

                Handler handler = new Handler();

                // If no winner yet, let the computer make a move
                if (mGame.checkForWinner() == 0){
                    mInfoTextView.setText(R.string.turn_computer);

                    androidTurn = true;
                    handler.postDelayed(() -> {
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);

                        // If no winner yet, humman continue
                        if (mGame.checkForWinner() == 0)
                            mInfoTextView.setText(R.string.turn_human);
                        else
                            updateScoreBoard();

                        androidTurn = false;
                    }, 2000);
                }
                else{
                    updateScoreBoard();
                    return false;
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private boolean setMove(char player, int location) {
        if(player == mGame.HUMAN_PLAYER){
            mHumanMediaPlayer.start();
        }
        if(player == mGame.COMPUTER_PLAYER){
            mComputerMediaPlayer.start();
        }

        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();
            return true;
        }
        return false;
    }
}