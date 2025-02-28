package com.example.jll.DijkstrasAlgorithim.DataIngress;

import com.example.jll.DijkstrasAlgorithim.DataStructures.Dijkstra;
import com.example.jll.DijkstrasAlgorithim.DataStructures.Graph;
import com.example.jll.DijkstrasAlgorithim.DataStructures.Node;
import com.example.jll.DijkstrasAlgorithim.Visualization.ForceDiagram;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * The {@code MapboxMatrix} class is responsible for interfacing with the Mapbox Directions Matrix API
 * to retrieve a distance or duration matrix for a given set of nodes.
 * <p>
 * This class constructs a matrix by making API requests based on the provided nodes,
 * transport profile, and unit preferences.
 * </p>
 *
 * @author Ani Thyagarajan
 */
public class MapboxMatrix {

    /**
     * Base URL for the Mapbox Directions Matrix API.
     */
    private static final String MATRIX_URL = "https://api.mapbox.com/directions-matrix/v1/";

    /**
     * List of nodes representing locations for which the distance matrix is generated.
     */
    private final List<Node> nodes;

    /**
     * API key for authenticating requests to the Mapbox API.
     */
    private final String apiKey;

    /**
     * The transport profile used for the distance matrix (e.g., driving, walking, cycling).
     */
    private final MapboxProfile profile;

    /**
     * The unit system used for distance calculations (e.g., metric, imperial).
     */
    private final MapboxUnits units;

    /**
     * Constructs a {@code MapboxMatrix} instance using a predefined list of nodes.
     * This constructor initializes the graph by calling {@code generateGraph()}.
     *
     * @param apiKey  The Mapbox API key for authentication.
     * @param nodes   A list of nodes representing locations.
     * @param profile The Mapbox profile defining the mode of transportation.
     * @param units   The unit system for distances (metric or imperial).
     */
    public MapboxMatrix(String apiKey, List<Node> nodes, MapboxProfile profile, MapboxUnits units) {
        this.apiKey = apiKey;
        this.nodes = nodes;
        this.profile = profile;
        this.units = units;
        this.generateGraph();
    }

    /**
     * Constructs a {@code MapboxMatrix} instance by reading node data from a CSV file.
     * This constructor initializes the graph by calling {@code generateGraph()}.
     *
     * @param apiKey        The Mapbox API key for authentication.
     * @param csvDataSource The CSV file containing node data.
     * @param profile       The Mapbox profile defining the mode of transportation.
     * @param units         The unit system for distances (metric or imperial).
     */
    public MapboxMatrix(String apiKey, File csvDataSource, MapboxProfile profile, MapboxUnits units) {
        this.apiKey = apiKey;
        this.nodes = CSVIngress.createNodesFromCsv(csvDataSource);
        this.profile = profile;
        this.units = units;
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
                    .queryParam("access_token", apiKey)
                    .queryParam("annotations", units);


            RestTemplate template = new RestTemplate();
            String response = template.getForObject(uriBuilder.toUriString(), String.class);

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Extract the "durations" matrix
            JsonNode durationsNode = this.units == MapboxUnits.DURATION ? root.get("durations") : root.get("distances");
            if (durationsNode.isMissingNode() || !durationsNode.isArray()) {
                throw new RuntimeException("Invalid response: 'durations or distances' field is missing or not an array");
            }

            int size = durationsNode.size();

            System.out.println("Start Matrix API Distance Matrix");
            System.out.println("___________________________");



            for (int i = 0; i < size; i++) {
                JsonNode row = durationsNode.get(i);
                System.out.println(row);
                for (int j = 0; j < row.size(); j++) {
                    nodes.get(i).addDestination(nodes.get(j), row.get(j).asInt());
                }
            }

            System.out.println("___________________________");
            System.out.println("End Matrix API Distance Matrix");

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
    public Graph runDijkstrasAlgorithm(String sourceName) {
        Graph graph = new Graph();
        nodes.iterator().forEachRemaining(graph::addNode);
        try {
            Node sourceNode = nodes.stream().filter(node -> node.getName().equals(sourceName)).findFirst().orElse(null);
            assert sourceNode != null;
            return Dijkstra.calculateShortestPathFromSource(sourceNode);

        } catch (NullPointerException e) {
            throw new NullPointerException("The source node " + sourceName + " does not exist.");
        }
    }

    public void visualizeGraph(String title){
        Graph graph = new Graph(nodes);
        ForceDiagram visual = new ForceDiagram(graph, title);
    }

    public void visualizeShortestPath(String title, String soruceNode){
        Graph graph = new Graph(nodes);
        ForceDiagram visual = new ForceDiagram(graph, title, soruceNode);
    }

    public void showShortestPathMap(String title, String soruceNode, String token) {
        try {
            String id = UUID.randomUUID().toString();

            File jsonFile = generateDijkstraJsonFile(id, nodes, soruceNode);
            File outDir = new File("out").getAbsoluteFile();

            File htmlFile = generateMapboxHtmlFile(id, jsonFile.getName(), token);

            if (Desktop.isDesktopSupported()) {
                try {
                    File parentFile = outDir.getParentFile();
                    //http://localhost:63342/INEG35303-Instructor-Repo/out/diagram_3116c563-ffe7-4f56-a13e-15c8b22ef798.html?_ijt=r9cak14134uh2p08470utinm90&_ij_reload=RELOAD_ON_SAVE
                    String urlString = "http://localhost:63342/" + parentFile.getName() + "/out/" + htmlFile.getName();
                    URI uri = new URI(urlString);
                    Desktop.getDesktop().browse(uri);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

// Open the HTML file in the user's browser
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private File generateDijkstraJsonFile(String uuid, List<Node> nodes, String sourceNodeName) throws IOException {
        // Ensure the output directory exists
        File outDir = new File("out").getAbsoluteFile();

        // Ensure the directory exists
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        // Generate a unique filename
        String jsonFileName = "mapData_" + uuid + ".json";
        File jsonFile = new File(outDir, jsonFileName);

        // JSON creation using Jackson
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graphJson = mapper.createObjectNode();

        // Arrays for nodes and edges
        ArrayNode nodesArray = mapper.createArrayNode();
        ArrayNode linksArray = mapper.createArrayNode();

        // Add nodes to the JSON array
        for (Node node : nodes) {
            ObjectNode nodeJson = mapper.createObjectNode();
            nodeJson.put("id", node.getName());
            nodeJson.put("latitude", node.getLatitude());
            nodeJson.put("longitude", node.getLongitude());
            nodesArray.add(nodeJson);
        }

        // Add edges based on the shortest paths
        for (Node node : nodes) {
            if (!node.getName().equals(sourceNodeName)) { // Skip the source node
                if (node.getShortestPath().size() > 1) {
                    for (int i = 0; i < node.getShortestPath().size() - 1; i++) {
                        Node fromNode = node.getShortestPath().get(i);
                        Node toNode = node.getShortestPath().get(i + 1);
                        Double distance = fromNode.getAdjacentNodes().get(toNode);

                        ObjectNode linkJson = mapper.createObjectNode();
                        linkJson.put("source", fromNode.getName());
                        linkJson.put("target", toNode.getName());
                        linkJson.put("value", distance);
                        linksArray.add(linkJson);
                    }
                } else if (!node.getShortestPath().isEmpty()) {
                    Node fromNode = node.getShortestPath().get(0);
                    Double distance = fromNode.getAdjacentNodes().get(node);

                    ObjectNode linkJson = mapper.createObjectNode();
                    linkJson.put("source", fromNode.getName());
                    linkJson.put("target", node.getName());
                    linkJson.put("value", distance);
                    linksArray.add(linkJson);
                }
            }
        }

        // Add nodes and links to the main JSON object
        graphJson.set("nodes", nodesArray);
        graphJson.set("links", linksArray);

        // Write JSON to file
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, graphJson);

        System.out.println("Dijkstra JSON file created at: " + jsonFile.getAbsolutePath());
        return jsonFile;
    }

    private File generateMapboxHtmlFile(String uuid, String jsonFileName, String mapboxAccessToken) throws IOException {
        // Ensure the output directory exists
        File outDir = new File("out").getAbsoluteFile();

        // Ensure the directory exists
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        // Define the HTML file path
        File htmlFile = new File(outDir, "map_"+ uuid +".html");

        // Generate HTML content
        String htmlContent = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Map Visualization - Dijkstra's Algorithm</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script src="https://api.mapbox.com/mapbox-gl-js/v2.14.0/mapbox-gl.js"></script>
            <link href="https://api.mapbox.com/mapbox-gl-js/v2.14.0/mapbox-gl.css" rel="stylesheet" />
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100vw; height: 100vh; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                mapboxgl.accessToken = '${MAPBOX.TOKEN}';

                // Initialize map
                var map = new mapboxgl.Map({
                    container: 'map',
                    style: 'mapbox://styles/mapbox/streets-v11',
                    center: [0, 0],  // Default center, will update dynamically
                    zoom: 2
                });

                // Load JSON data
                fetch('${JSON.PATH}')
                    .then(response => response.json())
                    .then(data => {
                        var bounds = new mapboxgl.LngLatBounds();

                        // Add nodes (points)
                        data.nodes.forEach(node => {
                            var marker = new mapboxgl.Marker()
                                .setLngLat([node.x, node.y])
                                .setPopup(new mapboxgl.Popup().setHTML("<b>" + node.id + "</b>"))
                                .addTo(map);
                            bounds.extend([node.x, node.y]);
                        });

                        // Add edges (lines)
                        data.links.forEach(link => {
                            var sourceNode = data.nodes.find(n => n.id === link.source);
                            var targetNode = data.nodes.find(n => n.id === link.target);

                            if (sourceNode && targetNode) {
                                var coordinates = [
                                    [sourceNode.x, sourceNode.y],
                                    [targetNode.x, targetNode.y]
                                ];

                                map.addLayer({
                                    id: 'line-' + link.source + '-' + link.target,
                                    type: 'line',
                                    source: {
                                        type: 'geojson',
                                        data: {
                                            type: 'Feature',
                                            geometry: {
                                                type: 'LineString',
                                                coordinates: coordinates
                                            },
                                            properties: {
                                                weight: link.value
                                            }
                                        }
                                    },
                                    layout: {},
                                    paint: {
                                        'line-color': '#ff0000',
                                        'line-width': 3
                                    }
                                });

                                // Add edge weight as a label
                                var midLng = (sourceNode.x + targetNode.x) / 2;
                                var midLat = (sourceNode.y + targetNode.y) / 2;

                                new mapboxgl.Marker({ color: 'black' })
                                    .setLngLat([midLng, midLat])
                                    .setPopup(new mapboxgl.Popup().setHTML("<b>Weight: " + link.value + "</b>"))
                                    .addTo(map);
                            }
                        });

                        // Fit map to the bounds of all nodes
                        map.fitBounds(bounds, { padding: 50 });
                    })
                    .catch(error => console.error("Error loading JSON:", error));
            </script>
        </body>
        </html>
    """;

        String mapbox = htmlContent.replace("${MAPBOX.TOKEN}", mapboxAccessToken);
        String json = htmlContent.replace("${JSON.PATH}", jsonFileName);

        // Write the HTML file
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(json);
        }

        return htmlFile;
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

    public enum MapboxUnits {
        DISTANCE("distance"),
        DURATION("duration");

        private final String profile;

        MapboxUnits(String profile) {
            this.profile = profile.toLowerCase();
        }

        @Override
        public String toString() {
            return profile;
        }
    }

}
