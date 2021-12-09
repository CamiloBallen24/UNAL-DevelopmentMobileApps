package co.edu.unal.reto_5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    static final int DIALOG_RESET_ID = 3;
    static final int DIALOG_ONLINE= 4;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private BoardView mBoardView;

    private SharedPreferences mPrefs;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context =  this;
        mInfoTextView = (TextView) findViewById(R.id.information);
        mCounterTextView = (TextView) findViewById(R.id.counter);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        mBoardView.setOnTouchListener(mTouchListener);

        if (savedInstanceState == null) {
            startNewGame();
        }
        else {
            // Restore the game's state
            mGame.setmBoard(savedInstanceState.getCharArray("board"));
            winnerFlag = savedInstanceState.getBoolean("winnerFlag");
            androidTurn = savedInstanceState.getBoolean("androidTurn");

            counterPlayer = savedInstanceState.getInt("counterPlayer");
            counterComputer = savedInstanceState.getInt("counterComputer");
            counterTie = savedInstanceState.getInt("counterTie");

            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mCounterTextView.setText(savedInstanceState.getCharSequence("mCounterTextView"));

        }

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        counterPlayer = mPrefs.getInt("counterPlayer", 0);
        counterComputer = mPrefs.getInt("counterComputer", 0);
        counterTie = mPrefs.getInt("counterTie", 0);
        mCounterTextView.setText(counterPlayer + " :Player \n" + counterTie + " :Ties \n" + counterComputer + " :Android\n");

        Button online_play = (Button) findViewById(R.id.play_online);
        online_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalasActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("counterPlayer", counterPlayer);
        ed.putInt("counterComputer", counterComputer);
        ed.putInt("counterTie", counterTie);
        ed.commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setmBoard(savedInstanceState.getCharArray("board"));
        winnerFlag = savedInstanceState.getBoolean("winnerFlag");
        androidTurn = savedInstanceState.getBoolean("androidTurn");

        counterPlayer = savedInstanceState.getInt("counterPlayer");
        counterComputer = savedInstanceState.getInt("counterComputer");
        counterTie = savedInstanceState.getInt("counterTie");

        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mCounterTextView.setText(savedInstanceState.getCharSequence("mCounterTextView"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getmBoard());

        outState.putBoolean("winnerFlag", winnerFlag);
        outState.putBoolean("androidTurn", androidTurn);

        outState.putInt("counterPlayer", Integer.valueOf(counterPlayer));
        outState.putInt("counterComputer", Integer.valueOf(counterComputer));
        outState.putInt("counterTie", Integer.valueOf(counterTie));

        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putCharSequence("mCounterTextView", mCounterTextView.getText());
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
            case R.id.reset_score:
                showDialog(DIALOG_RESET_ID);
                return true;
            case R.id.online:
                showDialog(DIALOG_ONLINE);
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
                                    startNewGame();
                                    break;
                                case 1:
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                    startNewGame();
                                    break;
                                case 2:
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                    startNewGame();
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

            case DIALOG_RESET_ID:
                counterComputer = 0;
                counterPlayer = 0;
                counterTie = 0;
                mCounterTextView.setText(counterPlayer + " :Player \n" + counterTie + " :Ties \n" + counterComputer + " :Android\n");
                break;

            case DIALOG_ONLINE:
                Intent i = new Intent(this, SalasActivity.class);
                startActivity(i);
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
            mCounterTextView.setText(counterPlayer + " :Player \n" + counterTie + " :Ties \n" + counterComputer + " :Android\n");
        }

        else if (winner == 2){
            mInfoTextView.setText(R.string.result_human_wins);
            winnerFlag = true;
            counterPlayer++;
            mCounterTextView.setText(counterPlayer + " :Player \n" + counterTie + " :Ties \n" + counterComputer + " :Android\n");
        }
        else{
            mInfoTextView.setText(R.string.result_computer_wins);
            winnerFlag = true;
            counterComputer++;
            mCounterTextView.setText(counterPlayer + " :Player \n" + counterTie + " :Ties \n" + counterComputer + " :Android\n");
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
                    }, 750);
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
//        if(player == mGame.HUMAN_PLAYER){
//            mHumanMediaPlayer.start();
//        }
//        if(player == mGame.COMPUTER_PLAYER){
//            mComputerMediaPlayer.start();
//        }

        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();
            return true;
        }
        return false;
    }
}