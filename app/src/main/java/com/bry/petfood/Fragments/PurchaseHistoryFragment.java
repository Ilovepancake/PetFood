package com.bry.petfood.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bry.petfood.R;

public class PurchaseHistoryFragment extends Fragment {
    private int pos;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setPos(int Pos){
        pos = Pos;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.purchase_history_fragment, container, false);
        TextView tvLabel =  view.findViewById(R.id.Window1Txt);
        tvLabel.setText("Purchase History fragment");
        return view;
    }
}