package com.bry.petfood.Services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.bry.petfood.Constants;
import com.bry.petfood.Models.CurrentUser;
import com.bry.petfood.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseManager {
    private final String TAG = "DatabaseManager";
    private Context mContext;


    public DatabaseManager(Context con){
        this.mContext = con;
    }

    public void createUserSpace(CurrentUser user){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(uid);
        dbRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.CREATE_USER_SPACE_COMPLETE));
            }
        });
    }

    public void loadUserData(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(uid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Variables.user = dataSnapshot.getValue(CurrentUser.class);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.LOADED_USER_DATA_SUCCESSFULLY));
                }else{
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.FAILED_TO_LOAD_USER_DATA));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
