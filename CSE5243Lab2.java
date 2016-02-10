import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class CSE5243Lab2 {
	public static void main(String[] args){
		int wordSize = 512, numberOfFiles = 22, fraction = 10;
		long startTime, endTime;
//		for(int a = 2; a < 12; a++){
//			for(int f = 1; f < 1; f++){
//			wordSize = (int) Math.pow(2, a);
//			fraction = 10/f;
			System.out.println("Word Size: "+wordSize);
			System.out.println("Fraction: "+fraction);
		PreProcessing p = new PreProcessing(wordSize, numberOfFiles);
		Map<String, FeatureVector> FV = p.getWordsList();
		Map<Integer, DocumentObject> DOCTraining = p.getDocuments(), DOCTesting;
		
		//Creation
		System.out.println("Creating KNN CL1 classifier");
		KNNCL1 cl1 = new KNNCL1(1);
		System.out.println("Creating KNN CL2 classifier");
		BayesianCL2 cl2 = new BayesianCL2();
		
		//Parsing
		System.out.println("Parsing documents into Training + Testing");
		DOCTesting = parseToTrainingTesting(DOCTraining,fraction);
		
		//Training
		System.out.println("Training CL1...");
		startTime = System.currentTimeMillis();
		cl1.train(FV.keySet(), DOCTraining.values());
		endTime = System.currentTimeMillis();
		System.out.println("TIMING ANALYSIS>> TIME TO TRAIN: "+ (endTime-startTime) + "ms on size "+DOCTraining.size());
		System.out.println("Training CL2...");
		startTime = System.currentTimeMillis();
		cl2.train(FV.keySet(), DOCTraining.values());
		endTime = System.currentTimeMillis();
		System.out.println("TIMING ANALYSIS>> TIME TO TRAIN: "+ (endTime-startTime) + "ms on size "+DOCTraining.size());		
		
		//Testing
		for(int i = 1; i < 20; i++){
			cl1.changeK(i);
			System.out.println("Testing for k="+i);
			testKNNCL1(cl1, DOCTesting);
		}
		System.out.println("Saving CL1 parameters to file...");
		cl1.saveParamsToFile();
		System.out.println("Testing BayesianCL2:");
		testBayesianCL2(cl2, DOCTesting);
		System.out.println("Saving CL2 parameters to file...");
		cl2.saveParamsToFile();
//			}
//		}
		System.out.println("Done.");
	}
	
	private static Map<Integer, DocumentObject> parseToTrainingTesting(Map<Integer, DocumentObject> docs, int fracOfTest){
		Map<Integer, DocumentObject> DOCTesting = new TreeMap<Integer, DocumentObject>();
		Iterator<Integer> it = docs.keySet().iterator();
		int docNum, count = 0, size = docs.size()/fracOfTest;
		while(it.hasNext() && count < size){
			docNum = it.next();
			DOCTesting.put(docNum, docs.get(docNum));
			count++;
		}
		it = DOCTesting.keySet().iterator();
		while(it.hasNext()){
			docNum = it.next();
			docs.remove(docNum);
		}
		return DOCTesting;
	}

	private static void testKNNCL1(KNNCL1 cl1, Map<Integer, DocumentObject> DOCTesting){
		Iterator<Integer> it = DOCTesting.keySet().iterator();
		int docNum, error = 0, count = 0;
		String classify;
		long startTime = System.currentTimeMillis(), endTime;
		while(it.hasNext()){
			docNum = it.next();
			classify = cl1.classify(DOCTesting.get(docNum).getWords());
			count++;
//			System.out.println("Doc #" + count + " guess: " + classify + " /// Actual classification: " + DOCTesting.get(docNum).getClassifier());
			if(!classify.equals(DOCTesting.get(docNum).getClassifier())){error++;}
		}
		endTime = System.currentTimeMillis();
		System.out.println("TESTING COMPLETE: " + (count - error) + " / " + count + " Successes " + ((count-error)/(double)count)*100 + "%");
		System.out.println("TIMING ANALYSIS>> Average Time to classify:"+(endTime-startTime)/(double)DOCTesting.size());
	}

	private static void testBayesianCL2(BayesianCL2 cl2, Map<Integer, DocumentObject> DOCTesting){
		Iterator<Integer> it = DOCTesting.keySet().iterator();
		int docNum, error = 0, count = 0;
		String classify;
		long startTime = System.currentTimeMillis(), endTime;
		while(it.hasNext()){
			docNum = it.next();
			classify = cl2.classify(DOCTesting.get(docNum).getWords());
			count++;
//			System.out.println("Doc #" + count + " guess: " + classify + " /// Actual classification: " + DOCTesting.get(docNum).getClassifier());
			if(!classify.equals(DOCTesting.get(docNum).getClassifier())){error++;}
		}
		endTime = System.currentTimeMillis();
		System.out.println("TESTING COMPLETE: " + (count - error) + " / " + count + " Successes " + ((count-error)/(double)count)*100 + "%");
		System.out.println("TIMING ANALYSIS>> Average Time to classify:"+(endTime-startTime)/(double)DOCTesting.size());
	}
}
