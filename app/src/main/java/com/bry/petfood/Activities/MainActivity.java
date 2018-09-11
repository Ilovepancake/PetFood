package com.bry.petfood.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActitivy";
    private Context mContext;
    @Bind(R.id.bottomNavLayouts) LinearLayout mBottomNavLayouts;
    @Bind(R.id.logoutBtn) ImageButton mLogoutBtn;
    @Bind(R.id.settingsBtn) ImageButton mSettingsBtn;
    @Bind(R.id.cartBtn) ImageButton mCartBtn;
    @Bind(R.id.checkoutBtn) ImageButton mCheckoutBtn;

    @Bind(R.id.searchFoodLayout) LinearLayout mSearchFoodLayout;
    @Bind(R.id.searchFoodCard) CardView mSearchFoodCard;
    @Bind(R.id.searchIcon) ImageButton mSearchIcon;
    @Bind(R.id.searchEditText) AutoCompleteTextView searchEditText;
    @Bind(R.id.listViewIcon) ImageButton listViewIcon;
    @Bind(R.id.expandedViewIcon) ImageButton expandedViewIcon;

    @Bind(R.id.cataloguePlaceHolderView)PlaceHolderView cataloguePlaceHolderView;
    @Bind(R.id.loadingLayout) LinearLayout mLoadingLayout;

    private final String[] COUNTRIES = new String[] {"belgium", "france", "italy", "germany", "spain"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = getApplicationContext();

        loadTestItems();
        seTopSearchBarStuff();

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


    private void showLoadingLayoutNHideViews(){
        Log("Hiding views");
        mLoadingLayout.setVisibility(View.VISIBLE);
        mBottomNavLayouts.setVisibility(View.INVISIBLE);
        mSearchFoodLayout.setVisibility(View.INVISIBLE);
        cataloguePlaceHolderView.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingLayoutNShowViews(){
        Log("Showing views");
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mBottomNavLayouts.setVisibility(View.VISIBLE);
        mSearchFoodLayout.setVisibility(View.VISIBLE);
        cataloguePlaceHolderView.setVisibility(View.VISIBLE);
    }

    private void Log(String message){
        Log.d(TAG,message);
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

}
