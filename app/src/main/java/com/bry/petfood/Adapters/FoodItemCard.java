package com.bry.petfood.Adapters;


import android.content.Context;
import android.widget.TextView;

import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.food_item_layout)
public class FoodItemCard {
    @View(R.id.titleText) private TextView mTitleTextView;

    private Context mContext;
    private PlaceHolderView mPlaceHolderView;
    private FoodItem mFoodItem;

    public FoodItemCard(Context context, PlaceHolderView phView, FoodItem foodItem){
        this.mContext = context;
        this.mPlaceHolderView = phView;
        this.mFoodItem = foodItem;
    }

    @Resolve
    private void onResolved(){
        mTitleTextView.setText("Food item --"+mFoodItem.getName());
    }

}
