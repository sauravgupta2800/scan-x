package com.example.saurabh.firebaseconnect;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private CardView M1_QRCode,M2_IdForm,M3_MedicalHistory,M4_HospitalVisited,M5_hospitalsNearMe,M6_logout;
    private DatabaseReference databaseReference,mDatabase;
    private CircleImageView PP;
    private String url_String="";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //progress dialog
    private ProgressDialog progressDialog;
    //private EditText editTextName,editTextAddress;
    //private Button buttonSave;

    //Speak Button
    private ImageView speakButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //closing the activity
            finish();
            //starting login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        /*editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonSave = (Button) findViewById(R.id.buttonSave);*/

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textGrid);
        textViewUserEmail.setText(""+user.getEmail());

        progressDialog = new ProgressDialog(this);

        M6_logout = (CardView) findViewById(R.id.M6_logout);
        M2_IdForm = (CardView) findViewById(R.id.M2_IdForm);
        M1_QRCode = (CardView) findViewById(R.id.M1_QRCode);
        M4_HospitalVisited = (CardView) findViewById(R.id.M4_HospitalVisited);
        M3_MedicalHistory = (CardView) findViewById(R.id.M3_MedicalHistory);
        M5_hospitalsNearMe = (CardView) findViewById(R.id.M5_hospitalsNearMe);
        PP = (CircleImageView) findViewById(R.id.PP);

        //tapped speak button;
        speakButton = (ImageView) findViewById(R.id.btnSpeak);


        M6_logout.setOnClickListener(this);
        M2_IdForm.setOnClickListener(this);
        M1_QRCode.setOnClickListener(this);
        M4_HospitalVisited.setOnClickListener(this);
        M3_MedicalHistory.setOnClickListener(this);
        M5_hospitalsNearMe.setOnClickListener(this);

        speakButton.setOnClickListener(this);
        //listener for speakButton


        /*
        //progressDialog.setMessage("Please wait...");
        //progressDialog.show();
        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/learningfirebase-15792.appspot.com/o/uploads%2F1521500624855.jpg?alt=media&token=a999afed-a217-42f8-95da-48529cd4956c").into(PP);

        mDatabase = FirebaseDatabase.getInstance().getReference(user.getUid()+"/ID FORM/ProfilePic/url");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("url",dataSnapshot.getValue()+"");

                //Glide.with(getApplicationContext()).load(url_String).error(R.drawable.user).into(PP);
                Picasso.get()
                        .load(dataSnapshot.getValue().toString().trim())
                        //.placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user)
                        .into(PP);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();

            }
        });
        */

    }

    @Override
    public void onClick(View v) {

        //start of M6
        if(v == M6_logout)
        {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this,LoginActivity.class));
        }


        //Start of M2
        if(v == M2_IdForm)
        {
            finish();
            startActivity(new Intent(this,IdFormActivity.class));

        }

        //start of M1
        if(v == M1_QRCode)
        {
            finish();
            startActivity(new Intent(this,QRCodeActivity.class));

        }

        //start of M4
        if(v == M4_HospitalVisited)
        {
            finish();
            //startActivity Hospital visited.....
            startActivity(new Intent(this,HospitalVisitedActivity.class));
        }
        //uploadimages
        if(v == M3_MedicalHistory)
        {
            finish();
            //start activity of uploading Images known as medical history
            startActivity(new Intent(this,MedicalHostoryActivity.class));
        }
        if(v == M5_hospitalsNearMe)
        {
            finish();
            startActivity(new Intent(this,MapsActivity.class));
        }
        //speakButton Listener
        if(v == speakButton)
        {
            askSpeechInput();
        }
    }


    // Showing google speech input dialog
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String Input = result.get(0).toLowerCase().replaceAll("\\s+","").trim();
                    //Toast.makeText(this, result.get(0).toLowerCase().replaceAll("\\s+","").trim(), Toast.LENGTH_SHORT).show();
                    if(Input.equals("generateqrcode"))
                    {
                        Toast.makeText(this,"Keyword Matched..."+result.get(0), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(this,QRCodeActivity.class));

                    }
                    else if(Input.equals("idform"))
                    {
                        Toast.makeText(this,"Keyword Matched1..."+result.get(0), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(this,IdFormActivity.class));

                    }
                    else if(Input.equals("medicalhistory"))
                    {
                        Toast.makeText(this,"Keyword Matched..."+result.get(0), Toast.LENGTH_SHORT).show();
                        finish();
                        //start activity of uploading Images known as medical history
                        startActivity(new Intent(this,MedicalHostoryActivity.class));

                    }
                    else if(Input.equals("hospitalvisited"))
                    {
                        Toast.makeText(this,"Keyword Matched..."+result.get(0), Toast.LENGTH_SHORT).show();
                        finish();
                        //startActivity Hospital visited.....
                        startActivity(new Intent(this,HospitalVisitedActivity.class));

                    }
                    else if(Input.equals("hospitalnearme"))
                    {
                        Toast.makeText(this,"Keyword Matched..."+result.get(0), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(this,MapsActivity.class));
                    }
                    else if(Input.equals("logout"))
                    {
                        Toast.makeText(this,"Keyword Matched..."+result.get(0), Toast.LENGTH_SHORT).show();
                        //logging out the user
                        firebaseAuth.signOut();
                        //closing activity
                        finish();
                        //starting login activity
                        startActivity(new Intent(this,LoginActivity.class));

                    }
                    else
                    {
                        Toast.makeText(this,"Keyword Doesn't Match you speak "+result.get(0), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }
}
