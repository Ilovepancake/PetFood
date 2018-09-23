package com.bry.petfood.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bry.petfood.Fragments.CartFragment;
import com.bry.petfood.Fragments.CompareFragment;
import com.bry.petfood.Fragments.PurchaseHistoryFragment;

public class CollapsibleBottomNavBarPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_ITEMS = 3;

    public CollapsibleBottomNavBarPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            PurchaseHistoryFragment frag = new PurchaseHistoryFragment();
            frag.setPos(position);
            return frag;
        }else if(position == 1){
            CartFragment frag = new CartFragment();
            frag.setPos(position);
            return frag;
        }else{
            CompareFragment frag = new CompareFragment();
            frag.setPos(position);
            return frag;
        }

    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}
