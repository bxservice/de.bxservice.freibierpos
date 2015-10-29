package de.bxservice.bxpos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by diego on 29/10/15.
 */
public class OrderArrayAdapter<T> extends ArrayAdapter<T> {

    public OrderArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = convertView;

        if (null == convertView) {
            listItemView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
        }

        //Obteniendo instancias de los text views
        TextView titulo = (TextView)listItemView.findViewById(android.R.id.text1);
        TextView subtitulo = (TextView)listItemView.findViewById(android.R.id.text2);

        //Obteniendo instancia de la Tarea en la posición actual
        T item = (T)getItem(position);

        //Dividir la cadena en Nombre y Hora
        String cadenaBruta;
        String subCadenas [];
        String delimitador = ",";

        cadenaBruta = item.toString();
        subCadenas = cadenaBruta.split(delimitador,2);

        titulo.setText(subCadenas[0]);
        subtitulo.setText(subCadenas[1]);

        //Devolver al ListView la fila creada
        return listItemView;
    }



}
