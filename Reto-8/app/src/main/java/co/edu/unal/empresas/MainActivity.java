package co.edu.unal.empresas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    ArrayList<Empresa> arraylist = new ArrayList<Empresa>();

    Button createButtonDialog;
    Button createButton;


    EmpresasRepository myRepository;

    CheckBox consultoria;
    CheckBox desarrollo;
    CheckBox fabrica;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listview);
        createButtonDialog = (Button) findViewById(R.id.create_button_dialog);

        consultoria = (CheckBox) findViewById(R.id.consultoria);
        consultoria.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {updateListEmpresas();}
        });

        desarrollo = (CheckBox) findViewById(R.id.desarrollo);
        desarrollo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {updateListEmpresas();}
        });

        fabrica = (CheckBox) findViewById(R.id.fabrica);
        fabrica.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {updateListEmpresas();}
        });

        this.myRepository =  new EmpresasRepository(this);
        this.updateListEmpresas();

        this.createEmpresa(this);

    }

    public ArrayList<Empresa> getEmpresas(){
        arraylist =  myRepository.getEmpresas();

        ArrayList<Empresa> empresasFiltradas = new ArrayList<>();


        if (!consultoria.isChecked() && !desarrollo.isChecked() && !fabrica.isChecked())
            return arraylist;

        for(int i=0; i<arraylist.size(); i++){
            Log.d("Calcul", arraylist.get(i).getClassification());

            if(arraylist.get(i).getClassification().equals("Consultoria") && consultoria.isChecked())
                empresasFiltradas.add(arraylist.get(i));

            if(arraylist.get(i).getClassification().equals("Desarrollo") && desarrollo.isChecked())
                empresasFiltradas.add(arraylist.get(i));

            if(arraylist.get(i).getClassification().equals("Fabrica") && fabrica.isChecked())
                empresasFiltradas.add(arraylist.get(i));
        }
        return  empresasFiltradas;

    }



    public void createEmpresa(Context context){
        createButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.create, viewGroup, false);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText editTextName =  (EditText) dialogView.findViewById(R.id.create_name);
                EditText editTextUrl =  (EditText) dialogView.findViewById(R.id.create_url);
                EditText editTextPhone =  (EditText) dialogView.findViewById(R.id.create_phone);
                EditText editTextEmail =  (EditText) dialogView.findViewById(R.id.create_email);
                EditText editTextServicio =  (EditText) dialogView.findViewById(R.id.create_servicio);

                //Spinner
                String[] items = new String[]{"Consultoria", "Desarrollo", "Fabrica"};
                Spinner  spinner = (Spinner)dialogView.findViewById(R.id.create_classificacion);
                ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                createButton = (Button) dialogView.findViewById(R.id.create_button);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Empresa newEmpresa = new Empresa(
                                0,
                                editTextName.getText().toString(),
                                editTextUrl.getText().toString(),
                                editTextPhone.getText().toString(),
                                editTextEmail.getText().toString(),
                                editTextServicio.getText().toString(),
                                spinner.getSelectedItem().toString());
                        myRepository.createEmpresa(newEmpresa);
                        updateListEmpresas();
                        alertDialog.dismiss();

                    }
                });
            }
        });
    }


    public void updateListEmpresas(){
        arraylist =  this.getEmpresas();

        adapter = new ListViewAdapter(this, arraylist, myRepository);
        list.setAdapter(adapter);

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }


}