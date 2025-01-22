package org.example.instructor.DijkstrasAlgorithim.DataStructures;

import java.util.*;

public class Dijkstra {
    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>(); // A set is a Java collection that does not have duplicate objects. Can also use a Java List.
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        printOutput(settledNodes);

        return graph;
    }

    private static void printOutput(Set<Node> settled){
        List<Node> permanent = new ArrayList<>(settled.stream().sorted(Comparator.comparingInt(Node::getDistance)).toList());
        System.out.println("Node | Distance | Path");
        System.out.println("______________________");
        for (Node i : permanent){
            List<String> shortestPath = i.getShortestPath().stream().map(Node::getName).toList();

            System.out.println(i.getName() +  " | " +  i.getDistance() + " | " +  shortestPath);
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
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
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
    public static void calculateMinimumDistance(Node evaluationNode, Integer edgeWeight, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
