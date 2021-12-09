package co.edu.unal.reto_5;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class TicTacToeGame {


    public enum DifficultyLevel {Easy, Harder, Expert};

    public DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public char mBoard[] = {'1','2','3','4','5','6','7','8','9'};

    public static final int BOARD_SIZE = 9;
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public TicTacToeGame() {
//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d("PEPE", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("PEPE2", "Failed to read value.", error.toException());
//            }
//        });

        mRand = new Random();
    }



    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }

        return move;
    }

    public int getRandomMove(){
        int move;

        do{
            move = mRand.nextInt(BOARD_SIZE);
        }
        while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        return move;
    }

    public int getWinningMove(){
        int move = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) move = i;
                mBoard[i] = curr;
            }
        }
        return move;
    }

    public int getBlockingMove(){
        int move = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) move = i;
                mBoard[i] = curr;
            }
        }
        return move;
    }



    public boolean setMove(char player, int location){
        if (mBoard[location] == OPEN_SPOT){
            mBoard[location] = player;
            return true;
        }
        return false;
    };

    public void clearBoard(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    };

    char getBoardOccupant(int i){
        return mBoard[i];
    }


    public DifficultyLevel getmDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setmDifficultyLevel(DifficultyLevel mDifficultyLevel) {
        this.mDifficultyLevel = mDifficultyLevel;
    }


    public char[] getmBoard() {
        return mBoard;
    }

    public void setmBoard(char[] mBoard) {
        this.mBoard = mBoard;
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public static char getHumanPlayer() {
        return HUMAN_PLAYER;
    }

    public static char getComputerPlayer() {
        return COMPUTER_PLAYER;
    }

    public static char getOpenSpot() {
        return OPEN_SPOT;
    }

    public Random getmRand() {
        return mRand;
    }

    public void setmRand(Random mRand) {
        this.mRand = mRand;
    }
}