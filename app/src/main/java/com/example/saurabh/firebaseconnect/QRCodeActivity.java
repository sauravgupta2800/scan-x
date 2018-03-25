package com.example.saurabh.firebaseconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String text2QR;
    private ImageView image;
    private Button BackButton,LogoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        image = (ImageView) findViewById(R.id.imageViewQR);
        BackButton = (Button) findViewById(R.id.BackButton);
        LogoutButton = (Button) findViewById(R.id.LogoutButton);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //closing the activity
            finish();
            //starting login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        //current user create
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("abc",user.getEmail());
        Toast.makeText(this, user.getEmail()+"", Toast.LENGTH_SHORT).show();
        text2QR = user.getUid();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    text2QR,
                    BarcodeFormat.QR_CODE,
                    500,
                    500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        }
        catch(WriterException e) {
            e.printStackTrace();
        }

        BackButton.setOnClickListener(this);
        LogoutButton.setOnClickListener(this);

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
        if(v == BackButton)
        {
            //back button pressed Activity
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
        if(v == LogoutButton)
        {
            //Logout Button pressed Activity
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this,LoginActivity.class));

        }

    }
}
