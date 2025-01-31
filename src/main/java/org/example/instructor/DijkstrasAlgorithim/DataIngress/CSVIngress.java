package org.example.instructor.DijkstrasAlgorithim.DataIngress;

import lombok.Getter;
import lombok.Setter;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Dijkstra;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Graph;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CSVIngress {
    private final File path;

    private List<Node> nodes;

    private List<List<Integer>> distanceMatrix;

    private Graph output;

    public CSVIngress(File path) {
        this.path = path;
        createNodes();
    }

    /**
     * Reads a distance matrix and node names from a CSV file and parses nodes
     * and creates a distance matrix.
     *
     * @throws RuntimeException If there's an error reading the file.
     */
    private void createNodes() {
        this.nodes = new ArrayList<>();
        this.distanceMatrix = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (isFirstLine) {
                    // First row contains the node names
                    for (int i = 1; i < values.length; i++) {
                        this.nodes.add(new Node(values[i]));
                    }
                    isFirstLine = false;
                } else {
                    // Subsequent rows contain the distance values
                    List<Integer> row = new ArrayList<>();
                    for (int i = 1; i < values.length; i++) {
                        row.add(Integer.parseInt(values[i].trim()));

                    }
                    distanceMatrix.add(row);
                }
            }

            for (int i = 0; i < distanceMatrix.size(); i++) {
                Node currentNode = nodes.get(i);

                for (int j = 0; j < distanceMatrix.get(i).size(); j++) {
                    if (i != j && distanceMatrix.get(i).get(j) > 0) { // Avoid self-loops and zero distances
                        Node adjacentNode = nodes.get(j);
                        currentNode.addDestination(adjacentNode, distanceMatrix.get(i).get(j));
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Reads a CSV file and creates a list of {@code Node} objects. The CSV file is expected to have
     * the following format:
     *
     * <pre>
     * name,longitude,latitude
     * NodeA,-122.4184,37.7517
     * NodeB,-122.4229,37.7552
     * NodeC,-122.4269,37.7597
     * </pre>
     *
     * Each row represents a node with its name, longitude, and latitude. The first row is treated as
     * a header and is skipped during processing. If any field in a row is blank, or if the file
     * cannot be read, an appropriate exception will be thrown.
     *
     * @param file the CSV file to read
     * @return a list of {@code Node} objects created from the data in the CSV file
     * @throws IllegalArgumentException if a row contains blank fields or if the longitude/latitude values
     *                                  cannot be parsed as numbers
     * @throws RuntimeException if an error occurs while reading the file
     */
    public static List<Node> createNodesFromCsv(File file) {
        List<Node> nodes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split line into values
                String[] values = line.split(",");

                // Ensure the CSV format is correct
                if (values.length < 3 || values[0].trim().isEmpty() || values[1].trim().isEmpty() || values[2].trim().isEmpty()) {
                    throw new IllegalArgumentException("CSV row contains blank fields: " + line);
                }

                // Parse node name, longitude, and latitude
                String nodeName = values[0].trim();
                double longitude = Double.parseDouble(values[1].trim());
                double latitude = Double.parseDouble(values[2].trim());

                // Create and add the Node
                nodes.add(new Node(nodeName, longitude, latitude));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + file.getPath(), e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV file", e);
        }

        return nodes;
    }

    /**
     * Reads a distance matrix and node names from a CSV file runs Djikstra's
     * Algorithim to find the shortest path.
     *
     * @param  source String with the name of the source node
     * @throws NullPointerException If the specified source node does not exist.
     */
    public void runDijkstrasAlgorithm(String source) {
        Graph graph = new Graph();

        for (Node node : this.nodes) {
            graph.addNode(node);
        }

        try {
            Node sourceNode = nodes.stream().filter(node -> node.getName().equals(source)).findFirst().orElse(null);
            assert sourceNode != null;
            this.output = Dijkstra.calculateShortestPathFromSource(graph, sourceNode);

        } catch (NullPointerException e) {
            System.out.println("The source node " + source + " does not exist.");
        }
    }
}
