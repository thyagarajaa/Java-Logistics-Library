package com.example.jll.DijkstrasAlgorithim.DataStructures;

//import com.example.jll.DijkstrasAlgorithim.visualization.ForceDiagram;
import com.example.jll.DijkstrasAlgorithim.Visualization.ForceDiagram;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Represents a Graph which is a collection of nodes. Uses Lombok annotations for code cleanliness
 */
@Setter @Getter
public class Graph {

    private Set<Node> nodes;

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Graph(Collection<Node> nodes) {
        this.nodes = new HashSet<>(nodes);
    }

    public Graph(Node... nodes) {
        this.nodes = new HashSet<>(Arrays.asList(nodes));
    }



    /**
     * Visualizes a diagram using the provided title and result flag.
     * <p>
     * This method creates a {@link ForceDiagram} with the specified title and result.
     * Note that although a source node identifier is passed in, it is not used
     * to highlight any node in the diagram.
     * </p>
     *
     * @param title      the title of the diagram
     */
    public void visualize(String title){
        ForceDiagram diagram = new ForceDiagram(this, title);
    }

    /**
     * Visualizes a diagram output by creating a {@link ForceDiagram} with the specified title
     * and highlights the given source node.
     * <p>
     * This method creates a {@code ForceDiagram} using the title and the source node name.
     * The source node passed in is highlighted within the diagram.
     * </p>
     *
     * @param title          the title of the diagram
     * @param sourceNodeName the name of the source node to be highlighted in the diagram
     */
    public void visualizeOutput(String title, String sourceNodeName){
        ForceDiagram diagram = new ForceDiagram(this, title, sourceNodeName);
    }
}