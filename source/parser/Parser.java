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

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author w1714902
 */
public abstract class Parser {
    private File inputFile;
    private final ArrayList<String> lines = new ArrayList<>();
    private boolean isRead; // a proper reading is allowed only once;
    private boolean isLoaded;
    protected int noOfNodes;
    protected final ArrayList<int[]> edges = new ArrayList<>();
    protected Scanner lineScanner = null;

    public Integer getNoOfNodes() {
        if (isLoaded()) {
            return this.noOfNodes;
        }
        return null;
    }

    public ArrayList<int[]> getEdges() {
        if (isLoaded()) {
            return edges;
        }
        return null;
    }

    public Boolean isFileRead(){
        return this.isRead;
    }

    public String getFileName() {
        if (isLoaded) {
            return inputFile.getName();
        }
        return null;
    }

    public Boolean isLoaded() {
        if (this.isFileRead()) {
            return this.isLoaded;
        }
        return null;
    }

    public File getFile() {
        if (this.isRead) {
            return inputFile;
        }
        return null;

    }

    public ArrayList<String> getLines() {
        if (this.isRead) {
            return this.lines;
        }
        return null;
    }

    public void printLines() {
        if (this.isRead) {
            for (String line: lines) {
                System.out.println(line);
            }
        }
    }

    public void readFile() throws Exception {
        // FileDialog explorer file select code
        FileDialog fileDialog = new FileDialog((Frame)null, "Select Input File To Load");
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setDirectory(System.getProperty("user.dir"));
        fileDialog.setFile("*.txt");
        fileDialog.setVisible(true);
        String file = fileDialog.getFile();
        String fileType = file.substring(Math.max(0, file.length()-4));

        // throw exception if user selected invalid file type
        if (!fileType.equals(".txt"))
            throw new Exception("Only text input files are allowed. File extension of the input file should be .txt");

        File inputFile = fileDialog.getFiles()[0];
        if (inputFile.length() == 0) throw new Exception("No file was selected");

        // set input file
        this.inputFile = fileDialog.getFiles()[0];
        this.isRead = true;
    }

    public void readFile(String path) throws FileNotFoundException {
        File inputFile;
        inputFile = new File(path);

        // TODO: check if the following is necessary
        if (inputFile.length() == 0) {
            throw new FileNotFoundException("File " + path + " does not exist");
        }

        // set input file
        this.inputFile = inputFile;
        this.isRead = true;
    }

    public void loadLines() throws IOException {
        if (this.isRead) {
            lines.addAll(Files.readAllLines(inputFile.toPath(), Charset.defaultCharset()));
            this.isLoaded = true;
        }
    }

    public abstract boolean loadValues();
}
