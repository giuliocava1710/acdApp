package com.Acdapp.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStoricoLetture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStoricoLetture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStoricoLetture extends Fragment {




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*proprietà di nostro interesse*/
    private RecyclerView recyclerView ;
    private List<Lettura> listaLetture;
    private AdapterLetture adapterLetture;

    /*DatabaseReference dbLetture*/
    private FirebaseFirestore db;


    private OnFragmentInteractionListener mListener;


    public FragmentStoricoLetture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStoricoLetture.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStoricoLetture newInstance(String param1, String param2) {
        FragmentStoricoLetture fragment = new FragmentStoricoLetture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db=FirebaseFirestore.getInstance();
    }


    /*il codice che deve eseguire il fragment deve essere inserito in questo metodo ovvero nel onCreateView*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lista_letture, container, false);


        recyclerView = v.findViewById(R.id.lista_letture);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaLetture = new ArrayList<>();
        adapterLetture = new AdapterLetture(this.getContext(), listaLetture);
        recyclerView.setAdapter(adapterLetture);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        /*prende tutte le letture dell'utente loggato in ordinate per data
        Query query = db.collection("Letture")
                .orderBy("data")
                .whereEqualTo("codiceUser", auth.getUid().toString());
        */

        /*
         * You just need to attach the value event listener to read the values
         * for example
         * query6.addListenerForSingleValueEvent(valueEventListener)
         * */

        db.collection("Letture")
                .orderBy("data")
                .whereEqualTo("codiceUser", auth.getUid().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        Lettura lettura = document.toObject(Lettura.class);
                        listaLetture.add(lettura);

                    }
                    adapterLetture.notifyDataSetChanged();
                }
            }
        });
        return v;
    }
        /*
        PARTE FATTA DA SINGH
        db.collection("Letture").whereEqualTo("codiceUser",auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documets = task.getResult();
                    for(DocumentSnapshot doc : documets.getDocuments()){

                        // Letture let = doc.getData(Letture.class);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        return v;
    }


     METODO COME DICEVA IL TUTORIAL
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            artistList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Artist artist = snapshot.getValue(Artist.class);
                    artistList.add(artist);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

  */


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /* @Override
   public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

