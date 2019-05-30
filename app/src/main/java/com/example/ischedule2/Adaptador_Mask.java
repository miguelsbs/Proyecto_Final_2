package com.example.ischedule2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adaptador_Mask extends RecyclerView.Adapter<Adaptador_Mask.ViewHolderMask> implements View.OnClickListener{
    ArrayList<Datos_Mask> listaDatos;
    private View.OnClickListener listener;

    public Adaptador_Mask(ArrayList<Datos_Mask> listaDatos) {
        this.listaDatos = listaDatos;
    }

    @Override
    public ViewHolderMask onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mask_tarea,null,false);
        view.setOnClickListener(this);
        return new ViewHolderMask(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderMask viewHolderMask, int i) {
        viewHolderMask.titulo.setText(listaDatos.get(i).getTitulo());
        viewHolderMask.descripcion.setText(listaDatos.get(i).getDescripcion());
        viewHolderMask.img.setImageResource(listaDatos.get(i).getImg());
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolderMask extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, id;
        ImageView img;

        public ViewHolderMask(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tTitulo);
            descripcion = itemView.findViewById(R.id.tDescrip);

            img = itemView.findViewById(R.id.imgT);
        }
    }

}
