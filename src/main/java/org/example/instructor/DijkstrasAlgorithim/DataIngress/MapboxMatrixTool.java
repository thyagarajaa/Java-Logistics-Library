package org.example.instructor.DijkstrasAlgorithim.DataIngress;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.matrix.v1.MapboxMatrix;

public class MapboxMatrixTool {
    public static void main(String[] args) {

        MapboxMatrix matrixApiClient = MapboxMatrix.builder()
                .accessToken(MAPBOX_ACCESS_TOKEN)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .coordinates(listOfCoordinates)
                .build();
    }
}
