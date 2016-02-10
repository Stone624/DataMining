This is my code for verious labs submitted for my Data Mining class during Autumn 2015 semester.

All labs were done according to an online respository of html-like-tagged reuters documents, containing 23 documents of 1000 articles each.

Lab1 was preprocessing from ugly raw data into structured data.

Lab2 is KNN and multinomial Bayesian classifiers. The goal was to predict article label tags given the words.

Lab3 is K-means and DBSCAN clustering algorithms. The goal was similar to the above, but without using class labels - simply group together the most similar sets of articles.

Lab5 was a lab examining error of the Minwise Hashing algorithm on n-gram singles. It calculates the minwise hashing similarity estimate for each shingle, the exact similarity, and then compares the 2.

All of these use the PreProcessing.java, FeatureVector.java, and DocumentObject.java. The Lab2 uses KNNCL1.java and BayesianCL2.java. Lab3 uses KMeans.java and KBSCAN.java.

NOTE: makefile only makes the necessary files, so the command "javac -g [filesToMake]" is necessary to compile other files.
