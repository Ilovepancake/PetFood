package com.bry.petfood.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;

import com.bry.petfood.Adapters.CheckoutItem;
import com.bry.petfood.Adapters.FoodItemInCart;
import com.bry.petfood.Fragments.FragmentModalBottomSheet;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bry.petfood.Variables;
import com.mindorks.placeholderview.PlaceHolderView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CompletePaymentActivity extends AppCompatActivity {
    @Bind(R.id.cartPlaceHolderView) PlaceHolderView cartPlaceHolderView;
    @Bind(R.id.checkoutBtn) CardView checkoutBtn;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);
        ButterKnife.bind(this);
        createProgressDialog();
        addCheckoutItems();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentModalBottomSheet fragmentModalBottomSheet = new FragmentModalBottomSheet();
                fragmentModalBottomSheet.setActivity(CompletePaymentActivity.this);
                fragmentModalBottomSheet.setDetails(getTotal());
                fragmentModalBottomSheet.show(getSupportFragmentManager(),"BottomSheet Fragment");
            }
        });
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiverForPaymentRequestToBeMade,
                new IntentFilter("START_PAYMENTS_INTENT"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiverForPaymentRequestToBeMade);
    }

    private void addCheckoutItems() {
        for(FoodItem item: Variables.cartItems){
            cartPlaceHolderView.addView(new CheckoutItem(getApplicationContext(),cartPlaceHolderView, item));
        }
    }

    private int getTotal(){
        int total = 0;
        for(FoodItem item:Variables.cartItems){
            total+=item.getPrice();
        }
        return total;
    }


    BroadcastReceiver mMessageReceiverForPaymentRequestToBeMade = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAuthProgressDialog.show();
        }
    };

    private void createProgressDialog(){
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(R.string.app_name);
        mAuthProgressDialog.setMessage("Initiating request...");
        mAuthProgressDialog.setCancelable(false);

    }
}
