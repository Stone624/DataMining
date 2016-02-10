import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;


public class BayesianCL2 {
	private ArrayList<String> reference, classifiersSet;
	private Map<ArrayList<Integer>,String> classifierMap;
	
	public BayesianCL2(){
		this.reference = new ArrayList<String>();
		this.classifiersSet = new ArrayList<String>();
		this.classifierMap = new Hashtable<ArrayList<Integer>,String>();
	}
	
	public void train(Set<String> words, Collection<DocumentObject> docs){
		Iterator<String> it = words.iterator();
		Iterator<DocumentObject> docIt;
		String word;
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
		fillClassifiers();
	}
	
	private void fillClassifiers(){
		Iterator<String> it = this.classifierMap.values().iterator();
		String c;
		while(it.hasNext()){
			c = it.next();
			if(!this.classifiersSet.contains(c)){this.classifiersSet.add(c);}
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
	
	public String classify(Queue<String> words){
		String classifyGuess = "", currentClassifier;
		ArrayList<Integer> point = getPoint(words), holder;
//		System.out.println("Point: " + point);
		Set<ArrayList<Integer>> setOfOnlyClassifiers;
		Iterator<ArrayList<Integer>> it;
		int length = this.reference.size(), count, lengthOfNewMap;
		ArrayList<Double> percents = new ArrayList<Double>();
		Map<String, Double> percentages = new TreeMap<String, Double>();
		double sum;
		for(int i = 0; i < this.classifiersSet.size(); i++){ //For all classifiers
			currentClassifier = this.classifiersSet.get(i);
			setOfOnlyClassifiers = splitClassifiers(currentClassifier).keySet();
			lengthOfNewMap = setOfOnlyClassifiers.size();
			percents.clear();
			for(int j = 0; j < length; j++){ //For all words
				it = setOfOnlyClassifiers.iterator();
				count = 0;
				while(it.hasNext()){ //For all entries in the map
					holder = it.next();
					if(holder.get(j) == (point.get(j))){count++;}
					
				}
				percents.add((double)count / (double)lengthOfNewMap);
			}
			sum=sum(percents);
			percentages.put(currentClassifier, sum*lengthOfNewMap / (double)this.classifierMap.size());
		}
//		System.out.println("PERCENTAGES FOR EACH CLASSIFIER: " + percentages);
		classifyGuess = getHighestPercentClassification(percentages);
		return classifyGuess;
	}
	
	private String getHighestPercentClassification(Map<String, Double> percentages) {
		String classifier = "", currentClassifier;
		double currentHighest = 0, currentPercent;
		Iterator<String> it = percentages.keySet().iterator();
		while(it.hasNext()){
			currentClassifier = it.next();
			currentPercent = percentages.get(currentClassifier);
			if(currentPercent > currentHighest){
				currentHighest = currentPercent;
				classifier = currentClassifier;
			}
		}
		return classifier;
	}

	private Double sum(ArrayList<Double> percents) {
		double sum = 1;
		for(int i = 0; i < percents.size(); i++){
			sum *= percents.get(i);
		}
		return sum;
	}

	private Map<ArrayList<Integer>, String> splitClassifiers(String classifier) {
		Map<ArrayList<Integer>, String> newMap = new Hashtable<ArrayList<Integer>, String>();
		Iterator<ArrayList<Integer>> it = this.classifierMap.keySet().iterator();
		ArrayList<Integer> holder;
		while(it.hasNext()){
			holder = it.next();
			if(this.classifierMap.get(holder).equals(classifier)){newMap.put(holder, classifier);}
		}
		return newMap;
	}

	public void saveParamsToFile(){
		PrintWriter writer;
		try {
			writer = new PrintWriter("./CSE5243HW2Cl2Params", "UTF-8");
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
}
