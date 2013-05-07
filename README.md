Cluster Genetic Algorithm
=========================

COS 314 Artificial Intelligence practical involving finding centroids for a data set using a genetic algorithm.

To build the application:
-------------------------

Change directory into the folder you found this README file and execute "ant jar".
This should result in a "dist" folder appearing, containing "ClusterGA.jar", the executable jar file.

To run the application:
-----------------------

In the same directory, execute "java -jar dist/ClusterGA.jar [data file] [number of clusters]", making sure to specify a valid path to a data file, and a positive integer specifying the desired number of clusters.

Upon execution, you will receive a number of prompts asking for additional information. Probabilities should be input as floating point numbers, and anything outside of the range [0,1] will be clamped.

After inputting all necessary information correctly, the GA will commence, with progress shown as a percentage. Once complete, a window will open showing statistical information, as well as a visual representation of the clustering in the case of a 2D data set.

