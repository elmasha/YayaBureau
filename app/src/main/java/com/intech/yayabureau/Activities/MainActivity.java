package com.intech.yayabureau.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.intech.yayabureau.R;

public class MainActivity extends AppCompatActivity {
    Button Toregister,ToLogin;
    FirebaseAuth  mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Toregister = findViewById(R.id.Register);
        ToLogin = findViewById(R.id.Log_in);


        Toregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });


        ToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
//            if (mAuth.getCurrentUser().getUid() != null){
//
//                startActivity(new Intent(getApplicationContext(),MyCandidatesActivity.class));
//            }else {
//
//                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            }

        }

    }
}

