package com.bry.petfood.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bry.petfood.Fragments.CartFragment;
import com.bry.petfood.Fragments.CompareFragment;
import com.bry.petfood.Fragments.PurchaseHistoryFragment;
import com.bry.petfood.Fragments.UserAccountFragment;

public class CollapsibleBottomNavBarPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_ITEMS = 4;
    private Context mContext;

    public CollapsibleBottomNavBarPagerAdapter(FragmentManager fragmentManager,Context context) {
        super(fragmentManager);
        this.mContext = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public float getPageWidth (int position) {
        return super.getPageWidth(position);
//        return 0.93f;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            UserAccountFragment frag = new UserAccountFragment();
            frag.setPos(position);
            frag.setContext(mContext);
            return frag;
        }else if(position == 1){
            PurchaseHistoryFragment frag = new PurchaseHistoryFragment();
            frag.setPos(position);
            frag.setContext(mContext);
            return frag;
        }else if(position == 2){
            CartFragment frag = new CartFragment();
            frag.setPos(position);
            frag.setContext(mContext);
            return frag;
        }else{
            CompareFragment frag = new CompareFragment();
            frag.setPos(position);
            frag.setContext(mContext);
            return frag;
        }

    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}
