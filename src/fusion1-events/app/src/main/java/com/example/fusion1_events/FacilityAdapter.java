package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private List<Facility> facilities; // List to hold facilities
    private OnFacilityClickListener listener; // Listener for click events

    // Constructor to initialize the facility list and listener
    public FacilityAdapter(List<Facility> facilities, OnFacilityClickListener listener) {
        this.facilities = facilities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view); // Return a new ViewHolder instance
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        // Get the facility at the current position
        Facility facility = facilities.get(position);

        // Bind the facility data to the views in the ViewHolder
        holder.tvFacilityName.setText(facility.getName());
        holder.tvFacilityLocation.setText(facility.getLocation());

        // Set click listeners for the edit and delete buttons
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(facility));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(facility));
    }

    @Override
    public int getItemCount() {
        return facilities.size(); // Return the total number of facilities
    }

    // ViewHolder class to hold the views for each item
    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName;
        TextView tvFacilityLocation;
        Button btnEdit; // Button for editing
        Button btnDelete; // Button for deleting

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextViews and Buttons
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvFacilityLocation = itemView.findViewById(R.id.tvFacilityLocation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Interface for handling edit and delete clicks
    public interface OnFacilityClickListener {
        void onEditClick(Facility facility);
        void onDeleteClick(Facility facility);
    }
}
