/* *****************************************************************************
 *  Name:    Thoshitha Kaduruwewa (Rathnayaka)
 *  UOW_ID:  w1714902
 *  IIT_ID:  2018142
 *
 *  Description: Contains the FordFulkerson Logic and Network-flow graph creation logic
 *
 *  Written:       22/03/2021
 *  Last updated:  8/04/2021
 *
 *  % javac NetworkFlowCLI.java
 *  % java NetworkFlowCLI
 *
 **************************************************************************** */
package w1714902.fordfulkerson;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class FordFulkerson {
    ArrayList<Integer> vertexList = new ArrayList(); // vertex list to keep track of vertexes in each augmenting path
    ArrayList<String> augmentedPaths = new ArrayList<>();

    // used to specify the length for creating graph data structure
    final int noOfVertices; // will contain the number of vertices of the flow graph
    // used to specify the starting node for the Depth-first-search algorithm
    final int startVertex; // will contain the start vertex position
    final int terminalVertex; // will contain the end vertex position

    // to keep track of visited and unvisited nodes when traversing the graph
    // we increment this by 1 to make the current node in visitedVertices array as visited and others unvisited
    protected int visitedCheck = 1; // a token of sorts for checking if the node is visited or not
    protected int[] visitedVertices; // an int array of visited vertices

    protected boolean maxFlowFound; // check if max flow is found

    protected long maximumFlowForNetwork; // stores the maximum pushable flow for the network

    protected List<Arc>[] networkFlowGraph; // stores the network flow graph itself

    // constructor for the class
    public FordFulkerson(int noOfVertices, int startVertex, int terminalVertex) {
        // initializes with the following methods
        this.noOfVertices = noOfVertices; // no of vertices of input graph
        this.startVertex = startVertex; // start vertex of input file
        this.terminalVertex = terminalVertex; // sink vertex of input file
        initEmptyFlowNetwork(); // initialize an emtpy network flow graph
        visitedVertices = new int[noOfVertices];
        vertexList.add(terminalVertex); // add the terminal vertex for the array in the beginning
        // note, we do this since evalustion of stack happens bottom-to-top, so terminal vertex will be at the end
    }

    // initialize empty flow network
    private void initEmptyFlowNetwork() {
        networkFlowGraph = new List[noOfVertices];
        int index = 0;
        while (index < noOfVertices) {
            networkFlowGraph[index] = new ArrayList<Arc>();
            index++;
        }
    }

    // add arc method
    public void addEdge(int fromVertex, int toVertex, long arcCapacity) {
        // INSERT method for the graph
        if (arcCapacity > 0) {
            Arc forwardEdge;
            forwardEdge = new Arc(fromVertex, toVertex, arcCapacity);
            Arc backwardEdge;
            backwardEdge = new Arc(toVertex, fromVertex, 0);
            forwardEdge.residualArc = backwardEdge;
            backwardEdge.residualArc = forwardEdge;
            networkFlowGraph[fromVertex].add(forwardEdge);
            networkFlowGraph[toVertex].add(backwardEdge);
        } else {
            throw new IllegalArgumentException("A zero capacity Arc isn't an Arc");
        }
    }
    // returns the network flow graph
    public List<Arc>[] getNetworkFlowGraph() {
        startSolvingNetwork(); // computes max flow before returning
        return this.networkFlowGraph; // return final residual flow graph
    }

    public long getMaximumFlow() {
        startSolvingNetwork(); // computes max flow before returning
        return this.maximumFlowForNetwork; // return max flow
    }

    private void startSolvingNetwork() {
        if (!maxFlowFound) {
            maxFlowFound = true;
            computeMaxFlow();
        }
    }


    public void printAugmentedPaths() {
        if (!augmentedPaths.isEmpty()) {
            for (int i = 0, augmentedPathsSize = augmentedPaths.size(); i < augmentedPathsSize; i++) {
                if (augmentedPaths.size() > 20) {
                    System.out.println("Graph too large to print augmenting paths, will print first few and last few");
                    System.out.println(augmentedPaths.get(0));
                    System.out.println(augmentedPaths.get(1));
                    System.out.println(augmentedPaths.get(2));
                    System.out.println("\t\t--- skipped a few ---");
                    System.out.println(augmentedPaths.get(augmentedPaths.size()-3));
                    System.out.println(augmentedPaths.get(augmentedPaths.size()-2));
                    System.out.println(augmentedPaths.get(augmentedPaths.size()-1));
                    return;
                } else {
                    String augmentedPath = augmentedPaths.get(i);
                    System.out.println(augmentedPath);
                }
            }
        }

    }

    public void computeMaxFlow() {
        // initialize computation
        int count = 1; // count to determine which augment
        // this method pushes the flow forward in the flow network and also solves network flow problem
        for (long generatedFlow = depthFirstSearch(startVertex, (Long.MAX_VALUE / 2)); generatedFlow != 0; generatedFlow = depthFirstSearch(startVertex, (Long.MAX_VALUE / 2))) {
            visitedCheck += 1;
            // update the maximum flow by the bottleneck of the found path
            maximumFlowForNetwork += generatedFlow;
            // method to generate augmenting paths print
            if (!vertexList.isEmpty()) {
                String verticesInAugmentingPath = "";
                for (int i = vertexList.size() - 1; i >= 0; i--) {
                    verticesInAugmentingPath += vertexList.get(i) + " - ";
                }

                if (!verticesInAugmentingPath.endsWith(" - " + (this.terminalVertex) + " - ")) {
                    verticesInAugmentingPath += this.terminalVertex + " - ";
                }
                String augmentPathPrintString = String.format("Augmented path %d: '%s' with flow of: %d", count, verticesInAugmentingPath, maximumFlowForNetwork);
                augmentedPaths.add(augmentPathPrintString);
                count++;
            }
            vertexList.clear();
        }
    }

    // SEARCH method for the network flow graph
    private long depthFirstSearch(int currentVertex, long currentFlow) {
        // initially, currentVertex will be the start vertex and currentFlow, will be infinity
        // note, this will be run the last, since this method is recursive
        if (currentVertex != terminalVertex) {
            // mark current node as visited
            visitedVertices[currentVertex] = visitedCheck;

            // create a temporary graph with the outbound edges for the current node
            List<Arc> arcs = networkFlowGraph[currentVertex];

            // iterate through the outbound edges of new temporary graph
            for (int i = 0, arcsSize = arcs.size(); i < arcsSize; i++) {
                Arc arc = arcs.get(i);
                // if no remaining capacity, or already visited, we move on to the next vertex
                if (arc.getRemainingCapacity() <= 0 || visitedVertices[arc.toVertex] == visitedCheck) {
                    continue;
                }
                // if there is remaining capacity
                long bottleNeck = depthFirstSearch(arc.toVertex, min(currentFlow, arc.getRemainingCapacity()));
                if (bottleNeck > 0) {
                    arc.augmentFlow(bottleNeck);
                    vertexList.add(currentVertex);
                    return bottleNeck; // return the bottleNeck of the current arc
                    // note: the bottleneck value propagates up the stacks, so we can augment the other edges in the same augmenting path
                }
            }
            return 0;
            // if not the terminal vertex we return the current flow, else we return zero,
            // symbolizing that the sink/terminal vertex has been reached
        } else {
            return currentFlow;
        }
    }
}
