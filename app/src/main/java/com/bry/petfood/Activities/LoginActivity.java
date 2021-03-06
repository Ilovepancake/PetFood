package com.bry.petfood.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import com.bry.petfood.R;
import com.bry.petfood.Services.DatabaseManager;
import com.bry.petfood.Variables;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = LoginActivity.class.getSimpleName();
    @Bind(R.id.emailEditText) EditText mEmail;
    @Bind(R.id.passwordEditText) EditText mPassword;
    @Bind(R.id.LoginButton) Button mLoginButton;
    @Bind(R.id.registerLink) TextView mRegisterLink;
    @Bind(R.id.LoginAvi) AVLoadingIndicatorView mAvi;
    @Bind(R.id.progressBarlogin) ProgressBar mProgressBarLogin;
    @Bind(R.id.settingUpMessageLogin) TextView mLoadingMessage;
    @Bind(R.id.LoginRelative) RelativeLayout mRelative;
    @Bind(R.id.noConnectionLayout) LinearLayout mNoConnectionLayout;
    @Bind(R.id.retry) Button mRetryButton;
    @Bind(R.id.failedLoadLayout) LinearLayout mFailedLoadLayout;
    @Bind(R.id.retryLoading) Button mRetryLoadingButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private DatabaseReference mRef2;
    private DatabaseReference mRef3;
    private DatabaseReference mRef4;
    private DatabaseReference adRef;
    private DatabaseReference mRef5;

    private Context mContext;
    private String mKey = "";
    private boolean mIsLoggingIn = false;

    private boolean hasEverythingLoaded;
    private boolean isActivityVisible;
    private boolean didUserJustLogInManually = false;
    private boolean isShowingPromptForeula = false;

    Handler h = new Handler();
    Runnable r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        if(!Fabric.isInitialized()) Fabric.with(this, new Crashlytics());
        mAuth = FirebaseAuth.getInstance();
        mRegisterLink.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mContext = this.getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverForFinishedLoadingData,
                new IntentFilter(Constants.LOADED_USER_DATA_SUCCESSFULLY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverForFailedToLoadData,
                new IntentFilter(Constants.FAILED_TO_LOAD_USER_DATA));

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT) ||
                        (actionId == EditorInfo.IME_ACTION_GO)) {
                    try{
                        mLoginButton.performClick();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.i(TAG,"Enter pressed");
                }
                return false;
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!= null){
                    Log.d(TAG,"A user exists."+firebaseAuth.getCurrentUser().getUid());
                    if(isOnline(mContext)){
                        Log.d(TAG,"user is online, setting up everything normally");
                        mRelative.setVisibility(View.GONE);
                        mNoConnectionLayout.setVisibility(View.GONE);
                        mProgressBarLogin.setVisibility(View.VISIBLE);
                        mLoadingMessage.setVisibility(View.VISIBLE);
                        mIsLoggingIn = false;
                        startLoadingUserData();
                    }else{
                        setNoInternetView();
                    }
                }
            }
        };

    }

    private  void setNoInternetView(){
        Log.d(TAG,"There is no internet connection,showing no internet dialog");
        mRelative.setVisibility(View.GONE);
        mNoConnectionLayout.setVisibility(View.VISIBLE);
        mRetryButton.setOnClickListener(this);
    }

    private void setFailedToLoadView(){
        Log.d(TAG,"Failed to load data,showing failed to load data dialog");
//        mRelative.setVisibility(View.GONE);
//        mAvi.setVisibility(View.GONE);
        mProgressBarLogin.setVisibility(View.GONE);
        mLoadingMessage.setVisibility(View.GONE);

        mFailedLoadLayout.setVisibility(View.VISIBLE);
        mRetryLoadingButton.setOnClickListener(this);
    }

    private void startMainActivity(){
        if(hasEverythingLoaded && isActivityVisible){
            mProgressBarLogin.setVisibility(View.GONE);
            mLoadingMessage.setVisibility(View.GONE);
            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        if(v == mRegisterLink && !mIsLoggingIn){
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if(v == mLoginButton && !mIsLoggingIn){
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            loginUserWithPassword();

        }
        if(v == mRetryButton){
            if(isOnline(mContext)){
                mNoConnectionLayout.setVisibility(View.GONE);
                mRelative.setVisibility(View.GONE);
//                mAvi.setVisibility(View.VISIBLE);
                mProgressBarLogin.setVisibility(View.VISIBLE);
                mLoadingMessage.setVisibility(View.VISIBLE);
                startLoadingUserData();
            }else{
                Log.d(TAG,"No internet connection!!");
                Toast.makeText(mContext,"You don't have an internet connection.",Toast.LENGTH_SHORT).show();
            }
        }
        if(v== mRetryLoadingButton){
            mRelative.setVisibility(View.GONE);
            mFailedLoadLayout.setVisibility(View.GONE);
//            mAvi.setVisibility(View.VISIBLE);
            mProgressBarLogin.setVisibility(View.VISIBLE);
            mLoadingMessage.setVisibility(View.VISIBLE);
            Toast.makeText(mContext,"Retrying...",Toast.LENGTH_SHORT).show();
            startLoadingUserData();
        }
    }

    private BroadcastReceiver mMessageReceiverForFinishedLoadingData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Finished loading user data");
            hasEverythingLoaded = true;
            checkIfUserIsAuthentic();
        }
    };

    private BroadcastReceiver mMessageReceiverForFailedToLoadData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Failed to load User data");
            hasEverythingLoaded = false;
            setFailedToLoadView();
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        isActivityVisible = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        isActivityVisible = true;
        if(!isShowingPromptForeula && hasEverythingLoaded) {
            startMainActivity();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy(){
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForFailedToLoadData);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForFinishedLoadingData);
        super.onDestroy();
    }

    private void loginUserWithPassword() {
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        if(email.equals("")){
//            mEmail.setText("Please enter your email");
            mEmail.setError("Please enter your email");
            return;
        }
        if(password.equals("")){
            mPassword.setError("Password cannot be blank");
            return;
        }
        if(!isOnline(mContext)){
            Snackbar.make(findViewById(R.id.loginCoordinatorLayout), R.string.LogInNoConnection,
                    Snackbar.LENGTH_LONG).show();
        }else{
            mProgressBarLogin.setVisibility(View.VISIBLE);
            mLoadingMessage.setVisibility(View.VISIBLE);
            mRelative.setVisibility(View.GONE);
            mIsLoggingIn = true;
            Log.d(TAG,"--Logging in user with username and password...");

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG,"signInWithEmail:onComplete"+task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.w(TAG,"SignInWithEmail",task.getException());
                                mRelative.setVisibility(View.VISIBLE);
                                mProgressBarLogin.setVisibility(View.GONE);
                                mLoadingMessage.setVisibility(View.GONE);
                                mIsLoggingIn = false;
                                Toast.makeText(mContext,"Invalid email or password.",Toast.LENGTH_SHORT).show();
                            }else{
                                didUserJustLogInManually = true;
                            }
                        }
                    });
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void startLoadingUserData(){
        DatabaseManager dbMan = new DatabaseManager(mContext);
        dbMan.loadUserData();
    }



    private void checkIfUserIsAuthentic(){
        if(checkIfEmailIsVerified()){
            startMainActivity();
        }else{
            openNotVerifiedPrompt();
        }
    }

    private void openNotVerifiedPrompt() {
        final Dialog d = new Dialog(this);
        d.setTitle("Email.");
        d.setContentView(R.layout.dialog_reverify_email);
        Button b1 = d.findViewById(R.id.okBtn);
        final TextView hasVerifiedText = d.findViewById(R.id.hasVerifiedText);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
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
