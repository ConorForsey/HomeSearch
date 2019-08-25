package com.example.homesearch.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.homesearch.Model.Crime;
import com.example.homesearch.R;

import java.util.ArrayList;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.MyViewHolder> {
    //https://www.androidhive.info/2016/01/android-working-with-recycler-view/
    private ArrayList<Crime> mCrimesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView crimeID, category, outcome;

        public MyViewHolder(View view){
            super(view);
            crimeID = (TextView) view.findViewById(R.id.crimeid);
            category = (TextView) view.findViewById(R.id.category);
            outcome = (TextView) view.findViewById(R.id.outcome);
        }
    }

    public CrimeAdapter(ArrayList<Crime> crimesList){
        this.mCrimesList = crimesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crime_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Crime crime = mCrimesList.get(position);
        holder.crimeID.setText(String.valueOf(crime.getmCrimeID()));
        holder.category.setText(crime.getmCategory());
        if(crime.getmOutcome_status().equals("Not Investigated")){
            holder.outcome.setText(crime.getmOutcome_status());
        }else if(crime.getmOutcome_Category().contains("complete")){
                holder.outcome.setText(crime.getmOutcome_Category().substring(0, crime.getmOutcome_Category().indexOf(';')));
            }else if(crime.getmOutcome_Category().contains("Formal")){
            holder.outcome.setText("Not in the public interest");
        }else holder.outcome.setText(crime.getmOutcome_Category());
    }

    @Override
    public int getItemCount() {
        return mCrimesList.size();
    }
}
