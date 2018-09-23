package com.bry.petfood.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bry.petfood.R;
import com.bry.petfood.Services.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() == null){
                        startLoginOrSignUp();
                    }else{
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    startLoginOrSignUp();
                }
            }
        });
    }

    private void startLoginOrSignUp(){
        if(new SharedPreferenceManager(getApplicationContext()).isFirstTimeLaunch()){
            Intent intent = new Intent(SplashActivity.this,SignUpActivity.class);
            startActivity(intent);
        }else{
            new SharedPreferenceManager(getApplicationContext()).setFirstTimeLaunch(true);
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
}
