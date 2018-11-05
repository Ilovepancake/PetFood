package com.bry.petfood.Adapters;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Constants;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.LongClick;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.food_item_layout)
public class FoodItemCard {
    @View(R.id.titleText) private TextView mTitleTextView;
    @View(R.id.FoodItemImage) private ImageView mFoodItemImage;
    @View(R.id.priceText) private TextView mPriceText;
    @View(R.id.quantityAvailable) private TextView mQuantityAvailable;
    @View(R.id.addToCartImageBtn) private ImageButton mAddToCartBtn;
    @View(R.id.addToCompareImageBtn) private ImageButton addToCompareBtn;

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
        mTitleTextView.setText(mFoodItem.getName());
        mFoodItemImage.setImageBitmap(mFoodItem.getImageBitmap());
        mPriceText.setText("Price: "+mFoodItem.getPrice());
        mQuantityAvailable.setText(mFoodItem.getQuantityAvailable()+" available");

        addToCompareBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
//                compareItem();
            }
        });

        addToCompareBtn.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event) {
                compareItem();
                return false;
            }
        });
    }

    private void compareItem() {
        if(!Variables.compareItems.contains(mFoodItem)) {
            Variables.itemToBeAddedToCompare = mFoodItem;
            Variables.compareItems.add(mFoodItem);
            Toast.makeText(mContext, "Added compare item", Toast.LENGTH_SHORT).show();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.ADD_ITEM_COMPARE_INTENT));
        }
    }

    @Click(R.id.addToCartImageBtn)
    private void onClick(){
        addItemtoCart();
    }

    @LongClick(R.id.addToCompareImageBtn)
    private void onLongClick(){
        compareItem();
    }

    private void addItemtoCart() {
        if(!Variables.cartItems.contains(mFoodItem)){
            Variables.itemToBeAddedToCart = mFoodItem;
            Variables.cartItems.add(mFoodItem);
            Toast.makeText(mContext,"Added item.",Toast.LENGTH_SHORT).show();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.ADD_ITEM_INTENT));
        }

    }

}
