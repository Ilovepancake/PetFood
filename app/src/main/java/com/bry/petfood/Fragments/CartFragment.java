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

public class CartFragment extends Fragment {
    private int mPos;
    private PlaceHolderView mCartPlaceHolderView;
    private Context mContext;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setPos(int Pos){
        mPos = Pos;
    }

    public void setContext(Context context){
        this.mContext = context;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        mCartPlaceHolderView =  view.findViewById(R.id.cartPlaceHolderView);

        loadTestItems();
        return view;
    }

    private void loadTestItems() {
        List<FoodItem> testItems = createTestItems(5);
        for(FoodItem item:testItems){
            mCartPlaceHolderView.addView(new FoodItemCard(mContext,mCartPlaceHolderView,item));
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

