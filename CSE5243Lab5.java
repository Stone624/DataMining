import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

//Last lab - Minwise Hashing test for reuters documents
public class CSE5243Lab5 {
	public static void main(String[] args){
		int numberOfFiles = 3,k=5;
		long startTime, endTime, startTotal, endTotal;
		//preprocess
		startTotal = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		PreProcessing p = new PreProcessing(1, numberOfFiles);
		endTime=System.currentTimeMillis();
		System.out.println("Preprocessing completed in " + (endTime-startTime) + " ms");
		//get documents
		Map<Integer, DocumentObject> DOCS = p.getDocuments();
		int docSize = DOCS.size();
		System.out.println("NumberOfFiles: " + numberOfFiles + ", "+k+"-gram shingles, " + DOCS.size() + " documents.");
//		System.out.println(DOCS);
		//Shingle (optional) into k-grams > Shingle x Document matrix
		System.out.println("[1]getting shingles...");
		startTime = System.currentTimeMillis();
		ArrayList<String[]> shingles = getShingles(DOCS,k);
		endTime=System.currentTimeMillis();
		System.out.println("getShingles completed in " + (endTime-startTime) + " ms");
		System.out.println("Number of Shingles: "+shingles.size());
		System.out.println("[2]constructing matrix");
		startTime = System.currentTimeMillis();
		ArrayList<Set<Integer>> matrix = constructKGramMatrix(shingles, DOCS,k); //<<NOTE: DOCS map index 1-1000 -> matrix arrayList index 0-999
		endTime=System.currentTimeMillis();
		System.out.println("constructKGramMatrix completed in " + (endTime-startTime) + " ms");
		//calculate exact jaccard similarity for reference
		System.out.println("[3] calculating exact jaccard similarity");
		startTime = System.currentTimeMillis();
		ArrayList<Double> jaccardSimilarities = calculateJaccard(matrix);
		endTime=System.currentTimeMillis();
		System.out.println("JaccardSimilarities completed in " + (endTime-startTime) + " ms");
		
		//generate 100 random A's, B's, large prime P, and n
		int kMH = 256;
		int[] a = new int[kMH];
		int[] b = new int[kMH];
		for(int i = 0; i < kMH; i++){
			Random r = new Random();
			a[i] = r.nextInt();
			b[i] = r.nextInt();
		}
		int prime=2147480009,n=shingles.size();//prime from http://www.prime-numbers.org/prime-number-2147480000-2147485000.htm
		//Loop through x [ax+b%p%n] in a document till find first instance of shingle >> report to PI's document sim matrix
		int [][] minHashSimilarityMatrix = new int[kMH][docSize];
		System.out.println("[4]fillingMinHashMatrix");
		startTime = System.currentTimeMillis();
		fillMinHashMatrix(a,b,prime,minHashSimilarityMatrix,shingles,matrix);
		endTime=System.currentTimeMillis();
		System.out.println("fillingMinHashMatrix completed in " + (endTime-startTime) + " ms");
		System.out.println("---Permutation sequences go here---");
//		for(int i = 0; i < 100; i++){
//			System.out.print(i+":[");
//			for(int j = 0; j < 1000; j++){
//				System.out.print(minHashSimilarityMatrix[i][j] + ",");
//			}
//			System.out.println("]");
//		}
		//calculate similarities for all docs >> Compare to exact and output results (sum of squares error)
		System.out.println("[5]Calculating minHash Similarities (jaccard estimate)");
		startTime = System.currentTimeMillis();
		ArrayList<Double> minHashSimilarities = calculateMinHash(minHashSimilarityMatrix);
		endTime=System.currentTimeMillis();
		System.out.println("MinHashSimilarities completed in " + (endTime-startTime) + " ms");
		System.out.println("[6]Calculating Sum Of Squares Error");
		startTime = System.currentTimeMillis();
		double sumOfSquaresError = calculateError(jaccardSimilarities, minHashSimilarities)/2;
		//NOTE: Similarites in vector of form [D11 D12 ... D1n D21 D22 ... D2n ... Dn1 Dn2 ... Dnn] where Dij is
		//the similarity between document i and j. Since the jaccard and minhash for Dii are both 1, this there is
		//no contribution to SSE for this. For all other, the SSE is 2x what it should be, both Dij and Dji are
		//both added [Dij^2 + Dji^2 = 2(Dij^2) => 2(D11^2) + 2(D12^2) + ... + 2(Dnn^2) = 2(D11^2 + D12^2 + .. Dnn^2)
		//So the true Sum of squares is 1/2 of what the method returns. this should then be divided by #OfDocuments
		//To get the Mean Squared error.
		endTime=System.currentTimeMillis();
		System.out.println("sumOfSquaresError completed in " + (endTime-startTime) + " ms");
		System.out.println("mean Squared error: " + sumOfSquaresError/docSize);
		endTotal = System.currentTimeMillis();
		System.out.println("TOTAL program time: " + (endTotal-startTotal) + "ms");
	}

//////////////////////////////////////////////////////////////////////////////////////////
	
private static double calculateError(ArrayList<Double> jaccardSimilarities,
			ArrayList<Double> minHashSimilarities) {
		double sumOfSquares = 0, holder;
		int size = jaccardSimilarities.size();
		if(jaccardSimilarities.size() != minHashSimilarities.size()){System.out.println("***WARNING: Similarity matrix lengths differ");}
		for(int i = 0; i < size; i++){
			//System.out.println("[6]: " + (i+1) +"/"+ size);
			holder = (jaccardSimilarities.get(i) - minHashSimilarities.get(i));
			sumOfSquares += holder*holder;
		}
		return sumOfSquares;
	}

private static ArrayList<Double> calculateMinHash(
			int[][] minHashSimilarityMatrix) {
		ArrayList<Double> sim = new ArrayList<Double>();
		int count = 0, size = minHashSimilarityMatrix[0].length, size2 = minHashSimilarityMatrix.length;
		for(int i = 0; i < size; i++){
			System.out.print(".");if((i+1)%(size/10)==0){System.out.println();}//System.out.println("[5]: " + (i+1) +"/"+ size);
			for(int j = 0; j < size; j++){
				count = 0;
				for(int k = 0; k < size2; k++){
					if(minHashSimilarityMatrix[k][i] == minHashSimilarityMatrix[k][j]){count++;}
				}
				sim.add((count + 0.0) / size2);
			}
		}
		return sim;
	}

private static void fillMinHashMatrix(int[] a, int[] b,int p,
			int[][] minHashSimilarityMatrix, ArrayList<String[]> shingles,ArrayList<Set<Integer>> matrix) {
	int d, size = minHashSimilarityMatrix.length;
	boolean got = false;
	//for every permutation
	for(int i = 0; i < size; i++){
		//for every document
		System.out.print(".");if((i+1)%(size/10)==0){System.out.println();}//System.out.println("[4]permutation: "+i);
		for(int j = 0; j < matrix.size(); j++){
			got=false;
			//for every shingle
			for(int k = 0; k < shingles.size(); k++){
				//get the shingle index of the kth permutation pair, check if Document contains shingle
				//if it does, add the k to the similarity matrix permuation[i] document[j], else k++ and retry
				d=((a[i]*k+b[i])%p)%shingles.size();
				if(matrix.get(j).contains(d)){minHashSimilarityMatrix[i][j] = k;got=true;break;}
			}
			if(!got){minHashSimilarityMatrix[i][j] = -1;}
		}
	}
		
	}

////////////////////////////////////////////////////////////////////////////////////////////
	
	private static ArrayList<Double> calculateJaccard(ArrayList<Set<Integer>> matrix) {
		ArrayList<Double> jaccardSimilarities = new ArrayList<Double>();
		int size = matrix.size();
		double overlap, bottom, result;
		for(int i = 0; i < size; i++){
			System.out.print(".");if((i+1)%(size/10)==0){System.out.println();}//System.out.println("[3]: " + (i+1) +"/"+ size);
			for(int j = 0; j < size; j++){
				overlap = overlap(matrix.get(i),matrix.get(j)) + 0.0;
				bottom = (matrix.get(i).size() + matrix.get(j).size() - overlap);
//				if(overlap / bottom > .75 && i != j){
//				System.out.println("Similarities between documents "+(i+1)+" and "+(j+1)+": "+overlap+"/"+bottom+" = "+overlap/bottom);
//				}
				result = overlap / bottom;
				if(Double.isNaN(result)){result=0.0;}
				jaccardSimilarities.add(result);
			}
		}
		return jaccardSimilarities;
	}
	
	private static int overlap(Set<Integer> s1, Set<Integer> s2){
		int overlap = 0, x;
		Iterator<Integer> it = s1.iterator();
		while(it.hasNext()){
			x = it.next();
			if(s2.contains(x)){overlap++;}
		}
		return overlap;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////

	//create and return list of shingles
	private static ArrayList<String[]> getShingles(Map<Integer, DocumentObject> DOCS, int k) {
		ArrayList<String[]> shingles = new ArrayList<String[]>();
		int size = DOCS.size();
		DocumentObject currentDoc;
		String[] words, gram = null;
		for(int i = 1; i <= size; i++){
			System.out.print(".");if(i%(size/10)==0){System.out.println();}//System.out.println("[1]: " + i + "/" + size);
			currentDoc = DOCS.get(i);
			words = currentDoc.getWords().toArray(new String[0]);
			if(words.length >= k){
				insertShingles(words, gram,shingles, k);
			}
		}
		return shingles;
	}
	//USED^
	//insert k word shingles from words into shingles::Testedv/
			private static void insertShingles(String[] words, String[] gram,
					ArrayList<String[]> shingles,int k) {
				for(int i = 0; i < words.length-(k-1); i++){
					gram = new String[k];
					for(int j = i; j < i+k; j++){
						gram[j-i] = words[j];
					}
					if(!contains(shingles,gram)){shingles.add(gram);}
				}
			}
			//USED^
			private static boolean contains(ArrayList<String[]> shingles, String[] gram){
				boolean contains = false;
				for(int i = 0 ; i < shingles.size(); i++){
					if(equals(shingles.get(i), gram)){contains = true;}
				}
				return contains;
			}
			//USED^
			private static boolean equals(String[] s1, String[] s2){
				if(s1.length != s2.length){return false;}
				boolean eq = true;
				for(int i = 0; i < s1.length;i++){
					if(!s1[i].equals(s2[i])){eq = false; break;}
				}
				return eq;
			}
			
//////////////////////////////////////////////////////////////////////////////////////////////
			
	//create the single x document matrix : array of arraylists <D1, D2, ... , Dn>
	private static ArrayList<Set<Integer>> constructKGramMatrix(ArrayList<String[]> shingles,Map<Integer, DocumentObject> DOCS,int k) {
		//<S{S in D1}, S{S in D2}, ..., S{S in Dn}>
		ArrayList<Set<Integer>> documentShingles = new ArrayList<Set<Integer>>(); 
		Set<Integer> set = new TreeSet<Integer>();
		int size = DOCS.size();
		DocumentObject currentDoc;
		String[] words, gram;
		//For every document
		for(int a = 1; a <= size; a++){
			System.out.print(".");if(a%(size/10)==0){System.out.println();}//System.out.println("[2]: " + a + "/" + size);
			currentDoc = DOCS.get(a);
			words = currentDoc.getWords().toArray(new String[0]);
			//Get all k-grams in 
			set = new TreeSet<Integer>();
			for(int i = 0; i < words.length-(k-1); i++){
				gram = new String[k];
				//get A k-gram
				for(int j = i; j < i+k; j++){
					gram[j-i] = words[j];
				}
				for(int j = 0; j < shingles.size(); j++){
					if(equals(shingles.get(j), gram)){set.add(j);}
				}
			}
			documentShingles.add(set);
		}
		return documentShingles;
	}
}
