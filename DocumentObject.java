import java.util.LinkedList;
import java.util.Queue;


public class DocumentObject {
	private String classifier;
	private Queue<String> words;
	private int documentID;
	
	public DocumentObject(String classifier){
		this.classifier = classifier;
		this.words = new LinkedList<String>();
		this.documentID = 0;
	}
	public DocumentObject(String classifier, Queue<String> words){
		this.classifier = classifier;
		this.words = words;
		this.documentID = 0;
	}
	
	public DocumentObject(String classifier, Queue<String> words, int id){
		this.classifier = classifier;
		this.words = words;
		this.documentID = id;
	}
	
	public void addWord(String word){this.words.add(word);}
	
	public void addWords(Queue<String> words){this.words = words;}
	
	public String getClassifier(){return this.classifier;}
	
	public Queue<String> getWords(){return this.words;}
	
	public int getDocumentLength(){return this.words.size();}
	
	public void changeClassifier(String word){this.classifier = word;}
	
	public int getID(){return this.documentID;}
}
