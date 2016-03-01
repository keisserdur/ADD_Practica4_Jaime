package com.example.admin.add_practica4_jaime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin.add_practica4_jaime.R;
import com.example.admin.add_practica4_jaime.pojo.Keep;

import java.util.List;

public class Adaptador extends ArrayAdapter<Keep>{

    private int res;
    private LayoutInflater lInflator;
    private List<Keep> valores;



    public Adaptador(Context context, int resource, List<Keep> objects) {
        super(context, resource, objects);
        this.res = resource;//layout del item
        this.valores = objects;//lista de valores
        this.lInflator = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder gv = new ViewHolder();
        if(convertView==null){
            convertView = lInflator.inflate(res, null);
            TextView tv = (TextView) convertView.findViewById(R.id.tvContenido);
            gv.tv1 = tv;
            tv = (TextView) convertView.findViewById(R.id.tvSinc);
            gv.tv2 = tv;

            convertView.setTag(gv);
        } else {
            gv = (ViewHolder) convertView.getTag();
        }

        gv.tv1.setText(valores.get(position).getContenido());
        gv.tv2.setText(valores.get(position).isEstado()+"");
        return convertView;
    }

    /***************************************************************************/
    static class ViewHolder {
        public TextView tv1, tv2;
    }


}