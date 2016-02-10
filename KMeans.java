import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
public class KMeans {
	private Cluster[] clusters;
	private String[] reference;
	private Collection<DocumentObject> docs;
	
	public KMeans(int numberOfClusters, Set<String> wordsSet, Collection<DocumentObject> docs){
		clusters = new Cluster[numberOfClusters];
		for(int i = 0; i < numberOfClusters;i++){clusters[i] = new Cluster(wordsSet.size());}
		this.reference = new String[wordsSet.size()];
		this.getWordsReference(wordsSet);
		this.docs=docs;
	}
	
	public void execute(){
		boolean converged = false;
		Iterator<DocumentObject> docIt = docs.iterator();;
		DocumentObject DOC;
		ArrayList<Integer> point;
		double currentMinDist = Double.MAX_VALUE, dist;
		int chosenCluster = -1;
		while(!converged){
			System.out.println("NOT CONVIRGED: Classifying points and recalculating centers...");
			converged = true;
			while(docIt.hasNext()){
				DOC = docIt.next();
				point = getPoint(DOC.getWords());
				currentMinDist = Double.MAX_VALUE;
				for(int i = 0; i < this.clusters.length; i++){
					dist = calculateDist(point,this.clusters[i].getCenter(),false);
					if(dist < currentMinDist){
						currentMinDist = dist;
						chosenCluster = i;
					}
				}
				if(!DOC.getClassifier().equals(chosenCluster + "")){
					if(Integer.getInteger(DOC.getClassifier()) != null){
						this.clusters[Integer.getInteger(DOC.getClassifier())].remove(DOC.getID());
					}
					this.clusters[chosenCluster].add(getPoint(DOC.getWords()), DOC.getID()); // Speed vs memory
					DOC.changeClassifier(chosenCluster + "");
					converged = false;
				}
			}
			
		}
		
	}
	
	//get reference words for use
	private void getWordsReference(Set<String> words){
		Iterator<String> it = words.iterator();
		int i = 0;
		while(it.hasNext()){
			this.reference[i] = it.next();
			i++;
		}
	}
	
	//return an n dimensional binary point of words for a given document.
	private ArrayList<Integer> getPoint(Queue<String> documentWords){
		ArrayList<Integer> point = new ArrayList<Integer>();
		for(int i = 0; i < this.reference.length; i++){
			if(documentWords.contains(this.reference[i])){
				point.add(i, 1);//getNumberOfWords(documentWords,this.reference.get(i))
			}else{point.add(i, 0);}
		}
		return point;
	}

	private static double calculateDist(ArrayList<Integer> point, ArrayList<Double> point2,boolean euclidian){
		double distance = Double.MAX_VALUE, difference;
		int count = 0;
		if(euclidian){
		for(int i = 0; i < point.size(); i++){
			difference = point.get(i) - point2.get(i);
			count += difference * difference;
		}
		distance = Math.sqrt(count + 0.0);
		}else{
			for(int i = 0; i < point.size(); i++){
				difference = Math.abs(point.get(i) - point2.get(i));
				count += difference;
			}
			distance = count;
		}
		return distance;
	}

	
	public Cluster[] getClusters(){return this.clusters;}
}
