package com.bry.petfood.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Constants;
import com.bry.petfood.Models.CurrentUser;
import com.bry.petfood.R;
import com.bry.petfood.Services.DatabaseManager;
import com.bry.petfood.Services.SharedPreferenceManager;
import com.bry.petfood.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener{
    @Bind(R.id.createUserButton) Button mCreateUserButton;
    @Bind(R.id.nameEditText) EditText mNameEditText;
    @Bind(R.id.emailEditText) EditText mEmailEditText;
    @Bind(R.id.passwordEditText) EditText mPasswordEditText;
    @Bind(R.id.confirmPasswordEditText) EditText mConfirmPasswordEditText;
    @Bind(R.id.loginTextView) TextView mLoginTextView;
    @Bind(R.id.signUpRelative) RelativeLayout mRelative;
    @Bind(R.id.SignUpAvi) AVLoadingIndicatorView mAvi;
    @Bind(R.id.progressBarSignUp) ProgressBar mProgressBarSignUp;
    @Bind(R.id.creatingAccountLoadingText) TextView mLoadingText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mName;
    private DatabaseReference mRef1;
    private DatabaseReference mRef2;
    private int mClusterID;

    public static final String TAG = SignUpActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private Context mContext;
    private CurrentUser user;

    Handler h = new Handler();
    Runnable r;
    boolean canRemoveCurrentUser = true;
    boolean doesUserNowExist = false;
    private List<String> easyPasswords = new ArrayList<>
            (Arrays.asList("123456789", "987654321","qwertyuio","asdfghjkl","zxcvbnm12","123456abc","123456qwe","987654qwe",
                    "987654asd",""));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();
        mContext = this.getApplicationContext();

        mLoginTextView.setOnClickListener(this);
        mCreateUserButton.setOnClickListener(this);
        mConfirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT) ||
                        (actionId == EditorInfo.IME_ACTION_GO)) {
                    try{
                        mCreateUserButton.performClick();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Enter pressed");
                }
                return false;
            }
        });

        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) findViewById(R.id.dontForgetStuff).setVisibility(View.VISIBLE);
                else findViewById(R.id.dontForgetStuff).setVisibility(View.GONE);
            }
        });
    }

    private void createAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    setUpUserSpace();
                }
            }
        };
    }



    @Override
    public void onClick(View v) {
        if(v.equals(findViewById(R.id.loginTextView))){
            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if(v == mCreateUserButton){
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            createNewUser();
        }
    }

    private void createNewUser() {
        final String name = mNameEditText.getText().toString().trim();
        final String email = mEmailEditText.getText().toString().trim();

        final String password = mPasswordEditText.getText().toString().trim();
        String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password,confirmPassword);
        boolean validName = isValidName(name);
        if(!validEmail || !validName || !validPassword)return;

        if(!isOnline(mContext)){
            Snackbar.make(findViewById(R.id.SignUpCoordinatorLayout), R.string.SignUpNoConnection,
                    Snackbar.LENGTH_LONG).show();
        }else{
            mProgressBarSignUp.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.VISIBLE);
            mRelative.setVisibility(View.GONE);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG,"authentication successful");
                        doesUserNowExist = true;
                        createFirebaseUserProfile(task.getResult().getUser());
                        Variables.userName = name;
                        Variables.setPassword(password);
                    }else {
                        mRelative.setVisibility(View.VISIBLE);
                        mProgressBarSignUp.setVisibility(View.GONE);
                        mLoadingText.setVisibility(View.GONE);
                        Toast.makeText(mContext,"Sign up failed, check your credentials again.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if(password.equals("")){
            mPasswordEditText.setError("We need a password.");
            return false;
        }else if (password.length() < 9) {
            mPasswordEditText.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mPasswordEditText.setError("Passwords do not match");
            return false;
        }else if(easyPasswords.contains(password)){
            mPasswordEditText.setError("Please, put a strong password!");
            return false;
        }
        return true;
    }


    private boolean isValidEmail(String email) {
        if(email.equals("")){
            mEmailEditText.setError("We need your email.");
            return false;
        }

        boolean isGoodEmail = (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());

        if(!email.contains("@")){
            mEmailEditText.setError("That's not an email address.");
            return false;
        }

        int counter = 0;
        for( int i=0; i<email.length(); i++ ) {
            if(email.charAt(i) == '.' ) {
                counter++;
            }
        }
        if(counter!=1 && counter!=2){
            mEmailEditText.setError("We need your actual email address.");
            return false;
        }

        int counter2 = 0;
        boolean continueIncrement = true;
        for( int i=0; i<email.length(); i++ ) {
            if(email.charAt(i) == '@' ) {
                continueIncrement = false;
            }
            if(continueIncrement)counter2++;
        }
        if(counter2<=3){
            mEmailEditText.setError("That's not a real email address");
            return false;
        }

        if(!isGoodEmail){
            mEmailEditText.setError("We need your actual email address please");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNameEditText.setError("Please enter your name");
            return false;
        }
        if(name.length()>16){
            mNameEditText.setError("Your name is too long");
            return false;
        }
        return true;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Created new username");
                }
            }
        });
    }

    private void LogoutUser(){
        Variables.user = null;
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }


    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForFinishedCreatingUserSpace,
                new IntentFilter(Constants.CREATE_USER_SPACE_COMPLETE));
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener!=null) mAuth.removeAuthStateListener(mAuthListener);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForFinishedCreatingUserSpace);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"On destroy called");
    }


    private BroadcastReceiver mMessageReceiverForFinishedCreatingUserSpace = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Finished creating user space");
            verifyUser();
        }
    };

    private void startMainActivity(){
        canRemoveCurrentUser = false;
        mProgressBarSignUp.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
        Variables.user = null;
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
        }
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setUpUserSpace(){
        final String name = mNameEditText.getText().toString().trim();
        final String email = mEmailEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();
        user = new CurrentUser(name,FirebaseAuth.getInstance().getCurrentUser().getUid(),email,password);

        mRelative.setVisibility(View.GONE);
        DatabaseManager dbManager = new DatabaseManager(mContext);
        dbManager.createUserSpace(user);
    }

    private void verifyUser(){
        if(!checkIfEmailIsVerified())sendVerificationEmail();
        final Dialog d = new Dialog(this);
        d.setTitle("Email.");
        d.setContentView(R.layout.dialog_verify_email);
        Button b1 = d.findViewById(R.id.okBtn);
        final TextView hasVerifiedText = d.findViewById(R.id.hasVerifiedText);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d(TAG,"On dismiss for dialog");
                h.removeCallbacks(r);
            }
        });
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d(TAG,"On dismiss for dialog");
                h.removeCallbacks(r);
            }
        });
        d.setCancelable(false);
        d.show();

        r = new Runnable() {
            @Override
            public void run() {
                if(checkIfEmailIsVerified()){
                    hasVerifiedText.setText("Email verified.");
                    h.removeCallbacks(r);
                    d.dismiss();
                    startMainActivity();
                }else{
                    hasVerifiedText.setText("Email not verified.");
                }
                h.postDelayed(r, 1000);
            }
        };
        h.postDelayed(r, 1000);
    }

    private boolean checkIfEmailIsVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.reload();
        return user.isEmailVerified();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   Toast.makeText(mContext,"Email sent.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
