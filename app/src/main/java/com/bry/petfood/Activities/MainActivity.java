package com.bry.petfood.Activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Adapters.CollapsibleBottomNavBarPagerAdapter;
import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Constants;
import com.bry.petfood.Fragments.FeedbackFragment;
import com.bry.petfood.Fragments.FragmentModalBottomSheet;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Services.Utils;
import com.bry.petfood.Variables;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.PlaceHolderView;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActitivy";
    private Context mContext;

    @Bind(R.id.viewPagerRelative) RelativeLayout mCollapsibleBottomNavLayout;
    @Bind(R.id.userAccountBtn) ImageButton mUserAccountBtn;
    @Bind(R.id.purchaseHistoryBtn) ImageButton mPurchaseHistoryBtn;
    @Bind(R.id.settingsBtn) ImageButton mSettingsBtn;
    @Bind(R.id.cartBtn) ImageButton mCartBtn;
    @Bind(R.id.compareBtn) ImageButton mCompareBtn;
    @Bind(R.id.vpPager) ViewPager mViewPager;
    @Bind(R.id.HistoryDot) View mHistoryDot;
    @Bind(R.id.CartDot) View mCartDot;
    @Bind(R.id.CompareDot) View mCompareDot;
    @Bind(R.id.UserAccountDot) View mUserAccountDot;
    @Bind(R.id.bottomTouchView)View mBottomTouchView;
    @Bind(R.id.bottomNavLayouts) LinearLayout mBottomNavLayouts;
    @Bind(R.id.viewPagerLinear) LinearLayout mViewPagerLinear;

    @Bind(R.id.settingsViewLayout) LinearLayout mSettingsViewLayout;
    @Bind(R.id.topSettingsView) RelativeLayout mTopSettingsView;
    @Bind(R.id.backBtnForSettings) ImageButton mBackBtnForSettings;
    @Bind(R.id.darkThemeLayout) LinearLayout mDarkThemeLayout;
    @Bind(R.id.DarkModeSwitch) Switch DarkModeSwitch;
    @Bind(R.id.AccountBtn) LinearLayout mAccountBtn;
    @Bind(R.id.LogoutLayout) LinearLayout mLogoutLayout;
    @Bind(R.id.SettingsTouchView) View mSettingsTouchView;
    @Bind(R.id.rightTouchView) View mRightTouchView;
    @Bind(R.id.FeedbackView) LinearLayout mFeedbackView;
    @Bind(R.id.VersiontLayout) LinearLayout mVersiontLayout;

    @Bind(R.id.searchFoodLayout) LinearLayout mSearchFoodLayout;
    @Bind(R.id.searchFoodCard) CardView mSearchFoodCard;

    @Bind(R.id.searchIcon) ImageButton mSearchIcon;
    @Bind(R.id.searchEditText) AutoCompleteTextView searchEditText;
    @Bind(R.id.expandedViewIcon) ImageButton expandedViewIcon;

    @Bind(R.id.cataloguePlaceHolderView)PlaceHolderView cataloguePlaceHolderView;
    @Bind(R.id.loadingLayout) LinearLayout mLoadingLayout;

    private final String[] COUNTRIES = new String[] {"Dog", "Cat", "Rabbit", "Hamster", "Guinea Pig"};

    private final int collapsedMargin = Utils.dpToPx(550);
    private final int unCollapsedMargin = Utils.dpToPx(1);
    private boolean isCardCollapsed = true;
    private boolean mIsScrolling = false;
    private GestureDetector mDetector;
    private int _yDelta;
    private boolean isInTransition = false;
    private final int normalDuration = 320;
    private List<Integer> RawList = new ArrayList<>();
    private FragmentPagerAdapter adapterViewPager;


    private final int SettingsCollapsedMargin = Utils.dpToPx(350);
    private final int SettingsUnCollapsedMargin = Utils.dpToPx(0);
    private boolean isSettingsCardCollapsed = true;
    private boolean mIsSettingsScrolling = false;

    private GestureDetector mSettingsDetector;
    private int _xDelta;

    private boolean isSettingsInTransition = false;
    private final int normalSettingsDuration = 320;

    private List<Integer> SettingsRawList = new ArrayList<>();
    private List<FoodItem> allFoodItems = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        loadItemsFromFirebase();
        seTopSearchBarStuff();
        addTouchListenerForSettingsOne();
        loadBottomViews();
        setClickListeners();
        settingsBtnListener();

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForShowBottomSheet,new IntentFilter(Constants.SHOW_PAYOUT));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForShowBottomSheet);
    }




    private void showLoadingLayoutNHideViews(){
        Log("Hiding views");
        mLoadingLayout.setVisibility(View.VISIBLE);
        mCollapsibleBottomNavLayout.setVisibility(View.INVISIBLE);
        mSearchFoodLayout.setVisibility(View.INVISIBLE);
        cataloguePlaceHolderView.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingLayoutNShowViews(){
        Log("Showing views");
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mCollapsibleBottomNavLayout.setVisibility(View.VISIBLE);
        mSearchFoodLayout.setVisibility(View.VISIBLE);
        cataloguePlaceHolderView.setVisibility(View.VISIBLE);
    }


    private void Log(String message){
        Log.d(TAG,message);
    }




    private void setClickListeners() {
        mCompareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCardCollapsed) {
                    setRightView();
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }else{
                    setRightView();
                }
            }
        });

        mCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCardCollapsed) {
                    setCentreView();
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }else{
                    setCentreView();
                }
            }
        });

        mPurchaseHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCardCollapsed) {
                    setLeftView();
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }else{
                    setLeftView();
                }
            }
        });

        mUserAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCardCollapsed) {
                    setCompleteLeftView();
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }else{
                    setCompleteLeftView();
                }
            }
        });
    }

    private void seTopSearchBarStuff() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, COUNTRIES);
        searchEditText.setThreshold(1);
        searchEditText.setAdapter(adapter);
        searchEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAndAddItems(searchEditText.getText().toString().trim());
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        searchEditText.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT) ||
                        (actionId == EditorInfo.IME_ACTION_GO)) {
                    Log("Enter pressed");
                    if(searchEditText.getText().toString().trim().equals("")){
                        unfilterEverything();
                    }else filterAndAddItems(searchEditText.getText().toString().trim());
                    searchEditText.setText("");
//                    Toast.makeText(mContext,"SearchedItem"+v.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }


    }

    private void unfilterEverything(){
        cataloguePlaceHolderView.removeAllViews();
        loadFoodItemsIntoViews();
    }

    private void filterAndAddItems(String phrase){
        Log.d(TAG,"Filtering items");
        cataloguePlaceHolderView.removeAllViews();
        for(FoodItem item:allFoodItems){
            if(item.getCategory().equals(phrase)){
                cataloguePlaceHolderView.addView(new FoodItemCard(mContext,cataloguePlaceHolderView,item));
            }
        }
    }

    private void loadItemsFromFirebase() {
        showLoadingLayoutNHideViews();
        Log.d(TAG,"Loading items from firebase");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.LISTED_ITEMS);
        Log.d(TAG,"Query set up: "+Constants.LISTED_ITEMS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"Loaded datas: "+dataSnapshot.toString());
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    String category = snap.child("category").getValue(String.class);
                    String description = snap.child("description").getValue(String.class);
                    String name = snap.child("name").getValue(String.class);
                    Double price = snap.child("price").getValue(Double.class);
                    String pushId = snap.child("pushId").getValue(String.class);
                    Double quantity = snap.child("quantityAvailable").getValue(Double.class);

                    FoodItem item = new FoodItem(name,pushId,description,quantity,price,category);
                    Log.d(TAG,"Adding item : "+item.getName());
                    allFoodItems.add(item);
                }
                Log.d(TAG,"Done Loading items from firebase: "+allFoodItems.size()+" items found.");
                loadFoodItemsImage();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int iterator = 0;
    private void loadFoodItemsImage(){
        iterator = 0;
        Log.d(TAG,"Loading items' images from firebase");
        for(FoodItem item:allFoodItems){

            DatabaseReference adRef3 = FirebaseDatabase.getInstance().getReference(Constants.LISTED_ITEMS_IMAGES)
                    .child(item.getPushId()).child(Constants.IMG1);
            Log.d(TAG,"Query set up: "+Constants.LISTED_ITEMS_IMAGES+" : "+item.getPushId()+" : "+Constants.IMG1);
            adRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG,"Loaded item image.");
                    allFoodItems.get(iterator).setImage(dataSnapshot.getValue(String.class));
                    iterator++;
                    if(iterator==allFoodItems.size()){
                        Log.d(TAG,"Done Loading items from firebase");
                        processTheImages();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,"Error: "+databaseError.getMessage());
                    Log.e(TAG,"Error: "+databaseError.getDetails());
                }
            });
        }
    }

    private void processTheImages() {
        Log.d(TAG,"Processint items' images.");
        new LongOperationFI().execute("");
    }

    private void loadFoodItemsIntoViews(){
        Log.d(TAG,"Loading items into views.");
        for(FoodItem item:allFoodItems){
            cataloguePlaceHolderView.addView(new FoodItemCard(mContext,cataloguePlaceHolderView,item));
        }
        hideLoadingLayoutNShowViews();
    }


    private void loadBottomViews() {
        mCollapsibleBottomNavLayout.setVisibility(View.VISIBLE);
        adapterViewPager = new CollapsibleBottomNavBarPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
//                if(position==0){
//                    mUserAccountDot.setVisibility(View.VISIBLE);
//                    mHistoryDot.setVisibility(View.INVISIBLE);
//                    mCartDot.setVisibility(View.INVISIBLE);
//                    mCompareDot.setVisibility(View.INVISIBLE);
//                }else
                    if(position == 0){
                    mUserAccountDot.setVisibility(View.INVISIBLE);
                    mHistoryDot.setVisibility(View.VISIBLE);
                    mCartDot.setVisibility(View.INVISIBLE);
                    mCompareDot.setVisibility(View.INVISIBLE);
                }else if(position == 1){
                    mUserAccountDot.setVisibility(View.INVISIBLE);
                    mHistoryDot.setVisibility(View.INVISIBLE);
                    mCartDot.setVisibility(View.VISIBLE);
                    mCompareDot.setVisibility(View.INVISIBLE);
                }else if(position == 2){
                    mUserAccountDot.setVisibility(View.INVISIBLE);
                    mHistoryDot.setVisibility(View.INVISIBLE);
                    mCartDot.setVisibility(View.INVISIBLE);
                    mCompareDot.setVisibility(View.VISIBLE);
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        isCardCollapsed = true;
        updateUpPosition(collapsedMargin,0);
        addTouchListener();
    }

    private void addTouchListener() {
        mDetector = new GestureDetector(this, new MyGestureListener());
        mCollapsibleBottomNavLayout.setOnTouchListener(touchListener);
        mCompareBtn.setOnTouchListener(touchListener);
        mCartBtn.setOnTouchListener(touchListener);
        mPurchaseHistoryBtn.setOnTouchListener(touchListener);
        mUserAccountBtn.setOnTouchListener(touchListener);
        mBottomTouchView.setOnTouchListener(touchListener);
    }


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mDetector.onTouchEvent(event)) {
                return true;
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(mIsScrolling) {
                    Log.d("touchListener"," onTouch ACTION_UP");
                    mIsScrolling  = false;
                    if(!isCardCollapsed){
                        if(mCollapsibleBottomNavLayout.getTranslationY()>50)mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()-50);
                        updateUpPosition(unCollapsedMargin,normalDuration);
                    }else{
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()+50);
                        updateUpPosition(collapsedMargin,normalDuration);
                    }
                }else{
                    if(v.equals(mPurchaseHistoryBtn)){
                        mPurchaseHistoryBtn.performClick();
                    }else if(v.equals(mCartBtn)){
                        mCartBtn.performClick();
                    }else if(v.equals(mCompareBtn)){
                        mCompareBtn.performClick();
                    }else if(v.equals(mUserAccountBtn)){
                        mUserAccountBtn.performClick();
                    }else if(v.equals(mBottomTouchView)){
//                       mDetector.onTouchEvent(event);
                    }
                }
            }
            return false;
        }
    };

    private boolean onCustomTouch(View v,MotionEvent event){

        return false;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        int origX = 0;
        int origY = 0;


        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            Log.d("TAG","onDown: event.getRawX(): "+event.getRawX()+" event.getRawY()"+event.getRawY());
            CoordinatorLayout.LayoutParams lParams = (CoordinatorLayout.LayoutParams) mCollapsibleBottomNavLayout.getLayoutParams();
            _yDelta = Y - lParams.topMargin;

            origX = lParams.leftMargin;
            origY = lParams.topMargin;
            RawList.clear();

            if(isCardCollapsed) {
//                if (X < 180) setCompleteLeftView();
//                else
                    if (X < 380) setLeftView();
                else if(X<580)setCentreView();
                else setRightView();
            }

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final int Y = (int) e2.getRawY();
           if(isSettingsCardCollapsed) {
               if ((Y - _yDelta) < unCollapsedMargin) {
                   updateUpPosition(unCollapsedMargin, 0);
               } else if ((Y - _yDelta) > collapsedMargin) {
//                   updateUpPosition(collapsedMargin, 0);
               } else {
                   updateUpPosition((Y - _yDelta), 0);
               }
               Log.d("TAG", "the e2.getAction()= " + e2.getAction() + " and the MotionEvent.ACTION_CANCEL= " + MotionEvent.ACTION_CANCEL);
               RawList.add(Y);
               mIsScrolling = true;
           }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int UpScore = 0;
            int DownScore = 0;
            if(RawList.size()>15){
                for(int i= RawList.size()-1 ;i>15;i--){
                    int num1 = RawList.get(i);
                    int numBefore1 = RawList.get(i - 1);

                    if(numBefore1>num1) DownScore++;
                    else UpScore++;
                }
            }else{
                for(int i= RawList.size()-1 ;i>0;i--){
                    int num1 = RawList.get(i);
                    int numBefore1 = RawList.get(i - 1);

                    if(numBefore1>num1) DownScore++;
                    else UpScore++;
                }
            }

            if(isSettingsCardCollapsed) {
                if (UpScore < DownScore) {
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() > unCollapsedMargin) {
                        if (mCollapsibleBottomNavLayout.getTranslationY() > 300)
                            mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 150);
                        updateUpPosition(unCollapsedMargin, normalDuration);
                    }
                } else {
                    isCardCollapsed = true;
                    if (mCollapsibleBottomNavLayout.getTranslationY() > collapsedMargin) {
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() + 150);
                        updateUpPosition(collapsedMargin, normalDuration);
                    }
                }
            }

            RawList.clear();
            return false;

        }
    }

    private void updateUpPosition(int y_pos,int duration){
        if(!isInTransition){
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mCollapsibleBottomNavLayout.getLayoutParams();
            layoutParams.bottomMargin = -unCollapsedMargin;
            layoutParams.topMargin = unCollapsedMargin;
            mCollapsibleBottomNavLayout.setLayoutParams(layoutParams);
            mCollapsibleBottomNavLayout.setTranslationY(collapsedMargin);
            isInTransition = true;
            onBottomLayoutExpand();
//            mBottomNavLayouts.setVisibility(View.VISIBLE);
//            mViewPagerLinear.setBackgroundColor(Color.TRANSPARENT);
        }

        Log.d("TAG","updateUpPosition y_pos: "+y_pos);

        mCollapsibleBottomNavLayout.animate().setDuration(duration).translationY(y_pos)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(mCollapsibleBottomNavLayout.getTranslationY()==collapsedMargin){
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mCollapsibleBottomNavLayout.getLayoutParams();
                    layoutParams.bottomMargin = -collapsedMargin;
                    layoutParams.topMargin = collapsedMargin;
                    mCollapsibleBottomNavLayout.setLayoutParams(layoutParams);
                    mCollapsibleBottomNavLayout.setTranslationY(unCollapsedMargin);
                    isInTransition = false;
                    onBottomLayoutCollapse();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        int rounded = (((y_pos/1100) + 99) / 100 );
        Log.d("TAG","rounded: "+rounded);

//        mBottomNavLayouts.animate().alpha((y_pos/1100)*1f).setDuration(duration).setListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                if(isCardCollapsed && !isInTransition){
//                    mBottomNavLayouts.setVisibility(View.VISIBLE);
////                    mViewPagerLinear.setBackgroundColor(Color.WHITE);
//                }else if(!isCardCollapsed && !isInTransition){
//                    mBottomNavLayouts.setVisibility(View.INVISIBLE);
////                    mViewPagerLinear.setBackgroundColor(Color.TRANSPARENT);
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
    }

    private void onBottomLayoutCollapse(){
        mUserAccountDot.setVisibility(View.INVISIBLE);
        mHistoryDot.setVisibility(View.INVISIBLE);
        mCartDot.setVisibility(View.INVISIBLE);
        mCompareDot.setVisibility(View.INVISIBLE);
        RawList.clear();
        mRightTouchView.setVisibility(View.VISIBLE);
    }

    private void onBottomLayoutExpand(){
        RawList.clear();
        mRightTouchView.setVisibility(View.GONE);
    }

    private void setCompleteLeftView(){
        mViewPager.setCurrentItem(0);
        mUserAccountDot.setVisibility(View.VISIBLE);
        mHistoryDot.setVisibility(View.INVISIBLE);
        mCartDot.setVisibility(View.INVISIBLE);
        mCompareDot.setVisibility(View.INVISIBLE);
    }

    private void setLeftView(){
        mViewPager.setCurrentItem(1);
        mUserAccountDot.setVisibility(View.INVISIBLE);
        mHistoryDot.setVisibility(View.VISIBLE);
        mCartDot.setVisibility(View.INVISIBLE);
        mCompareDot.setVisibility(View.INVISIBLE);
    }

    private void setCentreView(){
        mViewPager.setCurrentItem(2);
        mUserAccountDot.setVisibility(View.INVISIBLE);
        mHistoryDot.setVisibility(View.INVISIBLE);
        mCartDot.setVisibility(View.VISIBLE);
        mCompareDot.setVisibility(View.INVISIBLE);
    }

    private void setRightView(){
        mViewPager.setCurrentItem(3);
        mUserAccountDot.setVisibility(View.INVISIBLE);
        mHistoryDot.setVisibility(View.INVISIBLE);
        mCartDot.setVisibility(View.INVISIBLE);
        mCompareDot.setVisibility(View.VISIBLE);
    }







    private void addTouchListenerForSettingsOne(){
        isSettingsCardCollapsed = true;
        findViewById(R.id.settingsViewLayout).setVisibility(View.VISIBLE);
        updateSettingsLeftPosition(SettingsCollapsedMargin,0);
        mSettingsDetector = new GestureDetector(this, new MySettingsGestureListener());
        mTopSettingsView.setOnTouchListener(SettingsTouchListener);
        mSettingsBtn.setOnTouchListener(SettingsTouchListener);
        mSettingsTouchView.setOnTouchListener(SettingsTouchListener);
        mRightTouchView.setOnTouchListener(SettingsTouchListener);
        mBackBtnForSettings.setOnTouchListener(SettingsTouchListener);

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSettingsCardCollapsed = false;
                if(mSettingsViewLayout.getTranslationX()!=SettingsUnCollapsedMargin)mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX()-50);
                updateSettingsLeftPosition(SettingsUnCollapsedMargin,normalSettingsDuration);
            }
        });
        mBackBtnForSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSettingsCardCollapsed = true;
                if(mSettingsViewLayout.getTranslationX()!=SettingsCollapsedMargin)mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX());
                updateSettingsLeftPosition(SettingsCollapsedMargin,normalSettingsDuration);
            }
        });
    }

    View.OnTouchListener SettingsTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mSettingsDetector.onTouchEvent(event)) {
                return true;
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(mIsSettingsScrolling) {
                    Log.d("SettingsTouchListener"," onTouch ACTION_UP");
                    mIsSettingsScrolling  = false;
                    if(!isSettingsCardCollapsed){
                        mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX());
                        updateSettingsLeftPosition(SettingsUnCollapsedMargin,normalSettingsDuration);
                    }else{
                        mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX());
                        updateSettingsLeftPosition(SettingsCollapsedMargin,normalSettingsDuration);
                    }
                }else{
                    if(v.equals(mSettingsBtn)){
                        mSettingsBtn.performClick();
                    }else if(v.equals(mBackBtnForSettings)){
                        mBackBtnForSettings.performClick();
                    }
                }
            }
            return false;
        }
    };

    class MySettingsGestureListener extends GestureDetector.SimpleOnGestureListener {
        int origX = 0;
        int origY = 0;


        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            Log.d("TAG","onDown: event.getRawX(): "+event.getRawX()+" event.getRawY()"+event.getRawY());
            CoordinatorLayout.LayoutParams lParams = (CoordinatorLayout.LayoutParams) mSettingsViewLayout.getLayoutParams();
            _xDelta = X - lParams.leftMargin;

            origX = lParams.leftMargin;
            origY = lParams.topMargin;
            SettingsRawList.clear();
            if(isSettingsCardCollapsed) {

            }

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final int X = (int) e2.getRawX();
            if((X-_xDelta)<SettingsUnCollapsedMargin){
                updateSettingsLeftPosition(SettingsUnCollapsedMargin,0);
            }else if((X-_xDelta)> SettingsCollapsedMargin){
                updateSettingsLeftPosition(SettingsCollapsedMargin,0);
            }else{
                updateSettingsLeftPosition((X - _xDelta),0);
            }
            Log.d("TAG","the e2.getAction()= "+e2.getAction()+" and the MotionEvent.ACTION_CANCEL= "+MotionEvent.ACTION_CANCEL);
            SettingsRawList.add(X);
            mIsSettingsScrolling = true;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int RightScore = 0;
            int LeftScore = 0;
            if(SettingsRawList.size()>15){
                for(int i= SettingsRawList.size()-1 ;i>15;i--){
                    int num1 = SettingsRawList.get(i);
                    int numBefore1 = SettingsRawList.get(i - 1);

                    if(numBefore1>num1) LeftScore++;
                    else RightScore++;
                }
            }else{
                for(int i= SettingsRawList.size()-1 ;i>0;i--){
                    int num1 = SettingsRawList.get(i);
                    int numBefore1 = SettingsRawList.get(i - 1);

                    if(numBefore1>num1) LeftScore++;
                    else RightScore++;
                }
            }

            if(RightScore<LeftScore){
                Log.d("onFling","Expanding Settings card since leftscore is greater than right score");
                isSettingsCardCollapsed = false;
                if(mSettingsViewLayout.getTranslationX()!=SettingsUnCollapsedMargin)
                    mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX()-150);
                updateSettingsLeftPosition(SettingsUnCollapsedMargin,normalSettingsDuration);
            }else{
                Log.d("onFling","Collapsing Settings card since right score is greater than left score");
                isSettingsCardCollapsed = true;
                if(mSettingsViewLayout.getTranslationX()!=SettingsCollapsedMargin)
                    mSettingsViewLayout.setTranslationX(mSettingsViewLayout.getTranslationX()+150);
                updateSettingsLeftPosition(SettingsCollapsedMargin,normalSettingsDuration);
            }

            SettingsRawList.clear();
            return false;

        }
    }

    private void updateSettingsLeftPosition(final int x_pos, int duration){
        if(!isSettingsInTransition){
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mSettingsViewLayout.getLayoutParams();
            layoutParams.leftMargin = SettingsUnCollapsedMargin+1;
            layoutParams.rightMargin = -SettingsUnCollapsedMargin+1;
            mSettingsViewLayout.setLayoutParams(layoutParams);
            mSettingsViewLayout.setTranslationX(SettingsCollapsedMargin);
            isSettingsInTransition = true;
        }

        mSettingsViewLayout.animate().setDuration(duration).translationX(x_pos)
                .setInterpolator(new LinearOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(x_pos == SettingsCollapsedMargin){
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mSettingsViewLayout.getLayoutParams();
                    layoutParams.leftMargin = SettingsCollapsedMargin;
                    layoutParams.rightMargin = -SettingsCollapsedMargin;
                    mSettingsViewLayout.setLayoutParams(layoutParams);
                    mSettingsViewLayout.setTranslationX(SettingsUnCollapsedMargin);
                    isSettingsInTransition = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }



    @Override
    public void onBackPressed(){
        if(!isCardCollapsed){
            isCardCollapsed = true;
            if(mCollapsibleBottomNavLayout.getTranslationY()!=collapsedMargin)mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()+50);
            updateUpPosition(collapsedMargin,normalDuration);
        }else if(!isSettingsCardCollapsed){
            mBackBtnForSettings.performClick();
        }else{
            super.onBackPressed();
        }
    }


    public void settingsBtnListener(){
        mDarkThemeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDarkMode();
            }
        });

        DarkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setDarkMode();
            }
        });

        mAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserAccount();
            }
        });
        mLogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        mFeedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFeedBackFragment();
            }
        });

        mVersiontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"App version 1.0",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDarkMode(){
        DarkModeSwitch.setChecked(false);
        Toast.makeText(mContext,"Coming soon",Toast.LENGTH_SHORT).show();
    }

    private void showUserAccount(){
        Intent intent = new Intent(MainActivity.this,ListItemActivity.class);
        startActivity(intent);
    }

    private void logout(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout.");
            builder.setMessage("Are you sure you want to log out?")
                    .setCancelable(true)
                    .setPositiveButton("Yes.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logoutUser();
                        }
                    }).show();
    }

    private void logoutUser() {
        Variables.user = null;
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
        }
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFeedBackFragment(){
        FragmentManager fm = getFragmentManager();
        FeedbackFragment reportDialogFragment = new FeedbackFragment();
        reportDialogFragment.setMenuVisibility(false);
        reportDialogFragment.show(fm, "Feedback.");
        reportDialogFragment.setfragContext(mContext);
    }


    private Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        Bitmap bitm = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
//        return getResizedBitmap(bitm,1200);
        return bitm;
    }

    private void setImage(FoodItem item) {
        try {
            Bitmap bs = decodeFromFirebaseBase64(item.getImage());
            item.setImageBitmap(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LongOperationFI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try{
                for(FoodItem item:allFoodItems){
                    setImage(item);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG,"Done Processing items' images from firebase");
            loadFoodItemsIntoViews();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    BroadcastReceiver mMessageReceiverForShowBottomSheet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openBottomSheet();
        }
    };

    private void openBottomSheet(){
      Intent intent = new Intent(MainActivity.this,CompletePaymentActivity.class);
      startActivity(intent);
    }


}
