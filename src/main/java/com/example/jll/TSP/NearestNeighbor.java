package com.example.jll.TSP;

import com.example.jll.DijkstrasAlgorithim.DataStructures.Graph;
import com.example.jll.DijkstrasAlgorithim.DataStructures.Node;

import java.util.*;

public class NearestNeighbor {
    private final Graph graph;
    private final Node startNode;

    private double totalDistance;

    public NearestNeighbor(Graph graph, Node startNode) {
        this.graph = graph;
        this.startNode = startNode;
        findTour();
    }

    public NearestNeighbor(Graph graph, String startNode) {
        this.graph = graph;
        this.startNode = graph.getNodes().stream().filter(node -> node.getName()
                        .equals(startNode)).findFirst().orElseThrow();
        findTour();
    }

    /**
     * Executes the Nearest Neighbor heuristic for solving the Traveling Salesman Problem (TSP).
     * - Starts at the given node.
     * - Iteratively visits the nearest unvisited neighbor.
     * - Returns to the start node to complete the cycle.
     * - Prints the final tour and the total distance.
     */
    public void findTour() {
        Set<Node> visited = new HashSet<>();
        List<Node> tour = new ArrayList<>();
        totalDistance = 0.0; // Reset before each run

        Node current = startNode;
        visited.add(current);
        tour.add(current);

        while (tour.size() < graph.getNodes().size() - 1) {
            Map.Entry<Node, Double> nearestEntry = findNearestNeighbor(current, visited);
            if (nearestEntry != null) {
                Node nearest = nearestEntry.getKey();
                double distance = nearestEntry.getValue();

                visited.add(nearest);
                tour.add(nearest);
                totalDistance += distance; // Update total distance
                current = nearest;
            }
        }

        // Return to start node to complete the cycle
        totalDistance += getDistanceTo(current,startNode);
        tour.add(startNode);

        // Output Results
        System.out.println("Executed Nearest Neighbor TSP Heuristic with source node " + startNode.getName());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(String.join(" -> ", tour.stream().map(Node::getName).toArray(String[]::new)));
        System.out.println(totalDistance);
        System.out.println("Total Tour Distance: " + totalDistance);
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Finds the nearest unvisited neighbor of the given node.
     *
     * @param current The current node.
     * @param visited The set of already visited nodes.
     * @return The nearest unvisited node along with its distance as a Map.Entry<Node, Double>,
     *         or null if no unvisited neighbors are found.
     */
    private Map.Entry<Node, Double> findNearestNeighbor(Node current, Set<Node> visited) {
        return current.getAdjacentNodes().entrySet().stream()
                .filter(entry -> !visited.contains(entry.getKey()))
                .min(Map.Entry.comparingByValue()) // Find the neighbor with the smallest distance
                .orElse(null);
    }

    /**
     * Retrieves the distance between two nodes if an edge exists between them.
     *
     * @param current The starting node.
     * @param end     The destination node.
     * @return The distance between the nodes, or Double.MAX_VALUE if no direct edge exists.
     */
    private double getDistanceTo(Node current, Node end) {
        return current.getAdjacentNodes().entrySet().stream()
                .filter(entry -> entry.getKey().equals(end))
                .map(Map.Entry::getValue) // Extract the distance value
                .findFirst() // Get the first (and only) matching entry
                .orElseThrow(); // Default to MAX_VALUE if no path exists
    }


}
