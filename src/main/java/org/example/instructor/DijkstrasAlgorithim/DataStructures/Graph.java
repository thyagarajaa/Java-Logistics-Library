package org.example.instructor.DijkstrasAlgorithim.DataStructures;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Represents a Graph which is a collection of nodes. Uses Lombok annotations for code cleanliness
 */
@Setter @Getter
public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

}