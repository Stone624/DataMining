import java.util.ArrayList;
import java.util.Random;


public class Cluster {
	private ArrayList<ArrayList<Integer>> points;
	private ArrayList<Double> center;
	private ArrayList<Integer> map;
	
	public Cluster(int dimensions){
		points = new ArrayList<ArrayList<Integer>>();
		center = generateRandomCenter(dimensions);
		map = new ArrayList<Integer>();
	}
	
	private static ArrayList<Double> generateRandomCenter(int dimensions){//,double minValue, double maxValue){
		ArrayList<Double> center = new ArrayList<Double>();
		Random r = new Random();
		double k;
		for(int i = 0; i < dimensions; i++){
			if(r.nextDouble() > 0.9){k=1.0;}else{k=0.0;}
			center.add(k);//r.nextInt(2) + 0.0);
		}
		return center;
	}
	
	public void add(ArrayList<Integer> k, int docID){
		points.add(k);
		map.add(docID);
	}
	
	public void reCalculateCenter(){
		double sum = 0.0;
		int length = this.points.size();
		for(int i = 0; i < length; i++){
			sum = 0.0;
			for(int j = 0; j < this.points.get(i).size(); j++){
				sum += this.points.get(i).get(j);
			}
			this.center.add(sum/length);
		}
	}
	
	public ArrayList<Double> getCenter(){return this.center;}
	
	public void remove(int documentID){
		int index;
		if(this.map.contains(documentID)){
			index = this.map.indexOf(documentID);
			this.points.remove(index);
			this.map.remove(index);
		}
	}
	
	public int size(){return this.map.size();}
	
	public ArrayList<Integer> getMapOfDocuments(){return this.map;}
}
