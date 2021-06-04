/* *****************************************************************************
 *  Name:    Thoshitha Kaduruwewa (Rathnayaka)
 *  UOW_ID:  w1714902
 *  IIT_ID:  2018142
 *
 *  Description:
 *
 *
 *  Written:       22/03/2021
 *  Last updated:  8/04/2021
 *
 *  to run project - preferred to use Eclipse IDE
 *  % javac NetworkFlowCLI.java
 *  % java NetworkFlowCLI
 *
 **************************************************************************** */
package w1714902.fordfulkerson;


public class Arc {
    public int fromVertex; // u (arc/edge starting vertex)
    public int toVertex;   // v (arc/edge ending vertex)
    public Arc residualArc;  // creating a residual Network to calculate residual capacities of network
    public long arcFlow;   // flow of the arc
    // arcCapacity is constant and does not change
    public final long arcCapacity;  // maximum capacity of the arc

    /* constructor for Arc class
    *  creates a arc with vertex positions and arc capacity    *
    * */
    protected Arc(int fromVertex, int toVertex, long arcCapacity) {
        // Arc class can only be initialized from fordfulkerson package
        this.fromVertex = fromVertex;  // initialize fromVertex
        this.toVertex = toVertex;  // initialize fromVertex
        this.arcCapacity = arcCapacity; // initialize fromVertex
    }

    /*
    * Returns remaining capacity for edge
    *
    * */
    public long getRemainingCapacity() {
        return (this.arcCapacity - this.arcFlow); // capacity - current flow of the network
    }

    /*
     * Returns if arc is residual or not
     * */
    public boolean getResidualArc() {
        return (this.arcCapacity == 0); // returns true if more flow can be sent through arc
    }

    /*
     * Gets passed in the bottleneck by the depth-first-search algorithm
     * only if it's above zero
     * */
    public void augmentFlow(long bottleNeck) {
        arcFlow += bottleNeck; // bottleneck gets added to arcFlow to help identify future augmentable paths
        residualArc.arcFlow -= bottleNeck; // adjust new residual capacity with augmented flow
    }

    /*
     * Special toString method used to print residual graphs
     * accepts startingVertex and terminalVertex of each edge to print
     * */
    public String toString(int startingVertex, int terminalVertex) {
        // replaces name with s to represent sink node
        String fromVertexOfArc = (this.fromVertex == startingVertex) ? "s" : ((this.fromVertex == terminalVertex) ? "t" : String.valueOf(this.fromVertex));
        // replaces name with t to represent terminal node
        String toVertexOfArc = (this.toVertex == startingVertex) ? "s" : ((this.toVertex == terminalVertex) ? "t" : String.valueOf(this.toVertex));
        // returns the formatted string to print
        return String.format("Arc %s --(%3d/%3d)--> %s | has-residual? = %6s", fromVertexOfArc, this.arcFlow, this.arcCapacity, toVertexOfArc, this.getResidualArc());
    }
}
