package com.bry.petfood.Activities;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Adapters.CollapsibleBottomNavBarPagerAdapter;
import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.crashlytics.android.Crashlytics;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        loadTestItems();
        seTopSearchBarStuff();
        loadBottomViews();
        setListeners();
    }

    private void setListeners() {
        mCompareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRightView();
                if(isCardCollapsed) {
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }
            }
        });

        mCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCentreView();
                if(isCardCollapsed) {
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }
            }
        });

        mPurchaseHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLeftView();
                if(isCardCollapsed) {
                    isCardCollapsed = false;
                    if (mCollapsibleBottomNavLayout.getTranslationY() != unCollapsedMargin)
                        mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY() - 50);
                    updateUpPosition(unCollapsedMargin, normalDuration);
                }
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettings();
            }
        });
    }

    private void loadSettings() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
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
        adapterViewPager = new CollapsibleBottomNavBarPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapterViewPager);

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
            if((Y-_yDelta)<unCollapsedMargin){
                updateUpPosition(unCollapsedMargin,0);
            }else if((Y-_yDelta)>collapsedMargin){
                updateUpPosition(collapsedMargin,0);
            }else{
                updateUpPosition((Y - _yDelta),0);
            }
            Log.d("TAG","the e2.getAction()= "+e2.getAction()+" and the MotionEvent.ACTION_CANCEL= "+MotionEvent.ACTION_CANCEL);
            RawList.add(Y);
            mIsScrolling = true;
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

            if(UpScore<DownScore){
                isCardCollapsed = false;
                if(mCollapsibleBottomNavLayout.getTranslationY()!=unCollapsedMargin)mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()-50);
                updateUpPosition(unCollapsedMargin,normalDuration);
            }else{
                isCardCollapsed = true;
                if(mCollapsibleBottomNavLayout.getTranslationY()!=collapsedMargin)mCollapsibleBottomNavLayout.setTranslationY(mCollapsibleBottomNavLayout.getTranslationY()+50);
                updateUpPosition(collapsedMargin,normalDuration);
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

    private void setLeftView(){
        mViewPager.setCurrentItem(0);
    }

    private void setCentreView(){
        mViewPager.setCurrentItem(1);
    }

    private void setRightView(){
        mViewPager.setCurrentItem(2);
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



}
