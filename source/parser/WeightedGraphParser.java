/* *****************************************************************************
 *  Name:    Thoshitha Kaduruwewa (Rathnayaka)
 *  UOW_ID:  w1714902
 *  IIT_ID:  2018142
 *
 *  Description:  Finds the maximum flow of an entered flow network.
 *                Uses an input file, which the user can name or select from file explorer.
 *                The input file needs to be formatted in a specif way for the application to load the flow graph.
 *
 *  Written:       22/03/2021
 *  Last updated:  7/04/2021
 *
 *  % javac NetworkFlowCLI.java
 *  % java NetworkFlowCLI
 *
 **************************************************************************** */
package w1714902.parser;

import java.util.Scanner;

/**
 *
 * @author w1714902
 */
public class WeightedGraphParser extends Parser{

    @Override
    public boolean loadValues() {
        // function guard
        if (!this.isLoaded()) {
            return false;
        }

        this.lineScanner = new Scanner(this.getLines().get(0));
        if (lineScanner.hasNextInt()) {
            this.noOfNodes = lineScanner.nextInt();
        } else {
            // TODO: throw invalid input file format custom exception here
            // TODO: add the same exception for edges as well
            // TODO: get rid of the else guard and create a function guard by making the hasNextInt !
            System.out.println("Invalid input file format exception");
        }

        // adding edges
        boolean fistLineSkipped = false;
        for (String line: this.getLines()) {
            if (fistLineSkipped) {
                int[] edgeParameters = new int[3];
                this.lineScanner = new Scanner(line);
//                Scanner lineScanner = new Scanner(line);
                int counter = 0;
                while (lineScanner.hasNextInt()) {
                    if (counter < 3) {
                        edgeParameters[counter] = lineScanner.nextInt();
                    }
                    counter++;
                }
                lineScanner.close();
                lineScanner = null;
                // edge arraylist contains an array of integers
                this.edges.add(edgeParameters);
            }
            fistLineSkipped = true;
        }
        return true;
    }
}
