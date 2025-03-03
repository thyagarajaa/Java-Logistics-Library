# Java Logistics Library (JLL)

## Overview
The **Java Logistics Library (JLL)** is a comprehensive utility library designed to solve common logistics and routing problems in Java. It provides implementations for solving shortest path problems, the Travelling Salesperson Problem (TSP), and other essential logistics challenges.

## Features
- **Shortest Path Calculation**
  - Implements **Dijkstra's Algorithm** for finding the shortest paths in a weighted graph.
  - Can import distance matrices from CSV Files
  - Provides efficient methods for pathfinding in logistics networks.

- **Real-World Distance Calculations**
  - Integrates with the **Mapbox Matrix API** to compute real-world travel distances and durations.
  - Supports distance calculations using road network data.

- **Travelling Salesperson Problem (TSP) Solvers**
  - Implements **Nearest Neighbor Heuristic** for TSP.
  - Provides a fast approximation for route optimization.

- **Vehicle Routing Problem (VRP) Utilities** *(Upcoming Feature)*
  - Planned tools for optimizing multiple vehicle routes.
  - Future support for custom constraints and objectives.

## Installation
JLL is not distributed through Maven Central. To use it in your project, you can download the latest release and use it as a JAR file dependency in your **Maven** or **Gradle** build system:

**Maven**

```xml
<dependencies>
    <!-- Local JAR dependency -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>JLL</artifactId>
        <version>1.0.1</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/libs/JLL-1.0.1.jar</systemPath>
    </dependency>

    <!-- Jackson Databind -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.18.2</version>
    </dependency>

    <!-- Apache Commons Math -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>

    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
        <version>6.2.2</version>
    </dependency>
</dependencies>
Note:
```
The local JAR (JLL-1.0.1.jar) should be placed inside the libs directory at the project root. For production environments, consider installing the JAR into your local Maven repository or hosting it in a private repository instead of using the system scope.

### Gradle
```gradle
dependencies {
    implementation files('./libs/JLL-1.0.1.jar')

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.18.2'

    implementation 'org.apache.commons:commons-math3:3.6.1'

    // https://mvnrepository.com/artifact/org.springframework/spring-web
    implementation 'org.springframework:spring-web:6.2.2'
}
```

## Usage
### Dijkstra's Algorithm Example
```java
Node nodeA = new Node("A");
Node nodeB = new Node("B");
Node nodeC = new Node("C");
Node nodeD = new Node("D");

nodeA.addDestination(nodeB, 10);
nodeA.addDestination(nodeC, 15);
nodeB.addDestination(nodeD, 12);
nodeB.addDestination(nodeA, 12);
nodeC.addDestination(nodeA, 15);
nodeD.addDestination(nodeA, 5);

Graph graph = new Graph();

graph.addNode(nodeA);
graph.addNode(nodeB);
graph.addNode(nodeC);
graph.addNode(nodeD);

Dijkstra.calculateShortestPathFromSource(graph, nodeA);
```
The above code outputs:
``` output
Node | Distance/Duration | Path
______________________
A | 0.0 | []
B | 10.0 | [A]
C | 15.0 | [A]
D | 22.0 | [A, B]
```
### Using Dijkstra's Algorithm from CSV Distance Matrix
```java
CSVIngress csvIngress = new CSVIngress(new File("data/sampleData.csv"));
fromFile.runDijkstrasAlgorithm("HeadQ");
```

### Using Mapbox Matrix API for Distance Calculation
```java
MapboxMatrix mapboxMatrix = new MapboxMatrix("YOUR_MAPBOX_KEY, new File("data/nodes.csv"), MapboxMatrix.MapboxProfile.DRIVING, MapboxMatrix.MapboxUnits.DURATION);
mapboxMatrix.runDijkstrasAlgorithm("Node1");
```

## Roadmap
- **Additional TSP Heuristics**
- **Map Visual** use Mapbox GL JS to visualize TSP and Shortest Path problems that reference real world locations. 
- **Advanced TSP Solvers** including Linear Programming.
- **Vehicle Routing Problem (VRP) Utilities** *(Planned for Future Release)*.

## License
JLL is released under the **MIT License**. Feel free to use and modify it for your projects.

## Contributions
Contributions are welcome! Please open an issue or submit a pull request on GitHub.

**Developed by Ani Thyagarajan, Undergraduate Student at University of Arkansas College of Engineering Dept. of Industrial Engineering and Operations Analytics**
