package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * EntrantAdapter is a RecyclerView adapter used for displaying a list of Entrant objects.
 * It manages the creation and binding of view holders that display individual entrant data.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    // List of entrants to be displayed in the RecyclerView
    private List<Entrant> entrantList;

    /**
     * Constructor for EntrantAdapter that takes a list of entrants.
     *
     * @param entrantList List of entrants to be displayed.
     */
    public EntrantAdapter(List<Entrant> entrantList) {
        this.entrantList = entrantList;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     * Inflates the item layout and creates a new view holder.
     *
     * @param parent   The parent view that the new view will be attached to.
     * @param viewType The view type of the new view.
     * @return A new instance of EntrantViewHolder.
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (placeholder for item_event layout)
        // TODO: Replace with actual item layout when ready
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The view holder to bind the data to.
     * @param position The position of the data in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        // Bind entrant data to the view
        Entrant entrant = entrantList.get(position);
        // TODO: Set entrant details in holder views
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The number of items in entrantList.
     */
    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    /**
     * ViewHolder class for Entrant that holds the views for each item in the RecyclerView.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        // TODO: Define views here

        /**
         * Constructor for EntrantViewHolder that initializes the views.
         *
         * @param itemView The view representing an individual list item.
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: Initialize views
        }
    }
}
/**
 * Summary:
 * - EntrantAdapter is a RecyclerView adapter for displaying a list of entrants.
 * - It inflates item layouts, creates view holders, and binds data to the views.
 * - EntrantViewHolder is a nested class that holds references to the views for each entrant item.
 * - TODOs are placeholders to be replaced with the actual item layout and view initialization.
 */
