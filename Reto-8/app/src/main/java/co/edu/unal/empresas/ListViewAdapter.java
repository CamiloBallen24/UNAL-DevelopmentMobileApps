package co.edu.unal.empresas;

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



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<Empresa> empresas;
    private ArrayList<Empresa> arraylist;
    EmpresasRepository empresasRepository;

    public ListViewAdapter(Context context, List<Empresa> empresas, EmpresasRepository empresasRepository) {
        mContext = context;
        this.empresasRepository = empresasRepository;
        this.empresas = empresas;
        inflater = LayoutInflater.from(mContext);

        this.arraylist = new ArrayList<Empresa>();
        this.arraylist.addAll(empresas);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return empresas.size();
    }

    @Override
    public Empresa getItem(int position) {
        return empresas.get(position);
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

            //Eliminar Logica
            Button delete = (Button) view.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogo_eliminar = new AlertDialog.Builder(mContext);
                    dialogo_eliminar.setTitle("Importante");
                    dialogo_eliminar.setMessage("¿Desea eliminar la empresa?");
                    dialogo_eliminar.setCancelable(false);
                    dialogo_eliminar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo_eliminar, int id) {
                            empresasRepository.deleteEmpresa(empresas.get(position));
                            arraylist = empresasRepository.getEmpresas();
                            filter("");
                            dialogo_eliminar.dismiss();

                        }
                    });
                    dialogo_eliminar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo_eliminar, int id) {
                            dialogo_eliminar.dismiss();
                        }
                    });
                    dialogo_eliminar.show();

                }
            });

            //Ver y Editar Eliminar
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    ViewGroup viewGroup = v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.editar, viewGroup, false);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    EditText editTextName =  (EditText) dialogView.findViewById(R.id.update_name);
                    editTextName.setText(empresas.get(position).getName());

                    EditText editTextUrl =  (EditText) dialogView.findViewById(R.id.update_url);
                    editTextUrl.setText(empresas.get(position).getUrl());

                    EditText editTextPhone =  (EditText) dialogView.findViewById(R.id.update_phone);
                    editTextPhone.setText(empresas.get(position).getPhone());

                    EditText editTextEmail =  (EditText) dialogView.findViewById(R.id.update_email);
                    editTextEmail.setText(empresas.get(position).getEmail());

                    EditText editTextServicio =  (EditText) dialogView.findViewById(R.id.update_servicio);
                    editTextServicio.setText(empresas.get(position).getServices());

                    //Spinner
                    String[] items = new String[]{"Consultoria", "Desarrollo", "Fabrica"};
                    ArrayList<String> itemsArray = new ArrayList<>();
                    itemsArray.add("Consultoria");
                    itemsArray.add("Desarrollo");
                    itemsArray.add("Fabrica");

                    Spinner spinner = (Spinner)dialogView.findViewById(R.id.update_classificacion);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    Log.d("Messi", empresas.get(position).getClassification());
                    spinner.setSelection(itemsArray.indexOf(empresas.get(position).getClassification()));

                    Button updateButton = (Button) dialogView.findViewById(R.id.update_button);
                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Messi", spinner.getSelectedItem().toString());

                            Empresa updateEmpresa = new Empresa(
                                    empresas.get(position).getID(),
                                    editTextName.getText().toString(),
                                    editTextUrl.getText().toString(),
                                    editTextPhone.getText().toString(),
                                    editTextEmail.getText().toString(),
                                    editTextServicio.getText().toString(),
                                    spinner.getSelectedItem().toString());
                            empresasRepository.updateEmpresa(updateEmpresa);
                            arraylist = empresasRepository.getEmpresas();
                            filter("");
                            alertDialog.dismiss();

                        }
                    });
                }
            });

        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(empresas.get(position).getName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        empresas.clear();
        if (charText.length() == 0) {
            empresas.addAll(arraylist);
        } else {
            for (Empresa wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    empresas.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}