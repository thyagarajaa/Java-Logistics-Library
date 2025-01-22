package org.example.instructor.DijkstrasAlgorithim;

import org.example.instructor.DijkstrasAlgorithim.DataIngress.CSVIngress;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Dijkstra;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Graph;
import org.example.instructor.DijkstrasAlgorithim.DataStructures.Node;

import java.io.File;

public class BasicDijkstraTesting {
    public static void main(String[] args) {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addDestination(nodeB, 10);
        nodeA.addDestination(nodeC, 15);

        nodeB.addDestination(nodeD, 12);
        nodeB.addDestination(nodeF, 15);

        nodeC.addDestination(nodeE, 10);

        nodeD.addDestination(nodeE, 2);
        nodeD.addDestination(nodeF, 1);

        nodeF.addDestination(nodeE, 5);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);

        Dijkstra.calculateShortestPathFromSource(graph, nodeA);

        CSVIngress fromFile = new CSVIngress(new File("data/7BrewDijkstra.csv"));
        fromFile.runDijkstrasAlgorithm("HeadQ");

        //TODO From Mapbox source
        // TODO read points from file
        // TODO send list to mapbox
        // TODO process output

    }
}
