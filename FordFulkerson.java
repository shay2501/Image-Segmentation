

/**
   COPYRIGHT (C) 2013 Shannon Whalen. All Rights Reserved.
   Classes to manipulate widgets.
   CS 3460:635 Advanced Algorithms
   Project 1 Solution
   FordFulkerson implementation
   @author Shannon Whalen
   @version 1.0 2013-02-11
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class FordFulkerson 
{
    /**************************************************/
    /*main will read from standard input to assemble a graph
    * the intention is to pipe in a file with a matrix representation
    * FordFulkerson < source_matrix.txt
    * The output is the Max Flow
    */
    public static void main(String args[])
    {
        //adjacency matrix to hold the graph
        int[][] graphMatrix = null;
        
        String line = null;
        int idx = 0;

        BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
        try
        {
          //read in line by line
          line = f.readLine();
          while(line != null)
          {
           String[] tokens = line.split(" ");
           if(graphMatrix == null)graphMatrix = new int[tokens.length][tokens.length];

           //populate the matrix with the input values
           for(int i = 0; i< tokens.length; i++)
           {
               if(i<graphMatrix.length)
               {
                   try{
                    graphMatrix[idx][i] = Integer.parseInt(tokens[i]);
                   }
                   catch(NumberFormatException e)
                   {
                       System.out.println(e.getMessage());
                   }
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

          //process using FordFulkerson from the 0 node to the largest integer node
          System.out.println("Max flow = " + fordFulkerson(graphMatrix));
    }
    /**************************************************/
    public static int fordFulkerson(int[][] g)
    {
        long startTime = System.currentTimeMillis();
        
        int flow[][] = new int[g.length][g.length]; 
     
        //data structure to hold the path
        LinkedList<Integer> pgraph = null;
        
        int src = -1;
        int snk = -1;
        
        int loopCounts = 0;
        while(true)
        {
            //int numPathsFound = 0;
            
           //perform BFS to get a path  
            BFS.breadthFirstSearch(g);
            
            src = BFS.getSource();
            snk = BFS.getSink();
            
            //clear the previous path so we don't get 2 concatenated paths
            BFS.clearPath();
            
            //find shortest path 
            BFS.assemblePath(src, snk);
            pgraph = BFS.getPath();
            
            for(int l : pgraph)
            {
				System.out.print(l + "-->");
			}
			System.out.println();
			
            //if there is no path, then break the loop
            if(pgraph.size()< 2){break;}
             
            //store the path capacity to find the min
            int pathMinCap = Integer.MAX_VALUE;
            
            //for each node in the path, find the minimum path
            int i;
            for(i = 0; i< pgraph.size()-1; i++)
            {
                // get two adjacent vertices from the graph
                int v1 = pgraph.get(i);
                int v2 = pgraph.get(i+1);
                
                // get the capacity for the edge 
                int c = (g[v1][v2]);
                
                //make sure that pathCap has the lowest capacity, the saturated link in the path
                if(c < pathMinCap)
                {
                    pathMinCap = c;
                }
            }
            //System.out.println("FF find min capacity loop time = " + i); 
            
           //now set the flow and augment the capacity
            for(i=0; i< pgraph.size()-1; i++)
            {
                // get two adjacent vertices from the graph
                int v1 = pgraph.get(i);
                int v2 = pgraph.get(i+1);
                
                //update the residual network
                g[v1][v2] -= pathMinCap;
                g[v2][v1] += pathMinCap;

                //set the positive and negative flow in the residual network
                flow[v1][v2] += pathMinCap;
                flow[v2][v1] -= pathMinCap;
            }
            
            loopCounts++;
        }
        
        //calcluate the max flow, which is anything that is coming from the source
        //all other flows will be 0 because there is no flow back to s
        int maxFlow = 0;
        int count = 0;
        for (int x : flow[src])
        {	
            maxFlow += x;
            count++;
        }   
        
        //analysis statements
        //System.out.println("Number of paths found by BFS = " + loopCounts);
        //System.out.println("FordFulkerson system clock timing = " + (System.currentTimeMillis() - startTime));
        
       return maxFlow;
    }
    /************************************************/
    //returns the found src node
    public static int getSource()
    {
        return BFS.getSource();
    }
  /************************************************/
    //returns the found sink node
    public static int getSink()
    {
        return BFS.getSink();
    }
  /************************************************/ 
    
}
