package com.example.fusion1_events;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Displays a map with markers at the locations of the entrants.
 */
public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Find the layout where we'll add markers
        RelativeLayout mapLayout = findViewById(R.id.mapLayout); // The parent layout in activity_maps.xml
        ImageView mapImage = findViewById(R.id.edmontonMap); // Static map image

        ArrayList<Entrant> entrants   = getIntent().getParcelableArrayListExtra("entrants");
        // Mock locations (latitude, longitude)

        // TODO Need to extract all the locations and pass to the map
        // TODO we also need to clear the map
        double[][] entrantsLocations = new double[entrants.size()][2];
        int index =0;
        for(Entrant entrant : entrants)
        {

            double latitude = entrant.getLocation().getLatitude();
            double longitude = entrant.getLocation().getLongitude();
            entrantsLocations[index][0] = latitude;
            entrantsLocations[index][1] = longitude;
            index ++;
        }



        // Add markers after the map image is loaded
        mapImage.post(() -> {
            int imageWidth = mapImage.getWidth();
            int imageHeight = mapImage.getHeight();

            for (double[] location : entrantsLocations) {
                double latitude = location[0];
                double longitude = location[1];

                // Convert latitude and longitude to pixel coordinates
                int[] position = convertLatLngToPixels(latitude, longitude, imageWidth, imageHeight);

                // Create a marker
                ImageView marker = new ImageView(this);
                marker.setImageResource(R.drawable.ic_pin_red); // Use your pin icon
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(48, 48); // Marker size
                params.leftMargin = position[0]; // Set X position
                params.topMargin = position[1];  // Set Y position
                mapLayout.addView(marker, params); // Add marker to the layout
            }
        });
    }

    /**
     * Convert latitude and longitude to x, y pixel coordinates on the map image.
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param imageWidth Width of the map image
     * @param imageHeight Height of the map image
     * @return Pixel coordinates [x, y]w
     */
    private int[] convertLatLngToPixels(double latitude, double longitude, int imageWidth, int imageHeight) {
        double imageTopLat = 53.7000;
        double imageBottomLat = 53.4000;
        double imageLeftLon = -114.1000;
        double imageRightLon = -113.3000;

        int x = (int) ((longitude - imageLeftLon) / (imageRightLon - imageLeftLon) * imageWidth);
        int y = (int) ((imageTopLat - latitude) / (imageTopLat - imageBottomLat) * imageHeight);
        return new int[]{x, y};
    }
}
