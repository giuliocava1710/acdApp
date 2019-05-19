package com.Acdapp.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.*;


/*APPUNNTI:
* Il nome e cognome nella lettura lasciamo la possibilità di cambiarlo perchè l'utente puo eseguire le letture per
* la mamma o il papa anziani quindi risulteranno eseguite con il su account ma a nome del diretto interessato.
* L'utente immette il codice utente che trova sulla bolletta , il codice login di firebase viene anche esso inserito nel
* nella raccolta letture una volta inserita la lettura.
*
* ROBA DA FARE:
* - La main che contiene il form per la lettura dovrebbe essere un fragment che si apre dalla botton nav, la quale contiene
* anche il pulsante che apre un altro fragment che mosta lo storico delle letture.
* - Controllo sui campi del dialog l'utente deve immettere un telefono e se cambia i campi devono rispettare le regex
*   finchè i dati non sono accettabili il dialog non si deve chiudere
* - Indicare all'utente come compilare il form della lettura.
* - vedere per export in cvs del db firebase
**/

public class MainActivity<setOnClickListener> extends AppCompatActivity  implements UserInfoDialog.UserInfoDialogListener {

    Context context;
    UserInfoDialog dialog;
    static FirebaseUser user;

    /*non controllata la data*/
    private EditText txtDate = null;

    private EditText codiceUtente;
    private EditText nomeUtente;
    private EditText cognomeUtente;
    private EditText valoreLettura;
    private String imagePath = "";
    private Uri pathInternal;

    private Button btnStorico;

    private  final Calendar myCalendar = Calendar.getInstance();

    static String codiceUser = null;

    public final static int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codiceUtente = (EditText) findViewById(R.id.txtCodiceUtente);
        nomeUtente = (EditText) findViewById(R.id.txtNomeUtente);
        cognomeUtente = (EditText) findViewById(R.id.txtCognomeUtente);
        valoreLettura = (EditText) findViewById(R.id.txtValoreLettura);
        txtDate =  (EditText) findViewById(R.id.txtDate) ;
         //FirebaseApp.initializeApp(this);

         user =  FirebaseAuth.getInstance().getCurrentUser();
        Log.d("USERINFO", "Nome : " + user.getDisplayName() + " Codice : " + user.getUid());

        context = getApplicationContext();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid = user.getUid();

        /*controllo se l'utente è gia stato loggato e quindi ha gia inserito le sue informazioni nel database altrimenti
        * procedo a offrire l'interfaccia per eseguire le autoletture*/
        db.collection("user").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        /*L'utente non è presente nel db perciò deve confermare le sue credenziali*/

                        /*Passo oggetto user a dialog*/
                        ArrayList <String>infoUtente = new ArrayList<>();

                        /*prendo informazionin dell'utente loggato*/
                        infoUtente.add(0,user.getDisplayName());
                        infoUtente.add(1,user.getEmail());


                        /*Se il telefono dell'utente è disponibile viene estratto e inserito nel
                         * db altrimenti si inserisce una stringa vuota */
                        if(user.getPhoneNumber()!= null){
                            Toast toast = new Toast(context);
                            toast.makeText(context,user.getPhoneNumber().toString(),Toast.LENGTH_LONG);
                            infoUtente.add(2,user.getPhoneNumber());
                        }else{
                            infoUtente.add(2,"");
                        }

                        /*il codice utente viene preipostato e posto nel in un campo della raccolta letture
                         * le letture avranno un codice che viene creato automaticamente da firebase*/


                        /*instanzia il dialog per conferma delle informazioni*/
                        dialog = UserInfoDialog.newInstance();

                        final Bundle bundle = new Bundle();
                        /*passaggio dei dati al dialog tramite Bundle*/
                        bundle.putSerializable("UserBundle", infoUtente);
                        dialog.setArguments(bundle);


                        /*evita che con un doppio tap l'utente skippi il dialog*/
                        dialog.setCancelable(false);
                        dialog.show(getSupportFragmentManager(), "USER INFO DIALOG");



                    }
                }
            }
        });

        /*codice utente su firebase*/
        codiceUser = user.getUid().toString();


        /*Setta nella text field  la data corrente*/

        String data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        txtDate.setText(data.toString());

        btnStorico = (Button) findViewById(R.id.btnStorico);
        btnStorico.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query capitalCities = db.collection("Letture").whereEqualTo("codiceUser", codiceUser);
                capitalCities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Lettura> letture = new ArrayList<>();
                        List<DocumentSnapshot> query = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(DocumentSnapshot document : query)
                            letture.add(new Lettura(document));

                        Intent i = new Intent(MainActivity.this, ActivityStorico.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("letture", letture);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });


                /*
                FragmentStoricoLetture fm = FragmentStoricoLetture.newInstance(null,null);
                FragmentManager fMan = getSupportFragmentManager();
                fMan.beginTransaction().replace(R.id.frameLayoutStorico,fm,"fragment storico").commit();
                */
                /*FragmentStoricoLetture fsl = FragmentStoricoLetture.newInstance(null,null);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frameLayoutStorico,fsl,"Fragment storico letture");
                transaction.addToBackStack(null);
                transaction.commit();*/


            }
        });


        /*Data picker l'utente puo scegliere se mantenere la data corrente o cambiarla*/

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        /* per funzionare setOnClickListener deve essere nel metodo OnCreate dell'acitivity*/
        txtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.btnCaricaFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /*viene evocato il metodo sotto che esegue fisicamente il caricamneto dell'immagine au firebase*/
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        findViewById(R.id.btnInvia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inizializzo un'istanza di Cloud Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if(checkSubmit()){
                    // Caricamento dati su firestore

                    Map<String, Object> user = new HashMap<>();

                    user.put("codiceUser",codiceUser);
                    user.put("codiceUtenteBolletta",codiceUtente.getText().toString());
                    user.put("nomeUtente",nomeUtente.getText().toString());
                    user.put("cognomeUtente",cognomeUtente.getText().toString());
                    /*controllo sui campi nome e cognome se errore la textfield si colora di rosso*/
                    /*chiedere come vogliono questo valore , con quante cifre significative*/
                    user.put("valoreLettura", valoreLettura.getText().toString());
                    user.put("data",txtDate.getText().toString());
                    user.put("imagePath",imagePath);



                    /**/
                    db.collection("Letture").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context,"Lettura aquistia",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Problemi in lettura",Toast.LENGTH_LONG).show();
                        }
                    });




                /* Inserimento della lettura del contatore all'evento click sul pulsante invia
                La lettura viene legata al codice UID dell'utente loggato
                *
                db.collection("Letture").document( FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "DocumentSnapshot added with ID: ");
                        Toast toast = new Toast(context);
                        toast.makeText(context,"Lettura aquistia",Toast.LENGTH_LONG).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("failure", "Error adding document", e);
                            }
                        });

                     */
                }else{
                    Toast.makeText(getApplicationContext(),"Controllare i campi in rosso",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            pathInternal = data.getData();
            Log.d("Uri", pathInternal.toString());
            caricaImagine();
        }
    }


    /*il metodin del dialog sono utilizzabili da questa activity perchè è come se fosse
    * un interfaccia che viene implementata in questa activity */
    @Override
    public void onUserInfoDialogOkPressed(String nome, String cognome,String mail,String telefono,Boolean ret) {
        /* una volta confermate le informazioni tramite il dialog da questo metodo occorre
        * caricare le informazioni  dell'utente su firebase */ //TODO

        Log.d("USERINFODIALOG", nome + " " + cognome + " " + mail + "" + telefono );
        if(ret){
            dialog.dismiss();
        }else{
            Toast.makeText(getApplicationContext(),"Controllare campi",Toast.LENGTH_LONG).show();
        }

    }

    /*Metodi per il controllo dei campi effettuati tramite regex all'invio della lettura*/
    /*metodo utilizzato sia per il nome che per il cognome*/
    public static boolean validateNameSurname( String param )
    {
        return param.matches( "^[A-Za-zèùàòé][a-zA-Z'èùàòé ]*$");
    } // end method validateFirstName

    // ritorna vero se tutti i campi sono settati correttamente
    private boolean checkSubmit()
    {
        boolean ret=true;
        /* sul codice non puo esserci un controllo perchè sono dati appartenenti all'acda
        * quindi si controlla solo se il campo non è vuoto*/
        if(codiceUtente.getText().toString().equals("")) {
            ret = false;
            codiceUtente.setBackgroundColor(Color.RED);
        }else{
            codiceUtente.setBackgroundResource(0);
        }
        if(!validateNameSurname(nomeUtente.getText().toString())){
            ret=false;
            nomeUtente.setBackgroundColor(Color.RED);
        }else{
            nomeUtente.setBackgroundResource(0);
        }
        if(!validateNameSurname(cognomeUtente.getText().toString())){
            ret= false;
            cognomeUtente.setBackgroundColor(Color.RED);
        }else{
            cognomeUtente.setBackgroundResource(0);
        }
        if(valoreLettura.getText().toString().equals("")){

            ret=false;
            valoreLettura.setBackgroundColor(Color.RED);
        }else{
            valoreLettura.setBackgroundResource(0);
        }
        if(imagePath.equals("")){
            ret=false;
            Toast.makeText(getApplicationContext(),"Per favore selezionare un'immagine della lettura"
                    ,Toast.LENGTH_LONG).show();
        }
        return ret;
    } // end method validateLastName

    private void caricaImagine(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Caricamento immagine");
        pd.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference riversRef = storage.getReference().child("images/" + pathInternal.getLastPathSegment());
        final UploadTask uploadTask = riversRef.putFile(pathInternal);

        final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();

                // Handle failures
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    /*prendere questo percorso e immetterlo nella raccolta lettura come campo della
                     * singola lettura*/
                    Uri downloadUri = task.getResult();
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                    //mostra il percorso della foto caricata

                    /*Metto nella variabile statica imagePath il percorso dell'immagine inserita dal'utente
                     * dopodiche imagePsth verra usata per la scrittura sul db*/
                    imagePath = downloadUri.toString();
                    Toast.makeText(context, downloadUri.toString() , Toast.LENGTH_LONG).show();

                    Log.d("Uri download",downloadUri.toString());
                    Log.d("Storage ref",task.getResult().getPath());

                    pd.dismiss();

                } else {
                    /*Errore nel caricamento dell'immagine*/
                    Toast.makeText(context, "Errore nel caricamento", Toast.LENGTH_LONG).show();
                }
            }
        });

            /*
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

            */
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        txtDate.setText(sdf.format(myCalendar.getTime()));
    }

}

