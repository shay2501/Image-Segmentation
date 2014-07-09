
/**
   COPYRIGHT (C) 2013 Shannon Whalen. All Rights Reserved.
   Classes to manipulate widgets.
   CS 3460:635 Advanced Algorithms
   Project 1 Solution
   ImageSegmentation Implementation
   @author Shannon Whalen
   @version 1.0 2013-02-11
*/

import java.io.*;
import java.util.LinkedList;

//the graph contains a linked list of vertices and their corresponding edges
public class ImageSegmentation
{    
    private static final int SCALE_SRC_SNK = 4;
    private static final int SCALE_PENALTY = 2;
    private static final int SOURCE = 0;
    
    /**************************************************/
    public static void main(String[] args) 
    {
        long startTime = System.currentTimeMillis();
        
        //adjacency matrix to hold the graph
        int[][] graphMatrix = null;
        
        //if there was no filename, read in the matrix from standard input
        if(args.length < 1)
        {
          System.out.println("No file specified for input! Attempting to read from standard input");
          
          String line = null;
          
          //since we don't know how many lines there will be, 
          int idx = 0;
          
          BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
          try
          {
            //read in line by line
            line = f.readLine();
            
            //read until there is nothing left
            while(line != null)
            {
                //extract the values from the input and init the matrix if needed
                String[] tokens = line.split(" ");
                if(graphMatrix == null)graphMatrix = new int[tokens.length][tokens.length];

                //populate the matrix with the input values
                for(int i = 0; i< tokens.length; i++)
                {
                    //if we haven't read past the size of the matrix, set the value
                    if(idx < graphMatrix.length)
                    {
                        graphMatrix[idx][i] = Integer.parseInt(tokens[i]);
                    }
                }

                //read the next line and increment the row index
                line = f.readLine();
                idx++;
            }
          }
          catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
          }
          
           //this prints out the matrix to assure it was read in correctly
           //printAdjMatrix(graphMatrix);
            
            //process using FordFulkerson to find the max flow
            System.out.println("Max Flow = " + FordFulkerson.fordFulkerson(graphMatrix));
        }
        
        //if we have an arugment, assume it is the filename and process the pgmFile
        else{
            String fileName = args[0];
        
            if(fileName.substring(fileName.length()-3).equalsIgnoreCase("pgm"))
            { 
                //array to return the len, width, maxValue
                int[] fileData = new int[3];

                //data structures to hold the pgm file data
                int [][] pgmFile = readPGMInFile(fileName, fileData);
                int length = fileData[0];
                int width = fileData[1];
                int maxValue = fileData[2];

                //return if the file is 
                if(pgmFile == null) return;

                /**************************************************/
                //initialize the graph from the file and print out pgm file, adding 2 for src and snk
                int totalNodes = length * width + 2;

                //initialize the adjacency matrix
                graphMatrix = new int[totalNodes][totalNodes];
                createAdjMatrix(pgmFile, graphMatrix, length, width, maxValue);
                
                //release the pgm file and call garbage collection
                pgmFile = null;
                System.gc();
                
                //after initialization, this is the adjacency matrix
                //printAdjMatrix(graphMatrix);

                //call FordFulkerson to find max flow
                System.out.println("Max flow = " + FordFulkerson.fordFulkerson(graphMatrix));
                
                //retrieve or derive the output filename
                if(args.length > 1)
                {
                  fileName = args[1];
                }
                else
                {
                  fileName = "out_" + args[0];
                }
                
                //print out the output file
                outputPGMFile(fileName, length, width, maxValue, graphMatrix);

                //this prints remaining residual network and any nodes still connected to S
                //printAdjMatrix(graphMatrix);
            }
            else
            {
                System.out.println("Unrecognized file format! Please specify a .pgm file");
            }
        }
        //Output statements for analysis verification
        //System.out.println("Image Segmentation complete in  = " + (System.currentTimeMillis() - startTime) + " ms for graph size " + graphMatrix.length);
    }
    /**************************************************/
    //this method just loops through rows and columns printing out the values
    public static void printAdjMatrix(int[][] matrix)
    {
        
        for (int i = 0;i< matrix.length; i++)
        {
            for (int j = 0;j< matrix.length; j++)
            {
                System.out.print(matrix[i][j] + " ");
            }
        
        System.out.println();
        }
    }
    /**************************************************/
    //this reads in a portable gray map file and returns it
    private static int[][] readPGMInFile(String filename, int[] fileData)
    {
        long startTime = System.currentTimeMillis();
        
        //keep track of the line numbers of the file
        int lineNum = 1;
        
         //index counters for reading into the matrix because 
        //pgm file might not be in a tight row * column matrix
        int iRow = 0;
        int iCol = 0;
        
        //length and width and maxvalue from pgmfile
        int length = 0;
        int width = 0;
        int maxValue = 0;
        
        int[][] pgmFile = null;
         
        //open pgm file
        try
        {
            FileReader reader = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(reader);
           
            String line = buffer.readLine();
            
            //First read the file into a len * width matrix
            while(line != null){
                System.out.println(line);
                
                //tokenize the input to get individual values
                String[] tokens = null;
                if(lineNum == 3) {
                  tokens = line.split(" ");
                }
                else{
                  tokens = line.split("\t");
                }
                
                //read in the dimensions of the grid and initialize the 2d array holding the file contents
                if(lineNum ==  3 && tokens.length >= 2)
                {
      
                    length = Integer.parseInt(tokens[0].trim());
                    width = Integer.parseInt(tokens[1].trim());
                    
                    //set returning fileData
                    fileData[0] = length;
                    fileData[1] = width;
                   
                    pgmFile = new int[width][length];
                }
                
                //line 4 has max value of a node
                else if(lineNum == 4 && tokens.length > 0)
                {
                    maxValue = Integer.parseInt(tokens[0].trim());
                     //set returning fileData
                    fileData[2] = maxValue;
                }
                
                //if we are past 4, then start adding data to the grid 
                else if(lineNum > 4)
                {
                  
                    //loop through tokens in the file and populate the matrix
                    for(int i = 0; i < tokens.length; i++)
                    {
                        //if we are at the end of a row, 
                        //increment the row so we can process the next 
                        if(iCol >= length)
                        {
                            iCol = 0;
                             iRow++;
                        }
                        
                       //populate the pgmFile row
                        if(iCol < length && iRow < width){
                           pgmFile[iRow][iCol] = Integer.parseInt(tokens[i].trim());
                            
                        }
                        
                        //increment column
                        iCol++;
                    }
                } 
               //read another line and increase the line counter
               line = buffer.readLine();
               lineNum++;
           }
        }
        //for filereader
        catch(FileNotFoundException ex)
        {
            System.out.println("Can't find file");
            System.exit(1);
           
        }
        //for buffer readline
        catch(IOException ex)
        {
            System.out.println("Can't read file");
            System.exit(1);
        }
        
        //Output statements for analysis verification
        //System.out.println("Read pgm file in complete in  = " + (System.currentTimeMillis() - startTime) );
        System.out.println(pgmFile.length + " x " + pgmFile[0].length);
        return pgmFile;
        
    }
    /**************************************************/
    //this populates an adjacency matrix from a pgm file
    private static void createAdjMatrix(int[][] pgmFile, int[][] adjMatrix, int length, int width, int maxValue)
    {
        long startTime = System.currentTimeMillis();
        
        //local variables
        int nodeId = 1;
        //int totalNodes = adjMatrix.length;
        int sumNodeVal = 0;
        
        int loopCounts = 0;
       
        for(int h =0; h< pgmFile.length; h++)//the horizontal row
        {
            for(int j = 0; j< pgmFile[0].length; j++)//the vertical columns
            {
                //add the current node value to the sum 
                sumNodeVal += pgmFile[h][j];
                
                //add the vertex to the right if we aren't at the end of a row
                if(j < length-1)
                {
                    //calculate the weight of the edge
                    adjMatrix[nodeId][nodeId + 1] = (maxValue - Math.abs(pgmFile[h][j] - pgmFile[h][j+1]))/SCALE_PENALTY;   
                }
                //add the vertex to the left if we aren't at the beginning of a row
                if(j > 0)
                {
                    //calculate the weight of the edge
                    adjMatrix[nodeId][nodeId - 1] = (maxValue - Math.abs(pgmFile[h][j] - pgmFile[h][j-1]))/SCALE_PENALTY;               
  
                }
                //add vertex the node below this one if we aren't at the last row
                if((h < width -1 ))
                {
                    /*calculate the weight of the edge, 
                     * the higher the number/capcity, 
                     * the more alike the node values are*/
                    adjMatrix[nodeId][nodeId + length] = (maxValue - Math.abs(pgmFile[h][j] - pgmFile[h+1][j]))/SCALE_PENALTY; 
                }
                //add vertex above this one if we aren't at the 0 row
                if((h > 0 ))
                {
                    /*calculate the weight of the edge, 
                     * the higher the number/capcity, 
                     * the more alike the node values are*/
                    adjMatrix[nodeId][nodeId - length] = (maxValue - Math.abs(pgmFile[h][j] - pgmFile[h-1][j]))/SCALE_PENALTY;

                }
                
                //increase the node id
                nodeId++;
                
                //increase loop counts for analysis
                loopCounts++;
            }
        }
        
        //Output statements for analysis verification
        //System.out.println("Create Matrix loop counts " + loopCounts ); 
        //System.out.println("Create Matrix before calling src and sink complete in  = " + (System.currentTimeMillis() - startTime) );
        //now that we determined the average pixel value, set the source and sink
        setSourceAndSink((sumNodeVal/nodeId), pgmFile, adjMatrix, maxValue);
        //System.out.println("Create Matrix after calling src and sink complete in  = " + (System.currentTimeMillis() - startTime) );
        

    }
   /**************************************************/
   //this method sets the source and sink according to how close it is to the average value of the nodes
    private static void setSourceAndSink(int avg, int[][] pgmFile, int[][] adjMatrix, int maxValue){
        
        long startTime = System.currentTimeMillis();
        int loopCounts = 0;
        
        //start at the first node and go to the last
        int nodeId = 1;
        int totalNodes = adjMatrix.length;
        
         for(int h =0; h< pgmFile.length; h++)//the horizontal row
        {
            for(int j = 0; j< pgmFile[0].length; j++)//the vertical columns
            {
                //if the value is greater than average, weight the connection with the sink
                if(pgmFile[h][j]>= avg)
                {
                    //the sink is the last column
                    adjMatrix[nodeId][totalNodes - 1] = SCALE_SRC_SNK * maxValue;
                    
                    //source is first row
                    adjMatrix[SOURCE][nodeId] = maxValue;
                   
                }
                //otherwise weight the source
                else
                {
                     //the sink is the last column
                    adjMatrix[nodeId][totalNodes - 1] = maxValue;
                    
                    //source is first row
                    adjMatrix[SOURCE][nodeId] = maxValue * SCALE_SRC_SNK;
                }
                
                //increase node id and process the next pixel
                nodeId++;
                
                loopCounts++;
            }
         }
         //Output statements for analysis verification
         //System.out.println("Src and sink loop counter = " + loopCounts );
         //System.out.println("Src and sink complete in  = " + (System.currentTimeMillis() - startTime) );
        
    }
    /**************************************************/
    //write out the new pgm file
    private static void outputPGMFile(String outFile, int length, int width, int maxValue, int[][]data)
    {   
        long startTime = System.currentTimeMillis();
    
        //get the src and sink
        int src = FordFulkerson.getSource() ;
        int snk = FordFulkerson.getSink();
        
        try
        {
            FileWriter writer = new FileWriter(outFile);
            BufferedWriter buffer = new BufferedWriter(writer);
            
            buffer.write("P2\n");
            buffer.write("#Created by Image Segmentation\n");
            buffer.write(length + " " + width + "\n");
            buffer.write(maxValue + "\n");
            
            //loop through all but the 0 and last, which are our added source and sink
            for(int i =1; i<(data.length-1);i++)
            {
                //print a new line if we are at the end of length
                if(i!= 1 && i%(length) == 0)buffer.write("\n");
                
                //if this node is connected to the source, make it black
                if(data[src][i] != 0)
                {
                    buffer.write("0 ");
                }
                //if connected to sink, color white
                else if(data[i][snk] != 0)
                {
                     buffer.write(maxValue + " ");
                }
                //indicate orphans
                else
                {
                    System.out.println("Missing Data = " + i);
                }       
            }
            buffer.close();
        }
        catch(Exception e)
        {
            System.out.println("An error occurred " + e.getMessage());
            
        }
        System.out.println("output " + outFile);
         //analysis statements
         //System.out.println("Output pgm file  = " + (System.currentTimeMillis() - startTime) );
    }
    
    /**************************************************/       
}

