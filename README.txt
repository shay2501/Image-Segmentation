README.txt
Shannon Whalen
Arijit Ghosh


First compile the project
--------------------------------------------------------------------------------
javac ImageSegmentation.java

If no destination file is specified, the program will prefix the input file with "out_"
The JVM option -Xmx1024 will increase the maximum heap size and allow for files of 100 x 100
--------------------------------------------------------------------------------
java -Xmx1024m ImageSegmentation source_file.pgm des_file.pgm

Output: Max flow from Ford Fulkerson and the destination PGM file

To run FordFulkerson with a matrix input file
--------------------------------------------------------------------------------
java FordFulkerson < source_file

Output: Max flow from the algorithm

To run BFS with a matrix input file
--------------------------------------------------------------------------------
java BFS < source_file

Output: The shortest path found from source to sink
