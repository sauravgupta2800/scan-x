package com.example.saurabh.firebaseconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HospitalVisitedActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button RegisterButton,BackButton;

    private EditText HospitalName,TreatmentName,StartDate,EndDate;

    private String h,t,s,e;//all input enter by in module 4
    private HospitalData hospitalData;
    private FirebaseUser user;
    private int count = 1;

    private Button HospitalNumber;


    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_visited);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //closing the activity
            finish();
            //starting login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //initializing awesomevalidation object
        /*
        * The library provides 3 types of validation
        * BASIC
        * COLORATION
        * UNDERLABEL
        * */
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //instance creation
        RegisterButton = (Button) findViewById(R.id.RegisterButton);
        BackButton = (Button) findViewById(R.id.BackButton);

        HospitalName = (EditText) findViewById(R.id.HospitalName);//ok
        TreatmentName = (EditText) findViewById(R.id.TreatmentName);
        StartDate = (EditText) findViewById(R.id.StartDate);
        EndDate =  (EditText) findViewById(R.id.EndDate);

        //for displaying hospital number
        HospitalNumber = (Button) findViewById(R.id.HospitalNumber);


        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.HospitalName, "^[#.0-9a-zA-Z\\s,-]+$", R.string.hospitalerror);//ok
        awesomeValidation.addValidation(this, R.id.TreatmentName, "^[#.0-9a-zA-Z\\s,-]+$", R.string.treatmenterror);//ok
        awesomeValidation.addValidation(this, R.id.StartDate, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$",R.string.dateerror);//ok
        awesomeValidation.addValidation(this, R.id.EndDate, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$",R.string.dateerror);//ok

        HospitalNumber.setText("HOSPITAL NUMBER: "+count);

        //current user create
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("abc",user.getEmail()+"");//varify the current user
        Toast.makeText(this, user.getEmail()+"", Toast.LENGTH_SHORT).show();

        RegisterButton.setOnClickListener(this);
        BackButton.setOnClickListener(this);



        //TOOLBAR (back)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == RegisterButton)
        {
            if (awesomeValidation.validate())
            {
                saveUserInformation();
            }
        }
        if(v == BackButton)
        {
            // back to Profile Activity to select further MODULES
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
    }

    private void saveUserInformation() {

        takeInput();//take all input present on text fields
        putInput();//put into database
        applyText();//self developed program
    }
    private void takeInput(){
        h = HospitalName.getText().toString().trim();
        t = TreatmentName.getText().toString().trim();
        s = StartDate.getText().toString().trim();
        e = EndDate.getText().toString().trim();
        hospitalData = new HospitalData(h,t,s,e);
        user = firebaseAuth.getCurrentUser();
    }
    private void putInput(){
        databaseReference.child(user.getUid() + "/HOSPITAL VISITED"+"/H "+count).setValue(hospitalData);
        Toast.makeText(this, "Information saved ....", Toast.LENGTH_LONG).show();
        count = count+1;
        HospitalNumber.setText("HOSPITAL NUMBER: "+count);

    }
    private void applyText(){
        HospitalName.setText(null);
        TreatmentName.setText(null);
        StartDate.setText(null);
        EndDate.setText(null);
        HospitalName.setHint("Hospital Name");
        TreatmentName.setHint("Treatment Name");
        StartDate.setHint("Starting Date (DD/MM/YYYY)");
        EndDate.setHint("Ending Date   (DD/MM/YYYY)");
    }
}
