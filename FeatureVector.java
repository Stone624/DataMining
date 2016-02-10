import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class FeatureVector {
	private ArrayList<Integer> documentNumber;
	private ArrayList<Integer> frequency;
	private ArrayList<Double> TFIDF;
	
	public FeatureVector(int documentNumber){
		this.documentNumber = new ArrayList<Integer>();
		this.documentNumber.add(documentNumber);
		this.frequency = new ArrayList<Integer>();
		this.frequency.add(1);
		this.TFIDF = new ArrayList<Double>();
	}
	
	public void increaseFrequency(int index){this.frequency.set(index, this.frequency.get(index)+1);}
	
	public int getPosition(int documentNumber){return this.documentNumber.indexOf(documentNumber);}
	
	public double getTFIDFAt(int documentNumber){return this.TFIDF.get(documentNumber);}
	
	public Iterator<Integer> getDocumentNumbersIterator(){return this.documentNumber.iterator();}
	
	public Iterator<Integer> getFrequenciesIterator(){return this.frequency.iterator();}
	
	public int getSize(){return this.documentNumber.size();}
	
	public boolean contains(int documentNumber){return this.documentNumber.contains(documentNumber);}
	
	public void addDocument(int documentNumber){this.documentNumber.add(documentNumber);
	this.frequency.add(1);}
	
	public void setTFIDF(Map<Integer,DocumentObject> Documents){
		double tf,idf;
		for(int i=0;i<this.documentNumber.size();i++){
			tf=(double)this.frequency.get(i)/Documents.get(this.documentNumber.get(i)).getDocumentLength();
			idf = Math.log(Documents.size() / (double)this.getSize());
			this.TFIDF.add(tf*idf);
		}
	}
	
	public Iterator<Double> getTFIDFIterator(){return this.TFIDF.iterator();}
	
	public int getHighestTFIDFDocumentIndex(){
		int bestDocumentIndex = -1;
		double currentLeader = 0, current;
		for(int i = 0; i < this.getSize();i++){
			current = this.TFIDF.get(i);
			if(current > currentLeader){
				currentLeader = current;
				bestDocumentIndex = i;
			}
		}
		return bestDocumentIndex;
	}
}
