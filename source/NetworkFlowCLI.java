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
package w1714902;

import w1714902.fordfulkerson.Arc;
import w1714902.fordfulkerson.FordFulkerson;
import w1714902.parser.Parser;
import w1714902.parser.WeightedGraphParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.time.Duration;
import java.time.Instant;

public class NetworkFlowCLI {
    private final static Scanner inputReader = new Scanner(System.in);
    private static String inputFileName;
    private static File inputFile;
    private static boolean shouldSkipLoad;
    private static Parser parsedInputFile;

    public static void main(String[] args) {
        System.out.println("Max-Flow Calculator");
        while (true) {
            // resets the skipLoadChecker
            shouldSkipLoad = false;

            System.out.println("Press 1 to Load new weighted graph");
            System.out.println("Press 0 to Quit the application");

            System.out.print("\nchoice: ");
            String loopChoice = inputReader.nextLine();

            switch (loopChoice) {
                case "0":
                    System.exit(0);
                    break;
                case "1":
                    loadNewGraph();
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }
    private static void loadNewGraph() {
        while(true) {
            // function guard to return if got to here with graph loaded or graph cleared
            if (shouldSkipLoad) {
                return;
            }
            System.out.println("Press 1 to select graph using file explorer");
            System.out.println("Press 2 to enter graph name to select. \n\tNOTE: place it in the 'file-input' folder");
            System.out.println("Press 3 to go back to main menu");

            System.out.print("\nchoice: ");
            String loopChoice = inputReader.nextLine();
            boolean graphLoadError = false;

            switch (loopChoice) {
                // user selects file from system
                case "1":
                    System.out.println("Choose the text input file");
                    System.out.println("Please check the taskbar for a new icon");
                    // parser will throw error for all invalid stuff...
                    // parser everything needs to work on the constructor itself
                    try {
                        Parser fileParser = new WeightedGraphParser();
                        fileParser.readFile();
                        fileParser.loadLines();
                        fileParser.loadValues();
                        if (!fileParser.isFileRead()) {
                            throw new Exception("File not loaded");
                        }
                        inputFileName = fileParser.getFileName();
                        inputFile = fileParser.getFile();
                        parsedInputFile = fileParser;
                    } catch (Exception e) {
                        System.out.println("\nError reading input file, please try again\n");
                        graphLoadError = true;
                    }
                    if (!graphLoadError) {
                        shouldSkipLoad = true;
                        calculateGraph();
                        // update input file
                    } else {
                        continue;
                    }
                    break;
                // user file name input
                case "2":
                    System.out.println("Place the input text file in 'input_files_go_here' folder and enter the text file name in the console");
                    try {
                        // user input loop with basic validation
                        String userInputFileName;
                        do {
                            System.out.println("Note: it should be a text file with the '.txt' file extension");
                            System.out.print("Input file name:  ");
                            userInputFileName = inputReader.nextLine();
                        } while ((!userInputFileName.endsWith(".txt")));

                        Parser fileParser = new WeightedGraphParser();
                        fileParser.readFile("src/input_files_go_here/" + userInputFileName);
                        fileParser.loadLines();
                        fileParser.loadValues();
                        if (!fileParser.isFileRead()) {
                            throw new Exception("File not loaded");
                        }
                        inputFileName = fileParser.getFileName();
                        inputFile = fileParser.getFile();
                        parsedInputFile = fileParser;
                    } catch (Exception e) {
                        System.out.println("\nError reading input file, please try again\n");
                        graphLoadError = true;
                    }

                    if (!graphLoadError) {
                        shouldSkipLoad = true;
                        // update input file
                        calculateGraph();
                    } else {
                        continue;
                    }
                    // if (graphLoaded) => find max flow menu => update inputFileName
                    // if (error when choosing) => continue
                    break;
                case "3":
                    return;
                default:
                    System.out.println("invalid choice");
                    break;
            }
        }
    }

    private static void calculateGraph() {
        // time duration calculations for empherical analysis
        Instant startTime = null;
        Instant endTime = null;
        Duration timeElapsed = null;
        while (true) {
            System.out.println("\nSelected input file: " + inputFileName);
            System.out.println("No of lines: " + parsedInputFile.getLines().size());
            System.out.println("No of nodes in graph: " + parsedInputFile.getNoOfNodes());
            System.out.println("\n");
            System.out.println("Press 1 to find the max flow");
            System.out.println("Press 2 to print the final residual graph");
            System.out.println("Press 3 to print the contents of the file");
            System.out.println("Press 9 to restart the application. \n\tNote: This will unload the loaded input file");

            System.out.print("\nchoice: ");
            String loopChoice = inputReader.nextLine();

            int n,s,t;
            n = parsedInputFile.getNoOfNodes();
            s = 0;
            t = parsedInputFile.getNoOfNodes()-1;

            FordFulkerson solver = new FordFulkerson(n, s, t);
            ArrayList<int[]> loadedEdges;
            List<Arc>[] resultGraph;
            try {
                // TODO: add null checks
                loadedEdges = parsedInputFile.getEdges();
                for (int[] edge: loadedEdges) {
                    solver.addEdge(edge[0], edge[1], edge[2]);
                }
                resultGraph = solver.getNetworkFlowGraph();
            } catch (Exception exception) {
                System.out.println("There was an error when loading the input file values. This is most likely caused by an incompatible input file");
                System.out.println("You will be taken back to the main menu");
                shouldSkipLoad = true;
                return;
            }

            switch (loopChoice) {
                case "1":
                    // calculating time instance
                    if (startTime == null) {
                        startTime = Instant.now();
                    }
                    System.out.println("\nFinding maximum flow of network...\n");
                    solver.printAugmentedPaths();
                    System.out.printf("\nFinal Maximum Flow is of graph: %d\n", solver.getMaximumFlow());
                    if (endTime == null) {
                        endTime = Instant.now();
                        timeElapsed = Duration.between(startTime, endTime);
                    }
                    System.out.println("Time elapsed: "+ timeElapsed.toMillis() +" milliseconds");
                    break;
                case "2":
                    // calculating time instance
                    if (startTime == null) {
                        startTime = Instant.now();
                    }
                    System.out.println("\nFinal Residual Graph");
                    for (List<Arc> arcs : resultGraph) {
                        for (Arc e: arcs) {
                            System.out.println(e.toString(s, t));
                        }
                    }
                    if (endTime == null) {
                        endTime = Instant.now();
                        timeElapsed = Duration.between(startTime, endTime);
                    }
                    System.out.println("\nTime elapsed: "+ timeElapsed.toMillis() +" milliseconds\n");
                    break;
                case "3":
                    System.out.println("Printing " + inputFileName + " content");
                    parsedInputFile.printLines();
                    break;
                case "9":
                    System.out.println("\n");
                    inputFileName = null;
                    parsedInputFile = null;
                    shouldSkipLoad = true;
                    return;
                default:
                    System.out.println("invalid choice\n");
                    break;
            }
        }
    }
}
