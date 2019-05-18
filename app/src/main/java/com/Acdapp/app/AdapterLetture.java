package com.Acdapp.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class AdapterLetture extends RecyclerView.Adapter<AdapterLetture.LettureViewHolder> {



    private Context mCtx;
    private List<Lettura> listaLetture;

    public AdapterLetture(Context mCtx, List<Lettura> listaLetture) {
        this.mCtx = mCtx;
        this.listaLetture = listaLetture;
    }

    @NonNull
    @Override
    public LettureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.lista_letture, parent, false);
        return new LettureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LettureViewHolder holder, int position) {
        Lettura lettura = listaLetture.get(position);
        holder.codiceUser.setText(lettura.codiceUser);
        holder.codiceUtenteBolletta.setText(lettura.codiceUtenteBolletta);
        holder.nomeUtente.setText(lettura.nomeUtente);
        holder.cognomeUtente.setText(lettura.cognomeUtente);
        holder.valoreLettura.setText("Valore lettura: " + lettura.valoreLettura);
        holder.data.setText("Data: " + lettura.data);
        holder.imagePath.setText("Path Immagine" + lettura.imagePath);
    }

    private void caricaImmagine(String pathImmagine, ImageView imgv) {
        Picasso.get().load(pathImmagine).into(imgv);
    }

    @Override
    public int getItemCount() {
        return listaLetture.size();
    }


    /* sottoclasse */
    class LettureViewHolder extends RecyclerView.ViewHolder {

        TextView codiceUser,codiceUtenteBolletta,nomeUtente,cognomeUtente,valoreLettura,data,imagePath;


        public LettureViewHolder(@NonNull View itemView) {
            super(itemView);

            codiceUser = itemView.findViewById(R.id.codiceUser);
            codiceUtenteBolletta = itemView.findViewById(R.id.codiceUtenteBolleta);
            nomeUtente = itemView.findViewById(R.id.nomeUtente);
            cognomeUtente = itemView.findViewById(R.id.cognomeUtente);
            valoreLettura = itemView.findViewById(R.id.valoreLettura);
            data = itemView.findViewById(R.id.data);
            imagePath = itemView.findViewById(R.id.imagePath);

        }
    }

}

