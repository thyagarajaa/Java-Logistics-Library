package com.example.jll.DijkstrasAlgorithim.Visualization;

import com.example.jll.DijkstrasAlgorithim.DataStructures.Graph;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.jll.DijkstrasAlgorithim.DataStructures.Node;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * The {@code ForceDiagram} class is responsible for visualizing a graph representation
 * of Dijkstra's algorithm results using a force-directed diagram.
 * <p>
 * This class utilizes the {@code GraphStream} library to generate a graphical
 * representation of the shortest path calculations.
 * </p>
 *
 * @author Ani Thyagarajan
 */
public class ForceDiagram {

    /**
     * The title of the force-directed diagram.
     */
    private final String title;

    /**
     * The source node from which the shortest paths are computed.
     */
    private String source;

    /**
     * The data structure representing the graph used for Dijkstra's algorithm computations.
     */
    private final Graph data;

    /**
     * Constructs a new {@code ForceDiagram} that visualizes the shortest path.
     * <p>
     * This constructor initializes the diagram using the provided graph data,
     * title, and source node. The source node is used to highlight the shortest
     * path within the graph.
     * </p>
     *
     * @param data       the graph data to be visualized
     * @param title      the title of the diagram
     * @param sourceNode the identifier of the source node to highlight the shortest path
     */
    public ForceDiagram(Graph data, String title, String sourceNode) {
        this.data = data;
        this.title = title;
        this.source = sourceNode;
        createDiagram(true);
    }

    /**
     * Constructs a new {@code ForceDiagram} that visualizes the entire graph.
     * <p>
     * This constructor initializes the diagram using the provided graph data and title.
     * No specific source node is highlighted, and the entire graph is rendered.
     * </p>
     *
     * @param data  the graph data to be visualized
     * @param title the title of the diagram
     */
    public ForceDiagram(Graph data, String title) {
        this.data = data;
        this.title = title;
        createDiagram(false);
    }


    private void createDiagram(boolean useShortestPath) {
        List<Node> nodes = data.getNodes().stream().toList();

        // JSON creation using Jackson
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graphJson = mapper.createObjectNode();

        // Set graph title dynamically
        graphJson.put("title", title);

        // Arrays for nodes and links
        ArrayNode nodesArray = mapper.createArrayNode();
        ArrayNode linksArray = mapper.createArrayNode();

        // Add nodes to the JSON array
        for (Node node : nodes) {
            ObjectNode nodeJson = mapper.createObjectNode();
            nodeJson.put("id", node.getName());
            nodeJson.put("group", getGroupForNode(node));  // Customize group logic if needed
            nodesArray.add(nodeJson);
        }


        for (Node node : nodes) {
            if (!useShortestPath) {
                node.getAdjacentNodes().forEach((dest, weight) -> {
                    ObjectNode linkJson = mapper.createObjectNode();
                    linkJson.put("source", node.getName());
                    linkJson.put("target", dest.getName());
                    linkJson.put("value", weight);  // Use the appropriate weight value
                    linksArray.add(linkJson);
                });
            } else if (node != nodes.stream().filter(j -> j.getName().equals(source)).findFirst().orElseThrow()) {
                    if (node.getShortestPath().size() > 1){
                        for (int i = 0; i < node.getShortestPath().size(); i++) {
                            try {
                                Double distance = node.getShortestPath().get(i).getAdjacentNodes().get(node.getShortestPath().get(i+1));
                                ObjectNode linkJson = mapper.createObjectNode();
                                linkJson.put("source", node.getShortestPath().get(i).getName());
                                linkJson.put("target", node.getShortestPath().get(i+1).getName());
                                linkJson.put("value", distance);  // Use the appropriate weight value
                                linksArray.add(linkJson);
                            } catch (IndexOutOfBoundsException e) {
                                Double distance = node.getShortestPath().get(i).getAdjacentNodes().get(node);
                                ObjectNode linkJson = mapper.createObjectNode();
                                linkJson.put("source", node.getShortestPath().get(i).getName());
                                linkJson.put("target", node.getName());
                                linkJson.put("value", distance);  // Use the appropriate weight value
                                linksArray.add(linkJson);
                            }
                        }
                    } else {
                        Double distance = node.getShortestPath().get(0).getAdjacentNodes().get(node);
                        ObjectNode linkJson = mapper.createObjectNode();
                        linkJson.put("source", node.getShortestPath().get(0).getName());
                        linkJson.put("target", node.getName());
                        linkJson.put("value", distance);  // Use the appropriate weight value
                        linksArray.add(linkJson);
                    }
                }
        }

            // Add nodes and links to the main JSON object
            graphJson.set("nodes", nodesArray);
            graphJson.set("links", linksArray);

            // Write JSON to file
            try {
                File outDir = new File("out").getAbsoluteFile();
                // Generate a unique filename using UUID
                String uniqueFileName = "plotData_" + UUID.randomUUID() + ".json";

                // Ensure the JSON file is written inside the 'out' directory
                File jsonFile = new File(outDir, uniqueFileName);
                mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, graphJson);

                generateHtmlFile(uniqueFileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
    }

    private File generateHtmlFile(String jsonFilePath) throws IOException, URISyntaxException {
        // Get absolute path to the 'out' directory
        File outDir = new File("out").getAbsoluteFile();

        // Ensure the directory exists
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        String fileName = "diagram_" + jsonFilePath.substring(jsonFilePath.indexOf("_"), jsonFilePath.lastIndexOf(".") + 1) + ".html";

        // Define the HTML file path
        File htmlFile = new File(outDir, fileName);

        // HTML content with embedded script to fetch and display the JSON data
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Graph Visualization</title>\n" +
                "    <style>\n" +
                "        .links line {\n" +
                "            stroke: #999;\n" +
                "            stroke-opacity: 0.6;\n" +
                "        }\n" +
                "        .nodes circle {\n" +
                "            stroke: #fff;\n" +
                "            stroke-width: 1.5px;\n" +
                "        }\n" +
                "        .edge-label {\n" +
                "            font-family: sans-serif;\n" +
                "            font-size: 10px;\n" +
                "            fill: #333;\n" +
                "        }\n" +
                "        .node-label {\n" +
                "            font-family: sans-serif;\n" +
                "            font-size: 12px;\n" +
                "            fill: #000;\n" +
                "        }\n" +
                "        text {\n" +
                "            pointer-events: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1 id=\"graph-title\">Loading...</h1>\n" +
                "    <p id=\"graph-description\"></p>\n" +
                "    <svg width=\"1600\" height=\"900\"></svg>\n" +
                "    <script src=\"https://d3js.org/d3.v6.min.js\"></script>\n" +
                "    <script>\n" +
                "        window.onload = function() {\n" +
                "            // Delay execution to ensure everything is ready\n" +
                "            setTimeout(() => {\n" +
                "                d3.json('${FILE.PATH}')\n" +
                "                    .then(function(graph) {\n" +
                "                        console.log(\"JSON successfully loaded:\", graph);\n" +
                "                        if (!graph || Object.keys(graph).length === 0) {\n" +
                "                            document.getElementById('graph-title').textContent = \"Error: Could not load graph data.\";\n" +
                "                            return;\n" +
                "                        }\n" +
                "                        if (graph.title) {\n" +
                "                            document.getElementById('graph-title').textContent = graph.title;\n" +
                "                        }\n" +
                "                        if (graph.description) {\n" +
                "                            document.getElementById('graph-description').textContent = graph.description;\n" +
                "                        }\n" +
                "                        renderGraph(graph);\n" +
                "                    })\n" +
                "                    .catch(function(error) {\n" +
                "                        console.error(\"Error loading JSON:\", error);\n" +
                "                        document.getElementById('graph-title').textContent = \"Error: Unable to load graph.\";\n" +
                "                    });\n" +
                "            }, 500); // 500ms delay\n" +
                "        };\n" +
                "\n" +
                "        function renderGraph(graph) {\n" +
                "            const svg = d3.select(\"svg\"),\n" +
                "                width = +svg.attr(\"width\"),\n" +
                "                height = +svg.attr(\"height\");\n" +
                "\n" +
                "            const simulation = d3.forceSimulation(graph.nodes)\n" +
                "                .force(\"link\", d3.forceLink(graph.links).id(d => d.id).distance(100))\n" +
                "                .force(\"charge\", d3.forceManyBody().strength(-300))\n" +
                "                .force(\"center\", d3.forceCenter(width / 2, height / 2));\n" +
                "\n" +
                "            const link = svg.append(\"g\")\n" +
                "                .attr(\"class\", \"links\")\n" +
                "                .selectAll(\"line\")\n" +
                "                .data(graph.links)\n" +
                "                .enter().append(\"line\")\n" +
                "                .attr(\"stroke-width\", d => Math.sqrt(d.value));\n" +
                "\n" +
                "            const edgeLabels = svg.append(\"g\")\n" +
                "                .attr(\"class\", \"edge-labels\")\n" +
                "                .selectAll(\"text\")\n" +
                "                .data(graph.links)\n" +
                "                .enter().append(\"text\")\n" +
                "                .attr(\"class\", \"edge-label\")\n" +
                "                .text(d => d.value);\n" +
                "\n" +
                "            const node = svg.append(\"g\")\n" +
                "                .attr(\"class\", \"nodes\")\n" +
                "                .selectAll(\"circle\")\n" +
                "                .data(graph.nodes)\n" +
                "                .enter().append(\"circle\")\n" +
                "                .attr(\"r\", 10)\n" +
                "                .attr(\"fill\", d => d3.schemeCategory10[d.group % 10])\n" +
                "                .call(d3.drag()\n" +
                "                    .on(\"start\", dragstarted)\n" +
                "                    .on(\"drag\", dragged)\n" +
                "                    .on(\"end\", dragended));\n" +
                "\n" +
                "            const nodeLabels = svg.append(\"g\")\n" +
                "                .attr(\"class\", \"node-labels\")\n" +
                "                .selectAll(\"text\")\n" +
                "                .data(graph.nodes)\n" +
                "                .enter().append(\"text\")\n" +
                "                .attr(\"class\", \"node-label\")\n" +
                "                .attr('x', 12)\n" +
                "                .attr('y', 3)\n" +
                "                .text(d => d.id);\n" +
                "\n" +
                "            simulation.on(\"tick\", () => {\n" +
                "                link\n" +
                "                    .attr(\"x1\", d => d.source.x)\n" +
                "                    .attr(\"y1\", d => d.source.y)\n" +
                "                    .attr(\"x2\", d => d.target.x)\n" +
                "                    .attr(\"y2\", d => d.target.y);\n" +
                "\n" +
                "                edgeLabels\n" +
                "                    .attr(\"x\", d => (d.source.x + d.target.x) / 2)\n" +
                "                    .attr(\"y\", d => (d.source.y + d.target.y) / 2);\n" +
                "\n" +
                "                node\n" +
                "                    .attr(\"cx\", d => d.x)\n" +
                "                    .attr(\"cy\", d => d.y);\n" +
                "\n" +
                "                nodeLabels\n" +
                "                    .attr(\"x\", d => d.x)\n" +
                "                    .attr(\"y\", d => d.y);\n" +
                "            });\n" +
                "\n" +
                "            function dragstarted(event, d) {\n" +
                "                if (!event.active) simulation.alphaTarget(0.3).restart();\n" +
                "                d.fx = d.x;\n" +
                "                d.fy = d.y;\n" +
                "            }\n" +
                "            function dragged(event, d) {\n" +
                "                d.fx = event.x;\n" +
                "                d.fy = event.y;\n" +
                "            }\n" +
                "            function dragended(event, d) {\n" +
                "                if (!event.active) simulation.alphaTarget(0);\n" +
                "                d.fx = null;\n" +
                "                d.fy = null;\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        String output = html.replace("${FILE.PATH}", jsonFilePath);

        // Write the HTML file
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(output);
        }

        if (Desktop.isDesktopSupported()) {
            try {
                File parentFile = outDir.getParentFile();
                //http://localhost:63342/INEG35303-Instructor-Repo/out/diagram_3116c563-ffe7-4f56-a13e-15c8b22ef798.html?_ijt=r9cak14134uh2p08470utinm90&_ij_reload=RELOAD_ON_SAVE
                String urlString = "http://localhost:63342/" + parentFile.getName() + "/out/" + fileName;
                URI uri = new URI(urlString);
                Desktop.getDesktop().browse(uri);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return htmlFile;
    }


    private int getGroupForNode(Node node) {
        // Example logic to determine the group for a node (customize as needed)
        if (node.getName().equals(source)) {
            return 1;
        } else {
            return 2;
        }
    }
}
