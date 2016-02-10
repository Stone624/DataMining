import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class CSE5243Lab4 {
	public static void main(String[] args){
		//Initialise variables
		int wordSize = 256, numberOfFiles = 3, numberOfClusters = 10,minPts=20;//max fileSize=22;
		double epsilon=2.0;
		long startTime, endTime;
		//get and preprocess data
		PreProcessing p = new PreProcessing(wordSize, numberOfFiles);
		Map<String, FeatureVector> FV = p.getWordsList();
		Map<Integer, DocumentObject> DOCTraining = p.getDocuments();
		Map<Integer, String> IdClass = getIDtoClass(DOCTraining);
//		p=null;
		System.out.println("\n\nWord Size: "+wordSize);
		System.out.println("Number of documents: "+DOCTraining.size());
		System.out.println("Epsilon (DBSCAN): " + epsilon + "\nMinPts (DBSCAN): "+minPts);
		System.out.println("Number of Clusters (KMeans): "+numberOfClusters);
		//Create clusterer
		KMeans c1 = new KMeans(numberOfClusters, FV.keySet(), DOCTraining.values());
		DBSCAN c2 = new DBSCAN(minPts,epsilon,FV.keySet(),DOCTraining.values());
		Cluster[] c = new Cluster[1];
		//Execute clusterer
		System.out.println("Executing K-means");
		startTime = System.currentTimeMillis();
		c1.execute();
		endTime = System.currentTimeMillis();
		System.out.println("TIMING ANALYSIS>> TIME TO EXEC: "+ (endTime-startTime) + " ms on size "+DOCTraining.size());
		//Test clusterer
		c = c1.getClusters();
		numberOfClusters = c.length;
		printClusterData(numberOfClusters, IdClass, c);
		
		System.out.println("Executing DBSCAN");
		startTime = System.currentTimeMillis();
		c2.execute();
		endTime = System.currentTimeMillis();
		System.out.println("TIMING ANALYSIS>> TIME TO EXEC: "+ (endTime-startTime) + " ms on size "+DOCTraining.size());
		//Test clusterer
		c = new Cluster[1];
		c = c2.getClusters().toArray(c);
		numberOfClusters = c.length;
		printClusterData(numberOfClusters, IdClass, c);
		
		//Finish
		System.out.println("Done.");
	}
	
	private static Map<Integer, String> getIDtoClass(Map<Integer, DocumentObject> DOCTraining){
		Map<Integer, String> IdClass = new TreeMap<Integer, String>();
		Iterator<Integer> it = DOCTraining.keySet().iterator();
		int k = 0;
		while(it.hasNext()){
			k = it.next();
			IdClass.put(DOCTraining.get(k).getID(), DOCTraining.get(k).getClassifier());
		}
		return IdClass;
	}

	
	private static void printClusterData(int numberOfClusters,Map<Integer, String> IdClass,Cluster[] c){
		System.out.println("Number of Clusters: "+numberOfClusters);
		ArrayList<Integer> documentNumbers;
		Map<String, Integer> clusteredClassifiers = new TreeMap<String, Integer>();
		String someHolder;
		Iterator<String> it;
		int sum = 0, currentHighest, noise=0;;
		double entropy = 0.0, h=0;
		System.out.println("Significant clusters with n>2 documents:");
		for(int i = 0; i < numberOfClusters; i++){//For every cluster
			sum = 0;
			documentNumbers = c[i].getMapOfDocuments();//Get the list of document ID's associated with the cluster
			for(int k = 0; k < documentNumbers.size(); k++){//For every Document
				someHolder = IdClass.get(documentNumbers.get(k));//get the original document class
				if(clusteredClassifiers.containsKey(someHolder)){//Add the class to the map, or increment the amount 
					clusteredClassifiers.put(someHolder, clusteredClassifiers.get(someHolder)+1);
				} else {
					clusteredClassifiers.put(someHolder, 1);
				}
				sum++;
			}
			//print out statistics for the cluster
			if(sum > 2){
				System.out.println("\nCLUSTER "+i+" STATISTICS: ");
				System.out.println("NUMBER OF DOCUMENTS: " + documentNumbers.size());
//				System.out.println("DOCUMENTS: " + documentNumbers);
				System.out.println("CLASSIFIERS: " + clusteredClassifiers.size());
				it = clusteredClassifiers.keySet().iterator();
//				System.out.println("MAP: ");
				currentHighest = 0;
				while(it.hasNext()){
					someHolder = it.next();
//					System.out.print("Classifier: " + someHolder +", ");
					if(clusteredClassifiers.get(someHolder) > currentHighest){currentHighest = clusteredClassifiers.get(someHolder);}
//					System.out.print(clusteredClassifiers.get(someHolder) + " / " + sum + " -- ");
						h=clusteredClassifiers.get(someHolder) / (sum + 0.0);
						entropy += (h*Math.log(h));
//					System.out.println(h);
				}
			System.out.println("Highest purity: " + currentHighest + " / " + sum);
			System.out.println("Entropy: " + (-1 *entropy));
			}else{noise++;}
		entropy = 0.0;
		clusteredClassifiers.clear();
		}
		System.out.println((numberOfClusters-noise)+" / "+numberOfClusters+" significant (>2 document) clusters.");
	}
}
