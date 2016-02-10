//Tyler Stone
//CSE 5243 Lab 1
//Professor Fuhry
//9/13/15
//Pre-processor for reuters articles

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PreProcessing {
	private Map<String,FeatureVector> wordsList;
	private Map<Integer,DocumentObject> documents;
	public PreProcessing(int wordSize, int numberOfFiles){
		this.wordsList= new TreeMap<String,FeatureVector>();
		this.documents = new TreeMap<Integer,DocumentObject>();
		this.DoMain(wordSize, numberOfFiles);
	}
	private void DoMain(int wordSize, int numberOfFiles){
		System.out.println("---PREPROCESSING---");
//Initialize variables
//		Set<String> stopWords = getStopWords();
		Queue<String> words = new LinkedList<String>();
		Iterator<String> wordsIterator;
		String inputUrl, article, body, word;
		int artNum=0;
		FeatureVector FV;
		DocumentObject DOC;
	
//get articles
		for(int i = 0; i <= numberOfFiles-1; i++){
		//get the URL for the article and get a scanner for it. 
				if(i>=10){inputUrl="http://web.cse.ohio-state.edu/~srini/674/public/reuters/reut2-0"+i+".sgm";
				}else{inputUrl="http://web.cse.ohio-state.edu/~srini/674/public/reuters/reut2-00"+i+".sgm";}
				Scanner s = getInput(inputUrl);
				System.out.println("Processing File "+i+"/"+(numberOfFiles-1));
		//pick out the first article
				article = getArticle(s,"<REUTERS","</REUTERS>");
				while(article != ""){
					artNum++;
		//get the document classifier
//					word = parseClassifier(article);
					word="";
//					stopWords.add(word);
		//parse the body from the article
					body = extractBody(article).toLowerCase();
		//parse each word from the article, create a set of words in this article
					words = parseWords(body);
					DOC = new DocumentObject(word, words, artNum);
					documents.put(artNum, DOC);
					wordsIterator = words.iterator();
					while(wordsIterator.hasNext()){
						word = wordsIterator.next();
						if(wordsList.containsKey(word)){
							FV = wordsList.get(word);
							if(FV.contains(artNum)){
								FV.increaseFrequency(FV.getPosition(artNum));
							}else{FV.addDocument(artNum);}
						}else{FV = new FeatureVector(artNum);
							wordsList.put(word, FV);
						}
					}
					article = getArticle(s,"<REUTERS","</REUTERS>");
				}
				s.close();
		}
//		System.out.println("Pruning WordsList of stop words...");
//		pruneWords(wordsList, stopWords);
//		System.out.println("Calculating TF-IDF...");
//		wordsIterator = wordsList.keySet().iterator();
//		while(wordsIterator.hasNext()){
//			word = wordsIterator.next();
//			wordsList.get(word).setTFIDF(documents);
//		}
//		System.out.println("Tidying up data vectors...");
//		wordsList = prettyUpWords(wordsList,wordSize);
//		prettyUpDocuments(documents);
//		pruneDocumentWords(wordsList, documents);
		
//		System.out.println("Writing data to files...");
//		printWords(wordsList);
//		printDocuments(documents);
		
		System.out.println("---PreProcessing Complete---");
	}
	
	//Create and return a set of stop words
	private static Set<String> getStopWords(){
		System.out.println("Creating Stop Words...");
		Set<String> stopwords = new TreeSet<String>();
		stopwords.add("a");
		stopwords.add("about");
		stopwords.add("above");
		stopwords.add("across");
		stopwords.add("after");
		stopwords.add("again");
		stopwords.add("against");
		stopwords.add("all");
		stopwords.add("almost");
		stopwords.add("alone");
		stopwords.add("along");
		stopwords.add("already");
		stopwords.add("also");
		stopwords.add("although");
		stopwords.add("always");
		stopwords.add("among");
		stopwords.add("an");
		stopwords.add("and");
		stopwords.add("another");
		stopwords.add("any");
		stopwords.add("anybody");
		stopwords.add("anyone");
		stopwords.add("anything");
		stopwords.add("anywhere");
		stopwords.add("are");
		stopwords.add("area");
		stopwords.add("areas");
		stopwords.add("around");
		stopwords.add("as");
		stopwords.add("ask");
		stopwords.add("asked");
		stopwords.add("asking");
		stopwords.add("asks");
		stopwords.add("at");
		stopwords.add("away");
		stopwords.add("b");
		stopwords.add("back");
		stopwords.add("backed");
		stopwords.add("backing");
		stopwords.add("backs");
		stopwords.add("be");
		stopwords.add("became");
		stopwords.add("because");
		stopwords.add("become");
		stopwords.add("becomes");
		stopwords.add("been");
		stopwords.add("before");
		stopwords.add("began");
		stopwords.add("behind");
		stopwords.add("being");
		stopwords.add("beings");
		stopwords.add("best");
		stopwords.add("better");
		stopwords.add("between");
		stopwords.add("big");
		stopwords.add("both");
		stopwords.add("but");
		stopwords.add("by");
		stopwords.add("c");
		stopwords.add("came");
		stopwords.add("can");
		stopwords.add("cannot");
		stopwords.add("case");
		stopwords.add("cases");
		stopwords.add("certain");
		stopwords.add("certainly");
		stopwords.add("clear");
		stopwords.add("clearly");
		stopwords.add("come");
		stopwords.add("could");
		stopwords.add("d");
		stopwords.add("did");
		stopwords.add("differ");
		stopwords.add("different");
		stopwords.add("differently");
		stopwords.add("do");
		stopwords.add("does");
		stopwords.add("done");
		stopwords.add("down");
		stopwords.add("down");
		stopwords.add("downed");
		stopwords.add("downing");
		stopwords.add("downs");
		stopwords.add("during");
		stopwords.add("e");
		stopwords.add("each");
		stopwords.add("early");
		stopwords.add("either");
		stopwords.add("end");
		stopwords.add("ended");
		stopwords.add("ending");
		stopwords.add("ends");
		stopwords.add("enough");
		stopwords.add("even");
		stopwords.add("evenly");
		stopwords.add("ever");
		stopwords.add("every");
		stopwords.add("everybody");
		stopwords.add("everyone");
		stopwords.add("everything");
		stopwords.add("everywhere");
		stopwords.add("f");
		stopwords.add("face");
		stopwords.add("faces");
		stopwords.add("fact");
		stopwords.add("facts");
		stopwords.add("far");
		stopwords.add("felt");
		stopwords.add("few");
		stopwords.add("find");
		stopwords.add("finds");
		stopwords.add("first");
		stopwords.add("for");
		stopwords.add("four");
		stopwords.add("from");
		stopwords.add("full");
		stopwords.add("fully");
		stopwords.add("further");
		stopwords.add("furthered");
		stopwords.add("furthering");
		stopwords.add("furthers");
		stopwords.add("g");
		stopwords.add("gave");
		stopwords.add("general");
		stopwords.add("generally");
		stopwords.add("get");
		stopwords.add("gets");
		stopwords.add("give");
		stopwords.add("given");
		stopwords.add("gives");
		stopwords.add("go");
		stopwords.add("going");
		stopwords.add("good");
		stopwords.add("goods");
		stopwords.add("got");
		stopwords.add("great");
		stopwords.add("greater");
		stopwords.add("greatest");
		stopwords.add("group");
		stopwords.add("grouped");
		stopwords.add("grouping");
		stopwords.add("groups");
		stopwords.add("h");
		stopwords.add("had");
		stopwords.add("has");
		stopwords.add("have");
		stopwords.add("having");
		stopwords.add("he");
		stopwords.add("her");
		stopwords.add("here");
		stopwords.add("herself");
		stopwords.add("high");
		stopwords.add("high");
		stopwords.add("high");
		stopwords.add("higher");
		stopwords.add("highest");
		stopwords.add("him");
		stopwords.add("himself");
		stopwords.add("his");
		stopwords.add("how");
		stopwords.add("however");
		stopwords.add("i");
		stopwords.add("if");
		stopwords.add("important");
		stopwords.add("in");
		stopwords.add("interest");
		stopwords.add("interested");
		stopwords.add("interesting");
		stopwords.add("interests");
		stopwords.add("into");
		stopwords.add("is");
		stopwords.add("it");
		stopwords.add("its");
		stopwords.add("itself");
		stopwords.add("j");
		stopwords.add("just");
		stopwords.add("k");
		stopwords.add("keep");
		stopwords.add("keeps");
		stopwords.add("kind");
		stopwords.add("knew");
		stopwords.add("know");
		stopwords.add("known");
		stopwords.add("knows");
		stopwords.add("l");
		stopwords.add("large");
		stopwords.add("largely");
		stopwords.add("last");
		stopwords.add("later");
		stopwords.add("latest");
		stopwords.add("least");
		stopwords.add("less");
		stopwords.add("let");
		stopwords.add("lets");
		stopwords.add("like");
		stopwords.add("likely");
		stopwords.add("long");
		stopwords.add("longer");
		stopwords.add("longest");
		stopwords.add("m");
		stopwords.add("made");
		stopwords.add("make");
		stopwords.add("making");
		stopwords.add("man");
		stopwords.add("many");
		stopwords.add("may");
		stopwords.add("me");
		stopwords.add("member");
		stopwords.add("members");
		stopwords.add("men");
		stopwords.add("might");
		stopwords.add("more");
		stopwords.add("most");
		stopwords.add("mostly");
		stopwords.add("mr");
		stopwords.add("mrs");
		stopwords.add("much");
		stopwords.add("must");
		stopwords.add("my");
		stopwords.add("myself");
		stopwords.add("n");
		stopwords.add("necessary");
		stopwords.add("need");
		stopwords.add("needed");
		stopwords.add("needing");
		stopwords.add("needs");
		stopwords.add("never");
		stopwords.add("new");
		stopwords.add("new");
		stopwords.add("newer");
		stopwords.add("newest");
		stopwords.add("next");
		stopwords.add("no");
		stopwords.add("nobody");
		stopwords.add("non");
		stopwords.add("noone");
		stopwords.add("not");
		stopwords.add("nothing");
		stopwords.add("now");
		stopwords.add("nowhere");
		stopwords.add("number");
		stopwords.add("numbers");
		stopwords.add("o");
		stopwords.add("of");
		stopwords.add("off");
		stopwords.add("often");
		stopwords.add("old");
		stopwords.add("older");
		stopwords.add("oldest");
		stopwords.add("on");
		stopwords.add("once");
		stopwords.add("one");
		stopwords.add("only");
		stopwords.add("open");
		stopwords.add("opened");
		stopwords.add("opening");
		stopwords.add("opens");
		stopwords.add("or");
		stopwords.add("order");
		stopwords.add("ordered");
		stopwords.add("ordering");
		stopwords.add("orders");
		stopwords.add("other");
		stopwords.add("others");
		stopwords.add("our");
		stopwords.add("out");
		stopwords.add("over");
		stopwords.add("p");
		stopwords.add("part");
		stopwords.add("parted");
		stopwords.add("parting");
		stopwords.add("parts");
		stopwords.add("per");
		stopwords.add("perhaps");
		stopwords.add("place");
		stopwords.add("places");
		stopwords.add("point");
		stopwords.add("pointed");
		stopwords.add("pointing");
		stopwords.add("points");
		stopwords.add("possible");
		stopwords.add("present");
		stopwords.add("presented");
		stopwords.add("presenting");
		stopwords.add("presents");
		stopwords.add("problem");
		stopwords.add("problems");
		stopwords.add("put");
		stopwords.add("puts");
		stopwords.add("q");
		stopwords.add("quite");
		stopwords.add("r");
		stopwords.add("rather");
		stopwords.add("really");
		stopwords.add("right");
		stopwords.add("right");
		stopwords.add("room");
		stopwords.add("rooms");
		stopwords.add("s");
		stopwords.add("said");
		stopwords.add("same");
		stopwords.add("saw");
		stopwords.add("say");
		stopwords.add("says");
		stopwords.add("second");
		stopwords.add("seconds");
		stopwords.add("see");
		stopwords.add("seem");
		stopwords.add("seemed");
		stopwords.add("seeming");
		stopwords.add("seems");
		stopwords.add("sees");
		stopwords.add("several");
		stopwords.add("shall");
		stopwords.add("she");
		stopwords.add("should");
		stopwords.add("show");
		stopwords.add("showed");
		stopwords.add("showing");
		stopwords.add("shows");
		stopwords.add("side");
		stopwords.add("sides");
		stopwords.add("since");
		stopwords.add("small");
		stopwords.add("smaller");
		stopwords.add("smallest");
		stopwords.add("so");
		stopwords.add("some");
		stopwords.add("somebody");
		stopwords.add("someone");
		stopwords.add("something");
		stopwords.add("somewhere");
		stopwords.add("state");
		stopwords.add("states");
		stopwords.add("still");
		stopwords.add("still");
		stopwords.add("such");
		stopwords.add("sure");
		stopwords.add("t");
		stopwords.add("take");
		stopwords.add("taken");
		stopwords.add("than");
		stopwords.add("that");
		stopwords.add("the");
		stopwords.add("their");
		stopwords.add("them");
		stopwords.add("then");
		stopwords.add("there");
		stopwords.add("therefore");
		stopwords.add("these");
		stopwords.add("they");
		stopwords.add("thing");
		stopwords.add("things");
		stopwords.add("think");
		stopwords.add("thinks");
		stopwords.add("this");
		stopwords.add("those");
		stopwords.add("though");
		stopwords.add("thought");
		stopwords.add("thoughts");
		stopwords.add("three");
		stopwords.add("through");
		stopwords.add("thus");
		stopwords.add("to");
		stopwords.add("today");
		stopwords.add("together");
		stopwords.add("too");
		stopwords.add("took");
		stopwords.add("toward");
		stopwords.add("turn");
		stopwords.add("turned");
		stopwords.add("turning");
		stopwords.add("turns");
		stopwords.add("two");
		stopwords.add("u");
		stopwords.add("under");
		stopwords.add("until");
		stopwords.add("up");
		stopwords.add("upon");
		stopwords.add("us");
		stopwords.add("use");
		stopwords.add("used");
		stopwords.add("uses");
		stopwords.add("v");
		stopwords.add("very");
		stopwords.add("w");
		stopwords.add("want");
		stopwords.add("wanted");
		stopwords.add("wanting");
		stopwords.add("wants");
		stopwords.add("was");
		stopwords.add("way");
		stopwords.add("ways");
		stopwords.add("we");
		stopwords.add("well");
		stopwords.add("wells");
		stopwords.add("went");
		stopwords.add("were");
		stopwords.add("what");
		stopwords.add("when");
		stopwords.add("where");
		stopwords.add("whether");
		stopwords.add("which");
		stopwords.add("while");
		stopwords.add("who");
		stopwords.add("whole");
		stopwords.add("whose");
		stopwords.add("why");
		stopwords.add("will");
		stopwords.add("with");
		stopwords.add("within");
		stopwords.add("without");
		stopwords.add("work");
		stopwords.add("worked");
		stopwords.add("working");
		stopwords.add("works");
		stopwords.add("would");
		stopwords.add("x");
		stopwords.add("y");
		stopwords.add("year");
		stopwords.add("years");
		stopwords.add("yet");
		stopwords.add("you");
		stopwords.add("young");
		stopwords.add("younger");
		stopwords.add("youngest");
		stopwords.add("your");
		stopwords.add("yours");
		stopwords.add("z");
		return stopwords;
	}
	
	//Create and return a scanner pointer to the file url u
	private static Scanner getInput(String u){
		Scanner s = null;
		try {
			URL url = new URL(u);
			s = new Scanner(url.openStream());
		} catch (MalformedURLException e) {
			System.out.println("ERROR: BAD URL // "+e);
		} catch (IOException e) {
			System.out.println("ERROR: IO EXCEPTION // "+e);
		}
		return s;
	}
	
	//Parse and return a single article from between the start and end tags
	private static String getArticle(Scanner s,String start, String end){
		String intermediate = "";
		String article = "";
		boolean done = false;
		s.findWithinHorizon(start, 1000);
		s.findWithinHorizon(">",1000);
		while(!done && s.hasNextLine()){
			intermediate = s.nextLine() + " ";
			if(intermediate.contains(end)){done=true;}
			article += intermediate;
		}
		return article;
	}
	
	//parse and return the classifier from the header
	private static String parseClassifier(String article){
		int start = article.indexOf("<TOPICS><D>");
		int end = article.indexOf("</D>");
		if(start < 0 || end < 11 || (end)-(start+11) <= 0){article = "";}else{article = article.substring(start+11, end);}
		return article;
	}
	
	//parse and return the body from the article 
	private static String extractBody(String article){
		int start = article.indexOf("<BODY>");
		int end = article.indexOf("</BODY>");
		if(start < 0 || end < 11 || (end-11)-(start+6) <= 0){article = "";}else{article = article.substring(start+6, end-11);}
		return article;
	}
	
	//Parse the words from the body into a Queue
	private static Queue<String> parseWords(String body){
		Queue<String> words = new LinkedList<String>();
		String pattern = "[^\r\n\t\f 0-9~!@#$%^&*()_+`=,.?\"\'\\<>{}|/:;\\[\\]-]+";
		String intermediate;
	      Pattern r = Pattern.compile(pattern);
	      Matcher m = r.matcher(body);
	      while(m.find()){
	    	  intermediate = body.substring(m.start(), m.end());
	    	  words.add(intermediate);
	      }
	      return words;
	}
	
	//Pretty up the documents -- clear all documents with no classifier or no words
	private static void prettyUpDocuments(Map<Integer, DocumentObject> documents){
		Iterator<Integer> it = documents.keySet().iterator();
		Set<Integer> toRemove = new TreeSet<Integer>();
		int docNum;
		DocumentObject DOC;
		while(it.hasNext()){
			docNum = it.next();
			DOC = documents.get(docNum);
			if(DOC.getClassifier() == "" || DOC.getWords().size()==0){toRemove.add(docNum);}
		}
		Iterator<Integer> setIt = toRemove.iterator();
		while(setIt.hasNext()){
			docNum = setIt.next();
			documents.remove(docNum);
		}
	}
	
	//prune the stop words and classifiers from the words list
	private static void pruneWords(Map<String,FeatureVector> words, Set<String> stopWords){
		Iterator<String> it = stopWords.iterator();
		String word;
		while(it.hasNext()){
			word = it.next();
			if(words.containsKey(word)){
				words.remove(word);
			}
		}
	}
	
	//select only the top [count] TFIDF words, drop all others
	private static Map<String, FeatureVector> prettyUpWords(Map<String,FeatureVector> words, int count){
		Map<String,FeatureVector> newMap = new TreeMap<String,FeatureVector>();
		FeatureVector FV = null;
		String word;
		for(int i = 0; i < count; i++){
			word = getHighestTFIDF(words);
			if(word != null){
				FV = words.remove(word);
				newMap.put(word, FV);
			}
		}
		return newMap;
	}
	
	//remove all non-pretty'd words from all documents
	private static void pruneDocumentWords(Map<String,FeatureVector> words, Map<Integer, DocumentObject> docs){
		Iterator<Integer> it = docs.keySet().iterator();
		int docNum;
		Queue<String> legitimateWords, holder;
		DocumentObject DOC = null;
		String word;
		while(it.hasNext()){
			legitimateWords = new LinkedList<String>();
			docNum = it.next();
			DOC = docs.get(docNum);
			holder = DOC.getWords();
			while(!holder.isEmpty()){
				word = holder.poll();
				if(words.containsKey(word)){legitimateWords.add(word);}
			}
			DOC.addWords(legitimateWords);
		}
	}
	
	//get and return the word key with the highest TFIDF (USED in prettyUpWords) 
	private static String getHighestTFIDF(Map<String,FeatureVector> words){
		Iterator<String> it = words.keySet().iterator();
		double currentHighestTFIDF = 0, TFIDF;
		String word, FV = null;
		int docIndex;
		while(it.hasNext()){
			word = it.next();
			docIndex = words.get(word).getHighestTFIDFDocumentIndex();
			TFIDF = words.get(word).getTFIDFAt(docIndex);
			if(TFIDF > currentHighestTFIDF){
				currentHighestTFIDF = TFIDF;
				FV = word;
			}
		}
		return FV;
	}
	
	//print the wordsList map to a file
	private static void printWords(Map<String,FeatureVector> wordsList){
		Set<String> words = wordsList.keySet();
		Iterator<String> wordsIterator = words.iterator();
		Iterator<Integer> DocumentIterator, FrequencyIterator;
		Iterator<Double> TFIDFIterator;
		String word;
		int documentNumber, frequency;
		double tfidf;
		PrintWriter writer;
		try {
			writer = new PrintWriter("./CSE5243HW2Words", "UTF-8");
			while(wordsIterator.hasNext()){
				word = wordsIterator.next();
				if(wordsList.get(word) != null){
				writer.print(word + ": {");
				DocumentIterator = wordsList.get(word).getDocumentNumbersIterator();
				FrequencyIterator = wordsList.get(word).getFrequenciesIterator();
				TFIDFIterator = wordsList.get(word).getTFIDFIterator();
				while(DocumentIterator.hasNext()){
					documentNumber = DocumentIterator.next();
					frequency = FrequencyIterator.next();
					tfidf = TFIDFIterator.next();
					writer.print("["+documentNumber + ","+frequency+",");
					writer.printf("%.4f", tfidf);
					writer.print("]");
				}
				writer.println("}");
				}
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
	
	//print the documents map to a file
	private static void printDocuments(Map<Integer,DocumentObject> documents){
		Set<Integer> docNums = documents.keySet();
		Iterator<Integer> docNumIterator= docNums.iterator();
		Iterator<String> words;
		int doc;
		String word;
		PrintWriter writer;
		try {
			writer = new PrintWriter("./CSE5243HW2Documents", "UTF-8");
			while(docNumIterator.hasNext()){
				doc = docNumIterator.next();
				writer.print(doc + ": " + documents.get(doc).getClassifier() + " [");
				words = documents.get(doc).getWords().iterator();
				while(words.hasNext()){
					word = words.next();
					writer.print(word + ",");
				}
				writer.println("]");
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

	//getter methods for the wordsList and Documents
	public Map<String,FeatureVector> getWordsList(){return this.wordsList;}
	public Map<Integer,DocumentObject> getDocuments(){return this.documents;}
}
