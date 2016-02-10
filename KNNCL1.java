import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class KNNCL1 {
	private ArrayList<String> reference;
	private Map<ArrayList<Integer>,String> classifierMap;
	private int k;
	
	public KNNCL1(int k){
		this.reference = new ArrayList<String>();
		this.classifierMap = new Hashtable<ArrayList<Integer>,String>();
		this.k = k;
	}
	
	public void train(Set<String> words, Collection<DocumentObject> docs){
		Iterator<String> it = words.iterator();
		String word;
		Iterator<DocumentObject> docIt;
		DocumentObject DOC;
		ArrayList<Integer> point;
		Queue<String> documentWords;
		while(it.hasNext()){
			word = it.next();
			this.reference.add(word);
		}
		docIt = docs.iterator();
		while(docIt.hasNext()){
			DOC = docIt.next();
			documentWords = DOC.getWords();
			point = getPoint(documentWords);
			this.classifierMap.put(point, DOC.getClassifier());
		}
	}
	
	private ArrayList<Integer> getPoint(Queue<String> documentWords){
		ArrayList<Integer> point = new ArrayList<Integer>();
		for(int i = 0; i < this.reference.size(); i++){
			if(documentWords.contains(this.reference.get(i))){
				point.add(i, 1);//getNumberOfWords(documentWords,this.reference.get(i))
			}else{point.add(i, 0);}
		}
		return point;
	}

	private static Integer getNumberOfWords(Queue<String> documentWords,String targetWord) {
		Iterator<String> it = documentWords.iterator();
		String word;
		int count = 0;
		while(it.hasNext()){
			word = it.next();
			if(word.equals(targetWord)){count++;}
		}
		return count;
	}

	public String classify(Queue<String> words){
		String classifyGuess = "";
		ArrayList<Integer> point = getPoint(words), point2;
		Map<ArrayList<Integer>, Double> distance = new Hashtable<ArrayList<Integer>, Double>();
		Iterator<ArrayList<Integer>> it= this.classifierMap.keySet().iterator();
		while(it.hasNext()){
			point2 = it.next();
			distance.put(point2, calculateDistance(point, point2));
		}
		classifyGuess = getMostFreqClassifier(getClosestKNeighbourClassifiers(distance, this.k));
		return classifyGuess;
	}

	public void changeK(int k){this.k=k;}
	
	private static double calculateDistance(ArrayList<Integer> point, ArrayList<Integer> point2){
		double distance = Double.MAX_VALUE;
		int count = 0, difference;
		for(int i = 0; i < point.size(); i++){
			difference = point.get(i) - point2.get(i);
			count += difference * difference;
		}
		distance = Math.sqrt((double)count);
		return distance;
	}

	private Map<String, Integer> getClosestKNeighbourClassifiers(Map<ArrayList<Integer>, Double> distance, int k){
		Map<String, Integer> neighbours = new TreeMap<String, Integer>();
		ArrayList<Integer> holder;
		String classifier;
		int newVal;
		for(int i = 0; i < k; i++){
			holder = getClosestNeighbour(distance);
			if(holder != null){
				distance.remove(holder);
				classifier = this.classifierMap.get(holder);
				if(neighbours.containsKey(classifier)){
					newVal = neighbours.remove(classifier)+1;
					neighbours.put(classifier,newVal);
				}else{
					neighbours.put(classifier,1);
				}
			}//else{System.out.println("NULL: No neighbour");}
		}
		return neighbours;
	}
	
	private static ArrayList<Integer> getClosestNeighbour(Map<ArrayList<Integer>, Double> distance){
		Iterator<ArrayList<Integer>> it = distance.keySet().iterator();
		ArrayList<Integer> holder, currentClosest = null;
		double currentClosestDist = Double.MAX_VALUE;
		while(it.hasNext()){
			holder = it.next();
			if(distance.get(holder) < currentClosestDist){
				currentClosestDist = distance.get(holder);
				currentClosest = holder;
			}
		}
		return currentClosest;
	}

	private static String getMostFreqClassifier(Map<String, Integer> classifierFrequency){
		int highestFreq = 0;
		String classifier, returnClassifier = null;
		Iterator<String> it = classifierFrequency.keySet().iterator();
		while(it.hasNext()){
			classifier = it.next();
			if(classifierFrequency.get(classifier) > highestFreq){
				highestFreq = classifierFrequency.get(classifier);
				returnClassifier = classifier;
			}
		}
		return returnClassifier;
	}

	public void saveParamsToFile(){
		PrintWriter writer;
		try {
			writer = new PrintWriter("./CSE5243HW2Cl1Params", "UTF-8");
			for(int i = 0; i < this.reference.size(); i++){
				writer.print(this.reference.get(i) + " ");
			}
			ArrayList<Integer> k;
			Iterator<ArrayList<Integer>> it = this.classifierMap.keySet().iterator();
			writer.println();
			while(it.hasNext()){
				k = it.next();
				writer.print("[");
				for(int i = 0; i < k.size(); i++){
					writer.print(k.get(i) + " ");
				}
				writer.println("] >> " + this.classifierMap.get(k));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: File not found");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Unsupported Encoding");
		}
	}

	private void loadParamsFromFile(){}
}
