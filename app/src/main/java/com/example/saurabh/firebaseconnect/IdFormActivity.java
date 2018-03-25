package com.example.saurabh.firebaseconnect;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.os.Build.VERSION_CODES.O;

public class IdFormActivity extends AppCompatActivity implements View.OnClickListener{


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button RegisterButton,BackButton;
    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    //upload image instance
    private ImageView uploadImageView;

    private EditText FullName,ContactNumber,DOB,Gender,Height,Weight,BloodGroup,Address;

    //uri to store file
    private Uri filePath;

    //firebse storage reference
    private StorageReference storageReference;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_form);

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

        FullName = (EditText) findViewById(R.id.FullName);//ok
        ContactNumber = (EditText) findViewById(R.id.ContactNumber);//ok
        DOB = (EditText) findViewById(R.id.DOB);//ok
        Gender =  (EditText) findViewById(R.id.Gender);//ok
        Height = (EditText) findViewById(R.id.Height);//ok
        Weight = (EditText) findViewById(R.id.Weight);//ok
        BloodGroup = (EditText) findViewById(R.id.BloodGroup);///ok
        Address = (EditText) findViewById(R.id.Address);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.FullName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);//ok
        awesomeValidation.addValidation(this, R.id.ContactNumber, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);//ok
        awesomeValidation.addValidation(this, R.id.DOB, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$",R.string.dateerror);//ok
        awesomeValidation.addValidation(this,R.id.BloodGroup,"^(A|B|AB|O)[+-]$",R.string.blooderror);//ok
        awesomeValidation.addValidation(this,R.id.Gender,"^(M|F|M/F)$",R.string.gendererror);//ok
        awesomeValidation.addValidation(this, R.id.Height, Range.closed(1, 500), R.string.heighterror);
        awesomeValidation.addValidation(this, R.id.Weight, Range.closed(2, 500), R.string.weighterror);
        awesomeValidation.addValidation(this,R.id.Address,"^[#.0-9a-zA-Z\\s,-]+$",R.string.addresserror);//ok



        //firebase instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //getting upload image
        uploadImageView  = (ImageView) findViewById(R.id.uploadImageView);

        //current user create
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("abc",user.getEmail()+"");//varify the current user
        Toast.makeText(this, user.getEmail()+"", Toast.LENGTH_SHORT).show();

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

        RegisterButton.setOnClickListener(this);
        BackButton.setOnClickListener(this);
        uploadImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == RegisterButton)
        {
            if (awesomeValidation.validate()) {
                Toast.makeText(this, "Validation Successfull", Toast.LENGTH_LONG).show();

                //process the data further
                saveUserInformation();
                // after user registration automatically move for further MODULE
                finish();
                startActivity(new Intent(this,ProfileActivity.class));
            }
        }
        if(v == BackButton)
        {
            // back to Profile Activity to select further MODULES
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
        if(v == uploadImageView)
        {
            //upload image activity.
            showFileChooser();
        }

    }

    private void saveUserInformation() {
        String f = FullName.getText().toString().trim();
        String c = ContactNumber.getText().toString().trim();
        String d = DOB.getText().toString().trim();
        String g = Gender.getText().toString().trim();
        String h = Height.getText().toString().trim();
        String w = Weight.getText().toString().trim();
        String b = BloodGroup.getText().toString().trim();
        String a = Address.getText().toString().trim();

        UserInformation userInformation = new UserInformation(f,c,d,g,h,w,b,a);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //user returns the UID of current logged in user;
        databaseReference.child(user.getUid() + "/ID FORM").setValue(userInformation);
        Toast.makeText(this, "Information saved ....", Toast.LENGTH_LONG).show();
    }
    //show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
        uploadFile();
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //upload file to database
    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "Profile Pic Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            PPUpload upload = new PPUpload(taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(user.getUid() + "/ProfilePic/").setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }
}
