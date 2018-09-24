package com.bry.petfood.Activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.mindorks.placeholderview.PlaceHolderView;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActitivy";
    private Context mContext;

    @Bind(R.id.viewPagerLinear) LinearLayout mCollapsibleBottomNavLayout;
    @Bind(R.id.purchaseHistoryBtn) ImageButton mPurchaseHistoryBtn;
    @Bind(R.id.settingsBtn) ImageButton mSettingsBtn;
    @Bind(R.id.cartBtn) ImageButton mCartBtn;
    @Bind(R.id.compareBtn) ImageButton mCompareBtn;
    @Bind(R.id.vpPager) ViewPager mViewPager;
    @Bind(R.id.HistoryDot) View mHistoryDot;
    @Bind(R.id.CartDot) View mCartDot;
    @Bind(R.id.CompareDot) View mCompareDot;

    @Bind(R.id.settingsViewLayout) LinearLayout mSettingsViewLayout;
    @Bind(R.id.topSettingsView) RelativeLayout mTopSettingsView;
    @Bind(R.id.backBtnForSettings) ImageButton mBackBtnForSettings;
    @Bind(R.id.darkThemeLayout) LinearLayout mDarkThemeLayout;
    @Bind(R.id.DarkModeSwitch) Switch DarkModeSwitch;
    @Bind(R.id.AccountBtn) LinearLayout mAccountBtn;
    @Bind(R.id.LogoutLayout) LinearLayout mLogoutLayout;
    @Bind(R.id.SettingsTouchView) View mSettingsTouchView;
    @Bind(R.id.FeedbackView) LinearLayout mFeedbackView;
    @Bind(R.id.VersiontLayout) LinearLayout mVersiontLayout;

    @Bind(R.id.searchFoodLayout) LinearLayout mSearchFoodLayout;
    @Bind(R.id.searchFoodCard) CardView mSearchFoodCard;

    @Bind(R.id.searchIcon) ImageButton mSearchIcon;
    @Bind(R.id.searchEditText) AutoCompleteTextView searchEditText;
    @Bind(R.id.expandedViewIcon) ImageButton expandedViewIcon;

    @Bind(R.id.cataloguePlaceHolderView)PlaceHolderView cataloguePlaceHolderView;
    @Bind(R.id.loadingLayout) LinearLayout mLoadingLayout;

    private final String[] COUNTRIES = new String[] {"dog", "cat", "rabbit", "hamster", "parrot"};

    private final int collapsedMargin = 1100;
    private final int unCollapsedMargin = 1;
    private boolean isCardCollapsed = true;
    private boolean mIsScrolling = false;
    private GestureDetector mDetector;
    private int _yDelta;
    private boolean isInTransition = false;
    private final int normalDuration = 120;
    private List<Integer> RawList = new ArrayList<>();
    private FragmentPagerAdapter adapterViewPager;


    private final int SettingsCollapsedMargin = 700;
    private final int SettingsUnCollapsedMargin = 0;
    private boolean isSettingsCardCollapsed = true;
    private boolean mIsSettingsScrolling = false;

    private GestureDetector mSettingsDetector;
    private int _xDelta;

    private boolean isSettingsInTransition = false;
    private final int normalSettingsDuration = 120;

    private List<Integer> SettingsRawList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        loadTestItems();
        seTopSearchBarStuff();
        addTouchListenerForSettingsOne();
        loadBottomViews();
        setClickListeners();
        settingsBtnListener();
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

    private List<FoodItem> createTestItems(int numberOfItems){
        List<FoodItem> myTestList = new ArrayList<>();
        for(int i = 0;i<numberOfItems; i++){
            FoodItem myFood = new FoodItem();
            myFood.setName("Food-item "+i);
            myTestList.add(myFood);
        }
        return myTestList;
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
    }

    private void seTopSearchBarStuff() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, COUNTRIES);
        searchEditText.setThreshold(1);
        searchEditText.setAdapter(adapter);
        searchEditText.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT) ||
                        (actionId == EditorInfo.IME_ACTION_GO)) {
                    Log("Enter pressed");
                    searchEditText.setText("");
                    Toast.makeText(mContext,"SearchedItem"+v.getText().toString(),Toast.LENGTH_SHORT).show();
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

    private void loadTestItems() {
        List<FoodItem> testItems = createTestItems(5);
        for(FoodItem item:testItems){
            cataloguePlaceHolderView.addView(new FoodItemCard(mContext,cataloguePlaceHolderView,item));
        }
    }




    private void loadBottomViews() {
        mCollapsibleBottomNavLayout.setVisibility(View.VISIBLE);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(12);
        adapterViewPager = new CollapsibleBottomNavBarPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    mHistoryDot.setVisibility(View.VISIBLE);
                    mCartDot.setVisibility(View.INVISIBLE);
                    mCompareDot.setVisibility(View.INVISIBLE);
                }else if(position == 1){
                    mHistoryDot.setVisibility(View.INVISIBLE);
                    mCartDot.setVisibility(View.VISIBLE);
                    mCompareDot.setVisibility(View.INVISIBLE);
                }else if(position == 2){
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
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()-50);
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
                   }
                }
            }
            return false;
        }
    };

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

            if(isCardCollapsed) {
                if (X < 300) setLeftView();
                else if(X>500)setRightView();
                else setCentreView();
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
                   updateUpPosition(collapsedMargin, 0);
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
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 150);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                } else {
                    isCardCollapsed = true;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != collapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() + 150);
                    updateUpPosition(collapsedMargin, normalDuration);
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
        }

        mCollapsibleBottomNavLayout.animate().setDuration(duration).translationY(y_pos)
                .setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
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
    }

    private void onBottomLayoutCollapse(){
        mHistoryDot.setVisibility(View.INVISIBLE);
        mCartDot.setVisibility(View.INVISIBLE);
        mCompareDot.setVisibility(View.INVISIBLE);
    }

    private void setLeftView(){
        mViewPager.setCurrentItem(0);
    }

    private void setCentreView(){
        mViewPager.setCurrentItem(1);
    }

    private void setRightView(){
        mViewPager.setCurrentItem(2);
    }







    private void addTouchListenerForSettingsOne(){
        isSettingsCardCollapsed = true;
        findViewById(R.id.settingsViewLayout).setVisibility(View.VISIBLE);
        updateSettingsLeftPosition(SettingsCollapsedMargin,0);
        mSettingsDetector = new GestureDetector(this, new MySettingsGestureListener());
        mTopSettingsView.setOnTouchListener(SettingsTouchListener);
        mSettingsBtn.setOnTouchListener(SettingsTouchListener);
        mSettingsTouchView.setOnTouchListener(SettingsTouchListener);
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
                .setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
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
        Toast.makeText(mContext,"Comimg soon",Toast.LENGTH_SHORT).show();
    }

    private void showUserAccount(){
        Intent intent = new Intent(MainActivity.this,MyAccountActivity.class);
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
        Toast.makeText(mContext,"Feedback",Toast.LENGTH_SHORT).show();
    }





}
