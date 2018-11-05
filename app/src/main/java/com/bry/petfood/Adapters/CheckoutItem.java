package com.bry.petfood.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.food_item_in_cart)
public class CheckoutItem {
    @View(R.id.titleText) private TextView mTitleTextView;
    @View(R.id.FoodItemImage) private ImageView mFoodItemImage;
    @View(R.id.priceText) private TextView mPriceText;
    @View(R.id.quantityAvailable) private TextView mQuantityAvailable;
    @View(R.id.removeFromCartBtn) private CardView mRemoveFromCartBtn;

    private Context mContext;
    private PlaceHolderView mPlaceHolderView;
    private FoodItem mFoodItem;

    public CheckoutItem(Context context, PlaceHolderView phView, FoodItem foodItem){
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
    }

    @Click(R.id.removeImage)
    private void onClick(){
        RemoveItemFromCart();
    }

    private void RemoveItemFromCart() {
        Variables.cartItems.remove(mFoodItem);
        mPlaceHolderView.removeView(this);
    }
}
