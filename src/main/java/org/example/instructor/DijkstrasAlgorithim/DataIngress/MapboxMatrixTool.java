//package org.example.instructor.DijkstrasAlgorithim.DataIngress;
//
//import com.mapbox.api.matrix.v1.MapboxMatrix;
//import com.mapbox.geojson.Point;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.List;
//
//@Getter
//@Setter
//public class MapboxMatrixTool {
//
//    private static final String MAPBOX_API_URL = "https://api.mapbox.com/directions-matrix/v1/";
//    private final List<Point> coordinates;
//    private final String apiKey;
//
//    public MapboxMatrixAPI(String apiKey) {
//        this.apiKey = apiKey;
//    }
//
//    /**
//     * Retrieves a distance matrix from the Mapbox Matrix API.
//     *
//     * @param coordinates A list of coordinates in "longitude,latitude" format.
//     * @return A 2D array representing the distance matrix in meters.
//     * @throws IOException          If an error occurs during JSON parsing.
//     * @throws InterruptedException If the HTTP request is interrupted.
//     */
//    public double[][] getDistanceMatrix(List<String> coordinates) throws IOException, InterruptedException {
//        // Prepare the URL with coordinates and parameters
//        String coordinatesParam = String.join(";", coordinates);
//        String url = String.format("%s/%s/%s?access_token=%s", MAPBOX_API_URL, "mapbox/driving", coordinatesParam, apiKey);
//
//        // Create the HTTP request
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .GET()
//                .build();
//
//        // Send the HTTP request
//        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new IOException("Failed to fetch data from Mapbox API: " + response.body());
//        }
//
//        // Parse the JSON response
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonResponse = objectMapper.readTree(response.body());
//        JsonNode distancesNode = jsonResponse.get("distances");
//
//        if (distancesNode == null || !distancesNode.isArray()) {
//            throw new IOException("Distances not found in the API response.");
//        }
//
//        // Convert the distances JSON array to a 2D array
//        int numRows = distancesNode.size();
//        int numCols = distancesNode.get(0).size();
//        double[][] distanceMatrix = new double[numRows][numCols];
//
//        for (int i = 0; i < numRows; i++) {
//            for (int j = 0; j < numCols; j++) {
//                distanceMatrix[i][j] = distancesNode.get(i).get(j).asDouble();
//            }
//        }
//
//        return distanceMatrix;
//    }
//}
