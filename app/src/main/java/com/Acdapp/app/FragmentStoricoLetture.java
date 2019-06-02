package com.Acdapp.app;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*
  DESCRIZIONE:
  Fragment che mostra il lavoro svolto dall'adapeter ponendolo in un layout composto da una recycler Vieww.
* */

public class FragmentStoricoLetture extends Fragment {

    private ArrayList<Lettura> listaLetture = null;

    /*proprietà di nostro interesse*/
    private RecyclerView recyclerView ;
    private AdapterLetture adapterLetture;

    // OnFragmentInteractionListener mListener;


    public FragmentStoricoLetture() {}

    // TODO: Rename and change types and number of parameters
    public static FragmentStoricoLetture newInstance(ArrayList<Lettura> letture) {
        FragmentStoricoLetture fragment = new FragmentStoricoLetture();
        Bundle args = new Bundle();
        args.putSerializable("listaLetture", letture);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listaLetture = (ArrayList<Lettura>) getArguments().getSerializable("listaLetture");
        }
    }


    /*il codice che deve eseguire il fragment deve essere inserito in questo metodo ovvero nel onCreateView*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lista_letture, container, false);


        recyclerView = v.findViewById(R.id.lista_letture);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterLetture = new AdapterLetture(this.getContext(), listaLetture);
        recyclerView.setAdapter(adapterLetture);

        return v;
    }

}

