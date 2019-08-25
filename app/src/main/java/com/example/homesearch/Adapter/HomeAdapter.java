package com.example.homesearch.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.homesearch.Model.Home;
import com.example.homesearch.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
    private ArrayList<Home> mHomesList;
    private static ClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, address;

        public MyViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public HomeAdapter(ArrayList<Home> homesList){
        this.mHomesList = homesList;
    }

    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_row, parent, false);

        return new HomeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeAdapter.MyViewHolder holder, int position) {
        Home home = mHomesList.get(position);
        holder.name.setText(home.getmName());
        holder.address.setText(home.getmAddress());
    }

    @Override
    public int getItemCount() {
        return mHomesList.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        HomeAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public int getHomeID(int position){
        return mHomesList.get(position).getId();
    }

    public Home getHome(int position){
        return mHomesList.get(position);
    }
}
