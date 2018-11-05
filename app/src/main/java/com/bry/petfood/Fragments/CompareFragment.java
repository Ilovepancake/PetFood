package com.bry.petfood.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Adapters.FoodItemInCompare;
import com.bry.petfood.Constants;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;

import java.util.ArrayList;
import java.util.List;

public class CompareFragment extends Fragment {
    private int mPos;
    private Context mContext;
    private PlaceHolderView mComparePlaceHolderView;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setContext(Context context){
        this.mContext = context;
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForAddItemToCart,new IntentFilter(Constants.ADD_ITEM_COMPARE_INTENT));

    }

    public void setPos(int Pos){
        this.mPos = Pos;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compare_fragment, container, false);
        mComparePlaceHolderView =  view.findViewById(R.id.comparePlaceHolderView);

//        loadTestItems();

        return view;
    }

    private void loadTestItems() {
        List<FoodItem> testItems = createTestItems(5);
        for(FoodItem item:testItems){
            mComparePlaceHolderView.addView(new FoodItemInCompare(mContext,mComparePlaceHolderView,item));
        }
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForAddItemToCart);
    }

    BroadcastReceiver mMessageReceiverForAddItemToCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(mContext,"Received message to add to compare",Toast.LENGTH_SHORT).show();
            mComparePlaceHolderView.addView(new FoodItemInCompare(mContext,mComparePlaceHolderView, Variables.itemToBeAddedToCart));
        }
    };


}