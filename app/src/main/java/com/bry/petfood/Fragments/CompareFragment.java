package com.bry.petfood.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.mindorks.placeholderview.PlaceHolderView;

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
    }

    public void setPos(int Pos){
        this.mPos = Pos;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compare_fragment, container, false);
        mComparePlaceHolderView =  view.findViewById(R.id.comparePlaceHolderView);

        loadTestItems();
        return view;
    }

    private void loadTestItems() {
        List<FoodItem> testItems = createTestItems(5);
        for(FoodItem item:testItems){
            mComparePlaceHolderView.addView(new FoodItemCard(mContext,mComparePlaceHolderView,item));
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


}