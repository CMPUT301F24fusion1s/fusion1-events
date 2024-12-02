package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitListViewHolder> {
    private List<String> entrants;

    public WaitlistAdapter(List<String> entrants) {
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public WaitListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new WaitListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitListViewHolder holder, int position) {
        String name = entrants.get(position);
        holder.nameTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    static class WaitListViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        WaitListViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
        }
    }

    public void setEntrants(List<String> entrants) {
        this.entrants = entrants;
        notifyDataSetChanged();
    }
}
