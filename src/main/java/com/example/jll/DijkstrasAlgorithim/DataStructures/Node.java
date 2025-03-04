package com.example.jll.DijkstrasAlgorithim.DataStructures;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
/**
 * Represents a node in a graph, used for shortest path calculations.
 * Uses Lombok @Getter and @Setter annotations for code simplicity
 */
@Getter
@Setter
public class Node {

    /**
     * The name of the node, used for identification.
     */
    private String name;

    private double latitude;
    private double longitude;

    /**
     * The shortest path from the source node to this node.
     */
    private List<Node> shortestPath = new LinkedList<>();

    /**
     * The current shortest distance from the source node to this node.
     * Initialized to Integer.MAX_VALUE to represent infinity.
     */
    private Double distance = Double.MAX_VALUE;

    /**
     * A map of adjacent nodes used to associate immediate neighbors with edge length.
     * This is a simplified implementation of an adjacency list,
     * which is more suitable for the Dijkstra algorithm than the adjacency matrix.
     */
    Map<Node, Double> adjacentNodes = new HashMap<>();

    /**
     * Adds an adjacent node with a specified distance to this node.
     *
     * @param destination The adjacent node to connect to.
     * @param distance    The distance to the adjacent node.
     */
    public void addDestination(Node destination, double distance) {
        adjacentNodes.put(destination, distance);
    }

    /**
     * Constructs a new node with the specified name.
     *
     * @param name The name of the node.
     */
    public Node(String name) {
        this.name = name;
    }

    public Node(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}