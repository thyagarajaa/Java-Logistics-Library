package org.example.instructor.DijkstrasAlgorithim.DataIngress;

import org.example.instructor.DijkstrasAlgorithim.DataStructures.Dijkstra;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Graph;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Node;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import java.io.File;
import java.net.URI;
import java.util.List;

public class MapboxMatrix {

    private static final String MATRIX_URL = "https://api.mapbox.com/directions-matrix/v1/";
    private final List<Node> nodes;
    private final String apiKey;
    private final MapboxProfile profile;

    public MapboxMatrix(String apiKey, List<Node> nodes, MapboxProfile profile) {
        this.apiKey = apiKey;
        this.nodes = nodes;
        this.profile = profile;
        this.generateGraph();
    }

    public MapboxMatrix(String apiKey, File csvDataSource, MapboxProfile profile) {
        this.apiKey = apiKey;
        this.nodes = CSVIngress.createNodesFromCsv(csvDataSource);
        this.profile = profile;
        this.generateGraph();
    }

    /**
     * Retrieves a duration matrix from the Mapbox Matrix API using the driving profile
     * @throws RuntimeException if there is an issue executing the API Request
     * @throws IllegalArgumentException if the coordinate list is empty, null or exceeds 25 items
     * This is a Mapbox Matrix API limitation.
     */
    private void generateGraph() {
        try {
            if (nodes == null || nodes.isEmpty() || nodes.size() > 25) {
                throw new IllegalArgumentException("Node list is null or empty or > 25 items");
            }

            StringBuilder coordinateStringBuilder = new StringBuilder();

            for (Node i : nodes) {
                if (i.getLongitude() != 0 || i.getLatitude() != 0) {
                    String coordinate = i.getLongitude() + "," + i.getLatitude();
                    coordinateStringBuilder.append(coordinate).append(";");
                } else {
                    throw new IllegalArgumentException("Node does not contain coordinates, Node name: " + i.getName());
                }
            }

            coordinateStringBuilder.setLength(coordinateStringBuilder.length() - 1);

            String coordinateString = coordinateStringBuilder.toString();


            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(MATRIX_URL + profile + coordinateString))
                    .queryParam("access_token", apiKey);

            RestTemplate template = new RestTemplate();
            String response = template.getForObject(uriBuilder.toUriString(), String.class);


            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Extract the "durations" matrix
            JsonNode durationsNode = root.path("durations");
            if (durationsNode.isMissingNode() || !durationsNode.isArray()) {
                throw new RuntimeException("Invalid response: 'durations' field is missing or not an array");
            }

            int size = durationsNode.size();

            for (int i = 0; i < size; i++) {
                JsonNode row = durationsNode.get(i);
                for (int j = 0; j < row.size(); j++) {
                    nodes.get(i).addDestination(nodes.get(j), row.get(j).asInt());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes Dijkstra's algorithm to calculate the shortest paths from a given source node
     * to all other nodes in the graph. The resulting paths represent travel durations in seconds.
     *
     * @param sourceName the name of the source node from which shortest paths will be calculated
     * @throws NullPointerException if the source node does not exist in the graph
     * @throws IllegalStateException if the graph or nodes are not properly initialized
     *
     * <p>Note: The travel durations in seconds are stored as the edge weights in the graph.
     * Ensure that the graph and its nodes are properly initialized with valid weights before invoking this method.</p>
     */
    public void runDijkstrasAlgorithm(String sourceName) {
        Graph graph = new Graph();
        nodes.iterator().forEachRemaining(graph::addNode);
        try {
            Node sourceNode = nodes.stream().filter(node -> node.getName().equals(sourceName)).findFirst().orElse(null);
            assert sourceNode != null;
            Dijkstra.calculateShortestPathFromSource(graph, sourceNode);

        } catch (NullPointerException e) {
            System.out.println("The source node " + sourceName + " does not exist.");
        }
    }

    public enum MapboxProfile {
        DRIVING("mapbox/driving/"),
        WALKING("mapbox/walking/"),
        CYCLING("mapbox/cycling/"),
        DRIVING_TRAFFIC("mapbox/driving-traffic/");

        private final String profile;

        MapboxProfile(String profile) {
            this.profile = profile.toLowerCase();
        }

        @Override
        public String toString() {
            return profile;
        }
    }

}
