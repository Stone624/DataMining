import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;


public class DBSCAN {
	private ArrayList<Cluster> clusters;
	private String[] reference;
	private Collection<DocumentObject> docs;
	private int minPts;
	private double epsilon;
	private int[] type;//1=Core, 2 = Border, 3 = Noise
	
	public DBSCAN(int minPts, double epsilon, Set<String> wordsSet, Collection<DocumentObject> docs){
		clusters = new ArrayList<Cluster>();
		this.reference = new String[wordsSet.size()];
		this.getWordsReference(wordsSet);
		this.docs=docs;
		this.minPts=minPts;
		this.epsilon=epsilon;
		this.type = new int[docs.size()];
	}
	
	public void execute(){
		DocumentObject[] docArray= this.docs.toArray(new DocumentObject[this.docs.size()]);
		ArrayList<Integer> comeBackTo = new ArrayList<Integer>();
		ArrayList<Double> point;
		DocumentObject DOC;
		int p = 0;
		double dist = 0.0;
		boolean isBorder = false;
		Set<Integer> seen= new TreeSet<Integer>();
		//For every point
		System.out.println("Getting initial point types");
		for(int i = 0; i < docArray.length; i++){
		//Look at all other points, count # of points within epsilon
			p=0;
			DOC = docArray[i];
			point = getPoint(DOC.getWords());
			for(int j = 0; j < docArray.length; j++){
				if(j!=i){
					dist = calculateDist(point, getPoint(docArray[j].getWords()),true);
					if(dist <= this.epsilon){p++;}
				}
			}
			//if >= minpoints, mark as core
			if(p >= this.minPts){
				this.type[i] = 1;
			//else if 0 mark as Noise
			}else if(p == 0){
				this.type[i] = 3;
			//else mark as TBD (could be border or Noise point)
			}else{
				comeBackTo.add(i);
			}
		}
		//To do, check all points within epsilon. If any is core point, mark as Border. Else mark as Noise
		System.out.println("Addressing possible border/noise points");
		for(int i = 0; i < comeBackTo.size(); i++){
			isBorder = false;
			p=comeBackTo.get(i);
			DOC = docArray[p];
			point = getPoint(DOC.getWords());
			for(int j = 0; j < docArray.length; j++){
				if(j!=i){
					dist = calculateDist(point, getPoint(docArray[j].getWords()),true);
					if(dist <= this.epsilon && this.type[j] == 1){
						isBorder=true;
						this.type[p] = 2;
						break;
					}
				}
			}
			if(!isBorder){
				this.type[p] = 3;
			}
		}
		
		System.out.println("Splitting into clusters...");
		while(seen.size() < docArray.length){
			this.clusters.add(new Cluster(this.reference.length));
			clusterDocs(docArray,seen,min(seen),0);
		}
		System.out.println("Done.");
//		System.out.print("DATA DUMP: [");
//		for(int i = 0; i < this.type.length;i++){
//		System.out.print(this.type[i]+",");
//		}
//		System.out.println("]");
	}
	
	private int min(Set<Integer> seen) {
		int i = 0;
		while(seen.contains(i)){i++;}
		return i;
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
	private ArrayList<Double> getPoint(Queue<String> documentWords){
		ArrayList<Double> point = new ArrayList<Double>();
		for(int i = 0; i < this.reference.length; i++){
			if(documentWords.contains(this.reference[i])){
				point.add(i, 1.0);//getNumberOfWords(documentWords,this.reference.get(i))
			}else{point.add(i, 0.0);}
		}
		return point;
	}
	

	private static double calculateDist(ArrayList<Double> point, ArrayList<Double> point2, boolean euclidian){
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
	
	public ArrayList<Cluster> getClusters(){return this.clusters;}
	
	//Type determines noise/core+border cluster. 2=Noise, 1=Core+border, 0=unknown - Find out
	private void clusterDocs(DocumentObject[] docArray, Set<Integer> seen, int i, int type){
		if(seen.size() < docArray.length && !seen.contains(i)){
			DocumentObject DOC;
			Queue<Integer> neighbors = new LinkedList<Integer>();
			ArrayList<Double> point;
			double dist = 0.0;
			int a=0,t=type;
			if(type == 0){
				if(this.type[i] == 3){t=2;}else{t=1;}
			}
			DOC = docArray[i];
			point = getPoint(DOC.getWords());
			this.clusters.get(this.clusters.size()-1).add(convert(point), DOC.getID());
			seen.add(i);
			for(int j = 0; j < docArray.length; j++){
				if(j!=i){
					dist = calculateDist(point, getPoint(docArray[j].getWords()),true);
					if(dist <= this.epsilon){
						if((t==2 && this.type[j] == 3) || (t==1 && (this.type[j]==1 || this.type[j] == 2))){
							neighbors.add(j);
						}
					}
				}
			}
			while(neighbors.size() != 0){
				a=neighbors.poll();
				clusterDocs(docArray,seen,a,t);
			}
		
		}
	}
	
	private static ArrayList<Integer> convert(ArrayList<Double> point){
		ArrayList<Integer> a= new ArrayList<Integer>();
		double d;
		for(int i = 0; i < point.size(); i++){
			d=point.get(i);
			a.add((int)d);
		}
		return a;
	}
}
