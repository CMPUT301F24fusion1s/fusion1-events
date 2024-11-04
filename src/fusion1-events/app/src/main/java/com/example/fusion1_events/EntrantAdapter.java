package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Base code for EntrantAdapter
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {
    private List<Entrant> entrantList;

    // Constructor
    public EntrantAdapter(List<Entrant> entrantList) {
        this.entrantList = entrantList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (placeholder for item_event layout)
        // TODO: Replace with actual item layout when ready
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        // Bind entrant data to the view
        Entrant entrant = entrantList.get(position);
        // TODO: Set entrant details in holder views
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    // ViewHolder class for Entrant
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        // TODO: Define views here

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: Initialize views
        }
    }
}
