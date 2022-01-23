package co.edu.unal.reto_10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;

    RequestQueue queue;
    String url_base;
    String url_params;

    ArrayList<JSONObject> arraylist = new ArrayList<JSONObject>();
    String filterField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listview);
        editsearch = (SearchView) findViewById(R.id.search);

        queue = Volley.newRequestQueue(this);
        url_base = "https://www.datos.gov.co/resource/xdk5-pm3f.json";

        updateData(url_base, this);

        filterField = "municipio";


    }


    public void updateData(String url, Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray municipios = new JSONArray(response);
                            for(int x = 0; x < municipios.length(); x++)
                                arraylist.add(municipios.getJSONObject(x));

                            adapter = new ListViewAdapter(context, arraylist);
                            list.setAdapter(adapter);

                            editsearch = (SearchView) findViewById(R.id.search);
                            editsearch.setOnQueryTextListener((SearchView.OnQueryTextListener) context);

                        } catch (JSONException e) {}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }
        );

        queue.add(stringRequest);
    }





    @Override
    public boolean onQueryTextSubmit(String query) {
        editsearch = (SearchView) findViewById(R.id.search);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text, filterField);
        return false;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_name:
                if (checked){
                    filterField = "municipio";
                    url_params = "https://www.datos.gov.co/resource/xdk5-pm3f.json?municipio=";
                    break;
                }


            case R.id.radio_departamento:
                if (checked){
                    filterField = "departamento";
                    url_params = "https://www.datos.gov.co/resource/xdk5-pm3f.json?departamento=";
                    break;
                }

            case R.id.radio_region:
                if (checked){
                    filterField = "region";
                    url_params = "https://www.datos.gov.co/resource/xdk5-pm3f.json?region=";
                    break;
                }

            case R.id.radio_code:
                if (checked){
                    filterField = "c_digo_dane_del_municipio";
                    url_params = "https://www.datos.gov.co/resource/xdk5-pm3f.json?c_digo_dane_del_municipio=";
                    break;
                }
        }
    }




}