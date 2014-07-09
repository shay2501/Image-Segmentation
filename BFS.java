  	

/**
   COPYRIGHT (C) 2013 Shannon Whalen. All Rights Reserved.
   Classes to manipulate widgets.
   CS 3460:635 Advanced Algorithms
   Project 1 Solution
   BFS Implementation
   @author Shannon Whalen
   @author Arijit Ghosh
   @version 1.0 2013-02-11
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.LinkedList;

public class BFS 
{
    private static char[] _color;
    private static int[] _parent;
    private static int[] _dist;
    private static LinkedList<Integer> _path = new LinkedList<Integer>();
    private static final int EMPTY = -1;
    private static int _src = EMPTY;
    private static int _snk = EMPTY;

    /************************************************/
    /*main will read from standard input to assemble a graph
    * the intention is to pipe in a file with a matrix representation
    * BFS < source_matrix.txt
    * The output will be the shortest path
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
               if(graphMatrix == null)
               {
                   graphMatrix = new int[tokens.length][tokens.length];
               }

               //populate the matrix with the input values
               for(int i = 0; i< tokens.length; i++)
               {
                   graphMatrix[idx][i] = Integer.parseInt(tokens[i]);
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
           //search the matrix and find the shortest path
           breadthFirstSearch(graphMatrix);
           clearPath();
           assemblePath(_src, _snk);
           LinkedList<Integer> pgraph = BFS.getPath();
           
           //printout the path from BFS
           for(int v : pgraph)
           {
               System.out.print(v + " * ");
           }
           System.out.println();
     }
    /************************************************/
    //BFS on adjacency matrix
    public static void breadthFirstSearch(int[][] g)
    {
        long startTime = System.currentTimeMillis();
        
         if(_src == EMPTY || _snk == EMPTY){
             findSrcAndSnk(g);
         }
         
        //setup distance array and parent array and color nodes white
        _dist = new int[g.length];
        _parent = new int[g.length];
        _color = new char[g.length]; 
        
        //clear the path so if someone tries to getPath before assembling they will get an empty path
        //prevents users from using a path from a previous graph
        _path.clear();
        
        int i = 0;
        for(i=0; i< g.length;i++)
        {
          _dist[i] = Integer.MAX_VALUE; //set dist to infinity, 1000 ok for our purpose
          _parent[i] = -1; //set parent to NIL
          _color[i] = 'W';
        }
        
        //Output statements for analysis verification
       //System.out.println("BFS init nodes loop time = " + i);
        
        //set the distance for the source as 0 and set the color to gray to indicate visited
        _dist[_src] = 0;
        _color[_src] = 'G';

        //create queue and initize with the source
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.offer(_src);

        int loopCounts = 0;
         //loop while the queue is full
        while(queue.peek() != null)
        {

          int src = queue.remove();
          int [] neighbors = g[src];

          //for each neighbor, color it and set it's distance
          for(int j=0; j<neighbors.length; j++)
          {
              //if this is an adjacent node, process it 
              if(neighbors[j] > 0){
                int weight = neighbors[j];
             
                if(_color[j] == 'W')
                {
                  _color[j] = 'G'; //set this node color to Gray if white indicating we visited it
                  _dist[j] =  weight; //set the distance of this node plus the distance of the source node
                  _parent[j] = src; //set the node parent
                  queue.offer(j); //enqueue the node so we can see it's neighbors and find distance
                }
              }
              
            //set the color to black for the source node because we just visited all the neighbors
            _color[src] = 'B';
            
            //increase the loop counter to verify analysis
            loopCounts++;
          }
          
        }
        //Output statements for analysis verification
        //System.out.println("BFS Loop Counts = " + loopCounts);
        //System.out.println("BFS system clock timing = " + (System.currentTimeMillis() - startTime));
    }

    /************************************************/
    //based on printPath algorithm in the book, this method assembles the shortest path
    public static void assemblePath( int s, int v)
    {
        if(v == s )
        {
            _path.add(s);
        }
        else if (_parent[v] == -1)
        {
            System.out.println("there is no route from " + s + " to " + v);
        }
        else
        {
          assemblePath(s, _parent[v]);
          _path.add(v);
        }
    }
    /************************************************/
    public static void printParent( )
    {
        for(int i = 0; i < _parent.length; i++){
           System.out.println(i + " belongs to " + _parent[i]);
        }
    }
    /************************************************/ 
    //returns the found path
    public static LinkedList<Integer> getPath()
    {
        return _path;
    }
  /************************************************/
    //returns the found src node
    public static int getSource()
    {
        return _src;
    }
  /************************************************/
    //returns the found sink node
    public static int getSink()
    {
        return _snk;
    }
  /************************************************/ 
    //clears a previously found path
   public static void clearPath()
   {
       _path.clear();
   }
  /**************************************************/
    //this method searches the matrix to find the source and sink
    private static void findSrcAndSnk(int[][] g)
    {
        long startTime = System.currentTimeMillis();
        
        //analysis variable
        int totalLoops = 0;
        
       //this value holds the total from the sum of the row, indicating the output for this node
       int totalOut = 0;
       
       //this is an array holding the total for each column, holding the input for each node
       int[] totalIn = new int[g.length];

       //loop through the matrix, adding all the rows and columns
       for (int i = 0; i < g.length; i++)
       {
           //reset the total out for this row
           totalOut = 0;
          
           for (int j = 0; j < g.length; j++)
           {
                //this is the output from each node, 
               totalOut += g[i][j];

               //add this to the total In for this node
               totalIn[j] += g[i][j];
               
               totalLoops++;
           }
           
           //if totalOut is 0 after the j loop, then we found the sink
           if(totalOut == 0)
           {   
               _snk = i;
           }
       }
      //System.out.println("BFS find snk loop time = " + totalLoops);
        
       int srcLoop = 0;
        //loop through the in array and find the source node
        for(int i = 0; i< totalIn.length; i++)
        {
            if(totalIn[i] == 0)
            {
                _src = i;
            }
            
            srcLoop++;
        }
        
        //Output statements for analysis verification
        //System.out.println("BFS find src loop time = " + srcLoop);
        //System.out.println("BFS find src and snk system clock timing = " + (System.currentTimeMillis() - startTime));
     }
   /************************************************/ 
    
}

