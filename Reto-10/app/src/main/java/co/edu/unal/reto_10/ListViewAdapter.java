package co.edu.unal.reto_10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<JSONObject> municipios;
    private ArrayList<JSONObject> arraylist;

    public ListViewAdapter(Context context, List<JSONObject> municipios) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);

        this.municipios = municipios;
        this.arraylist = new ArrayList<JSONObject>();
        this.arraylist.addAll(municipios);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return municipios.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return municipios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_list_view_items, null);
            holder = new ViewHolder();

            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);

            Button viewButton = (Button) view.findViewById(R.id.view_mun);
            viewButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                      ViewGroup viewGroup = v.findViewById(android.R.id.content);
                      View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.view_municipio, viewGroup, false);
                      builder.setView(dialogView);

                      AlertDialog alertDialog = builder.create();
                      alertDialog.show();

                      try {

                        TextView municipio_name = (TextView) dialogView.findViewById(R.id.municipio_name);
                        municipio_name.setText(municipios.get(position).getString("municipio"));


                        TextView municipio_code = (TextView) dialogView.findViewById(R.id.municipio_code);
                        municipio_code.setText(municipios.get(position).getString("c_digo_dane_del_municipio"));

                        TextView municipio_departamento = (TextView) dialogView.findViewById(R.id.municipio_departamento);
                        municipio_departamento.setText(municipios.get(position).getString("departamento"));


                        TextView municipio_region = (TextView) dialogView.findViewById(R.id.municipio_region);
                        municipio_region.setText(municipios.get(position).getString("region"));

                        Button cerrarButton = (Button) dialogView.findViewById(R.id.cerrar_view);
                        cerrarButton.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              alertDialog.dismiss();

                          }
                        });

                      } catch (JSONException e) {}


                  }

            });



        }
        else {
            holder = (ViewHolder) view.getTag();
        }


        // Set the results into TextViews
        try {
            String nombre = municipios.get(position).getString("municipio");
            holder.name.setText(nombre);
        } catch (JSONException e) {}

        return view;
    }


    // Filter Class
    public void filter(String charText, String campoName) {
        charText = charText.toLowerCase(Locale.getDefault());
        municipios.clear();
        if (charText.length() == 0) {
            municipios.addAll(arraylist);
        } else {
            for (JSONObject municipio : arraylist) {
                try {
                    String campoValue = municipio.getString(campoName);
                    if (campoValue.toLowerCase(Locale.getDefault()).contains(charText)) municipios.add(municipio);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }

}