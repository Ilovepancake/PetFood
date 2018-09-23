package com.bry.petfood.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bry.petfood.R;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() == null){
                        Intent intent = new Intent(Splash.this,Login.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(Splash.this,MainActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Intent intent = new Intent(Splash.this,Login.class);
                    startActivity(intent);
                }
            }
        });
    }
}
