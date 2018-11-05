package com.bry.petfood.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bry.petfood.Adapters.FoodItemCard;
import com.bry.petfood.Adapters.FoodItemInCart;
import com.bry.petfood.Constants;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private int mPos;
    private PlaceHolderView mCartPlaceHolderView;
    private Context mContext;
    private CardView checkoutBtn;


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
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverForAddItemToCart,new IntentFilter(Constants.ADD_ITEM_INTENT));

    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        mCartPlaceHolderView =  view.findViewById(R.id.cartPlaceHolderView);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);
//        loadTestItems();
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Variables.cartItems.isEmpty()){
                    Toast.makeText(mContext,"Your cart is empty",Toast.LENGTH_SHORT).show();
                }else LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.SHOW_PAYOUT));

            }
        });
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverForAddItemToCart);
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

    BroadcastReceiver mMessageReceiverForAddItemToCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(mContext,"Received message to add to cart",Toast.LENGTH_SHORT).show();
            checkoutBtn.setVisibility(View.VISIBLE);
            mCartPlaceHolderView.addView(new FoodItemInCart(mContext,mCartPlaceHolderView, Variables.itemToBeAddedToCart));
        }
    };

}

