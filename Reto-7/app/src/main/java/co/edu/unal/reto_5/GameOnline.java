package co.edu.unal.reto_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GameOnline extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private BoardViewOnline mBoardViewOnline;
    private TextView information;
    private TextView counter;
    private Button boton;

    private String id;
    private int num_player;
    private JSONObject sala_info;
    private boolean winnerFlag = false;
    private boolean initFlag = false;
    private Context context;
    private boolean winner_process = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_online);

        Bundle bundle = getIntent().getExtras();
        this.id= bundle.getString("id");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBoardViewOnline = (BoardViewOnline) findViewById(R.id.boardOnline);
        mBoardViewOnline.setOnTouchListener(mTouchListener);
        boton = (Button) findViewById(R.id.nuevoJuegoOnline);

        information = (TextView) findViewById(R.id.informationOnline);
        counter = (TextView) findViewById(R.id.counterOnline);




        initSala(this);
        connetToChanges(this);
        nuevoJuego(this);
        context = this;

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            sala_info.put("players", -1);
            updateSala(context);
        } catch (JSONException e) {}
    }


    private void initSala(Context context){
        mDatabase.child(this.id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                try {
                    sala_info = new JSONObject(task.getResult().getValue().toString());
                    num_player = sala_info.getInt("players") + 1;
                    sala_info.put("players", num_player);
                    initFlag = (num_player==2);

                    if(!initFlag)
                        information.setText("Esperando al otro player");
                    else
                        information.setText("Turno del rival");

                    mBoardViewOnline.setGame(sala_info.getString("tablero").split("-"));
                    updateSala(context);
                } catch (JSONException e) {}
            }
        });
    }

    private void updateSala(Context context){
        mDatabase.child(this.id).setValue(this.sala_info.toString());
    }

    private void connetToChanges(Context context){
        mDatabase.child(this.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    sala_info = new JSONObject(snapshot.getValue().toString());
                    String[] tablero = sala_info.getString("tablero").split("-");
                    if(sala_info.getInt("players") == -1){
                        finish();
                        return;
                    }

                    initFlag = (sala_info.getInt("players")==2);


                    mBoardViewOnline.setGame(tablero);
                    mBoardViewOnline.invalidate();

                    winnerFlag = (checkForWinner(tablero) != 3);
                    if(winnerFlag) {
                        processWinner(checkForWinner(tablero));
                        return;
                    }

                    if(sala_info.getInt("turno") == num_player)
                        information.setText("Tu Turno");
                    else
                        information.setText("Turno del Rival");

                    if(!initFlag)
                        information.setText("Esperando al otro player");


                } catch (JSONException e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardViewOnline.getBoardCellWidth();
            int row = (int) event.getY() / mBoardViewOnline.getBoardCellHeight();
            int pos = row * 3 + col;

            int actual_turno = 0;
            String[] tablero = {};
            try {
                actual_turno =  sala_info.getInt("turno");
                tablero = sala_info.getString("tablero").split("-");
            }
            catch (JSONException e) {}

            if (!winnerFlag && initFlag && num_player == actual_turno){

                if (tablero[pos].equals("0") == true){
                    tablero[pos] = String.valueOf(num_player);
                    mBoardViewOnline.setGame(tablero);
                    try {
                        sala_info.put("tablero", tablero_to_string(tablero));
                        sala_info.put("turno", (actual_turno % 2) + 1);
                    }
                    catch (JSONException e) {}

                }

                mBoardViewOnline.invalidate();
                updateSala(context);
                updateScore();
                winnerFlag = (checkForWinner(tablero) != 3);
                if(winnerFlag) processWinner(checkForWinner(tablero));
            }
            return false;
        }
    };

    public int checkForWinner(String[] tablero) {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (tablero[i].equals("1") && tablero[i+1].equals("1") && tablero[i+2].equals("1"))
                return 1;
            if (tablero[i].equals("2") && tablero[i+1].equals("2") && tablero[i+2].equals("2"))
                return 2;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (tablero[i].equals("1") && tablero[i+3].equals("1") && tablero[i+6].equals("1"))
                return 1;
            if (tablero[i].equals("2") && tablero[i+3].equals("2") && tablero[i+6].equals("2"))
                return 2;
        }

        // Check for diagonal wins
        if (
                (tablero[0].equals("1") && tablero[4].equals("1") && tablero[8].equals("1")) ||
                (tablero[2].equals("1") && tablero[4].equals("1") && tablero[6].equals("1"))
        )
            return 1;
        if (
                (tablero[0].equals("2") && tablero[4].equals("2") && tablero[8].equals("2")) ||
                (tablero[2].equals("2") && tablero[4].equals("2") && tablero[6].equals("2"))
        )
            return 2;

        // Check for tie
        for (int i = 0; i < 9; i++) {
            // If we find a number, then no one has won yet
            if (tablero[i].equals("0"))
                return 3;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 0;
    }

    public void processWinner(int winner){
        if(winner_process) return;;
        winner_process = !winner_process;

        if(winner == 0)
            information.setText("Empate");
        else if(winner == num_player)
            information.setText("Ganaste");
        else
            information.setText("Perdiste");

        if(winner == 0)
            try {sala_info.put("ties", sala_info.getInt("ties") + 1);} catch (JSONException e) {}
        else if(winner == 1)
            try {sala_info.put("player-1", sala_info.getInt("player-1") + 1);} catch (JSONException e) {}
        else
            try {sala_info.put("player-2", sala_info.getInt("player-2") + 1);} catch (JSONException e) {}

        updateScore();
        updateSala(this.context);

    }

    private String tablero_to_string(String[] tablero){
        String mystring = tablero[0];
        for (int i=1; i<tablero.length; i++){
            mystring = mystring + "-" + tablero[i];
        }
        return mystring;
    }

    private void updateScore(){
        int player1 = 0;
        int player2 = 0;
        int ties = 0;

        try {
            player1 = sala_info.getInt("player-1");
            player2 = sala_info.getInt("player-2");
            ties = sala_info.getInt("ties");
        } catch (JSONException e) {}

        if(num_player==1)
            counter.setText(player1 + " :Tu \n" + ties + " :Ties \n" + player2 + " :Rival\n");
        else
            counter.setText(player2 + " :Tu \n" + ties + " :Ties \n" + player1 + " :Rival\n");
    };

    private void nuevoJuego(Context context){
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!winnerFlag) return;
                else {
                    winnerFlag = false;
                    winner_process = false;
                    try {
                        sala_info.put("tablero", "0-0-0-0-0-0-0-0-0");
                        updateSala(context);
                    } catch (JSONException e) {
                    }
                }
            }
        });
    }



}