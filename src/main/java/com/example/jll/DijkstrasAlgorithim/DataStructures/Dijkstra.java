package com.example.jll.DijkstrasAlgorithim.DataStructures;

import java.util.*;

public class Dijkstra {
    public static Graph calculateShortestPathFromSource(Node source) {
        source.setDistance(0.0);

        Graph output = new Graph();

        Set<Node> settledNodes = new HashSet<>(); // A set is a Java collection that does not have duplicate objects. Can also use a Java List.
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Double> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
            output.addNode(currentNode);
        }
        printOutput(settledNodes);

        return output;
    }

    private static void printOutput(Set<Node> settled){
        List<Node> permanent = new ArrayList<>(settled.stream().sorted(Comparator.comparingDouble(Node::getDistance)).toList());
        System.out.println("Node    |   Distance/Duration   |   Path    |   Dijkstra's Label    ");
        System.out.println("____________________________________________________________________");
        for (Node i : permanent){
            List<String> shortestPath = i.getShortestPath().stream().map(Node::getName).toList();

            String dikstraNotation;
            if (!shortestPath.isEmpty()) {
                dikstraNotation = i.getDistance() + "[" + shortestPath.get(shortestPath.size() - 1) + "]";
            } else {
                dikstraNotation = "0[0]";
            }

            System.out.println(i.getName() +  "     |     " +  i.getDistance() + "     |     " +  shortestPath + "     |     "
                    +  dikstraNotation);
        }
    }

    /**
     * Finds the node with the lowest distance value from a set of unsettled or non-permanent nodes.
     *
     * @param unsettledNodes A set of nodes to evaluate.
     * @return The node with the lowest distance value, or null if the set is empty.
     */
    private static Node getLowestDistanceNode(Set <Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (Node node: unsettledNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    /**
     * Updates the shortest distance and path to a given evaluation node if a shorter path is found.
     *
     * @param evaluationNode The node being evaluated for a shorter path.
     * @param edgeWeight     The weight of the edge between the source node and the evaluation node.
     * @param sourceNode     The source node from which the path originates.
     */
    public static void calculateMinimumDistance(Node evaluationNode, Double edgeWeight, Node sourceNode) {
        Double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
