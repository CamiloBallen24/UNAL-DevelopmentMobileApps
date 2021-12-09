package co.edu.unal.reto_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.Date;
import java.util.Map;



public class SalasActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private LinearLayout listView;
    private EditText salaName;
    private Button createSala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        listView = (LinearLayout) findViewById(R.id.salas_list);
        salaName = (EditText) findViewById(R.id.sala_name);
        createSala = (Button) findViewById(R.id.create_sala);
        updateSalas(this);
        createSala(this);
    }

    public void updateSalas(Context context){
        listView.removeAllViewsInLayout();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView.removeAllViews();
                Iterable<DataSnapshot> salas = dataSnapshot.getChildren();
                for(DataSnapshot sala: salas){
                    try {
                        JSONObject sala_info = new JSONObject(sala.getValue().toString());
                        Button button = new Button(context);
                        button.setText(sala_info.getString("nombre"));

                        button.setTextColor(Color.WHITE);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 30, 0, 0);
                        button.setLayoutParams(params);

                        switch(sala_info.getInt("players")){
                            case 0:
                                button.setBackgroundColor(Color.parseColor("#28a745"));
                                break;
                            case 1:
                                button.setBackgroundColor(Color.parseColor("#007bff"));
                                break;
                            case 2:
                                button.setBackgroundColor(Color.parseColor("#dc3545"));
                                break;
                        }

                        if(sala_info.getInt("players")<2) {
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(context, GameOnline.class);
                                    try {
                                        i.putExtra("id", sala_info.getString("id"));
                                    } catch (JSONException e) {
                                    }
                                    startActivity(i);
                                }
                            });
                        }
                        if(sala_info.getInt("players") != -1)
                            listView.addView(button);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void createSala(Context context){
        this.createSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_sala = (new Date()).toString();

                JSONObject new_sala = new JSONObject();
                try {
                    new_sala.put("id", id_sala);
                    new_sala.put("nombre", salaName.getText().toString());
                    new_sala.put("players", 0);
                    new_sala.put("turno", 1);
                    new_sala.put("tablero", "0-0-0-0-0-0-0-0-0");
                    new_sala.put("player-1", 0);
                    new_sala.put("player-2", 0);
                    new_sala.put("ties", 0);
                } catch (JSONException e) {}
                salaName.setText("");
                mDatabase.child(id_sala).setValue(new_sala.toString());
            }
        });
    }
}