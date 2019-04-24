package com.Acdapp.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MainActivity<setOnClickListener> extends AppCompatActivity {

    Context context;

    public final static int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        context = getApplicationContext();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        findViewById(R.id.btnInvia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inizializzo un'istanza di Cloud Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                EditText codiceUtente = (EditText) findViewById(R.id.txtCodiceUtente);
                EditText nomeUtente = (EditText) findViewById(R.id.txtNomeUtente);
                EditText cognomeUtente = (EditText) findViewById(R.id.txtCognomeUtente);
                EditText valoreLettura = (EditText) findViewById(R.id.txtValoreLettura);

                // Caricamento dati su firestore
                Map<String, Object> user = new HashMap<>();
                user.put("codiceUtente",codiceUtente.getText().toString());
                user.put("nomeUtente",nomeUtente.getText().toString());
                user.put("cognomeUtente", cognomeUtente.getText().toString());
                user.put("valoreLettura", valoreLettura.getText().toString());


                /*  al posto di GG nel .document bisogna mettere l' id di google ottenuto tramite l'accesso nel google account
                *   in firestore ci saranno due tabelle una user e l altra lettura che si collega a user con il codice utente
                *
                *
                * */
                db.collection("users").document("GG")
                        .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "DocumentSnapshot added with ID: ");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("failure", "Error adding document", e);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri path = data.getData();

            Log.d("Uri", path.toString());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference riversRef = storage.getReference().child("images/" + path.getLastPathSegment());
            final UploadTask uploadTask = riversRef.putFile(path);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Fail", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Download Uri", uri.toString());

                        }
                    });
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
