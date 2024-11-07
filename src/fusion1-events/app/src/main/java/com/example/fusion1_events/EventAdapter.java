package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * The EventAdapter class is used to bind a list of Event objects to a RecyclerView.
 * It handles the creation of item views and binds data from Event objects to those views.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    // Constructor
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (placeholder for item_event layout)
        // TODO: Replace with actual item layout when ready
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Bind event data to the view
        Event event = eventList.get(position);
        // TODO: Set event details in holder views
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for Event.
     * This class is responsible for holding references to the views for each item in the RecyclerView.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // TODO: Define views here

        /**
         * Constructor to initialize the EventViewHolder with the item view.
         *
         * @param itemView The item view representing a single event.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: Initialize views
        }
    }
}