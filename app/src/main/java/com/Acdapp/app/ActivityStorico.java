package com.Acdapp.app;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;


/*
DESCRIZIONE:
  Activity di collegamento che contenente il fragment il quale visualizza le autoletture svolte dall'utente loggato
* infatti per tornare alla main occorre  cliccare due volte il tasto indietro la prima per chiudere il fragment
 * e la seconda per chiudere l' Activity */

public class ActivityStorico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico);

        Bundle bundle = getIntent().getExtras();
        ArrayList<Lettura> letture = (ArrayList<Lettura>)bundle.getSerializable("letture");

        //for(Lettura lettura : letture) Log.d("LETTURA", lettura.toString());

        /*Appena lanciata l'activity si limita a lanciare il fragment dello storico*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame, FragmentStoricoLetture.newInstance(letture),"Fragment storico letture");
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
