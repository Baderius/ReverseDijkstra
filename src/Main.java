import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @author emre
 * 
 * In this solution I modeled the given pyramid as a rooted-weighed-directed graph. Then I used Dijkstra's shortest path algorithm with some changes so that 
 * it would yield the longest path instead. While doing so I've made use of a code that I'd implemented in one of my projects previously -which is also in my github account-. The numbers
 * are represented as the weights of the roads that connect the nodes. Therefore, I had to create an extra node so that I could represent the first number in the pyramid, as well. 
 * 
 * In this program I ASSUME that there IS at least ONE path to the bottom of the pyramid. In this case the program will yield the correct answer-hopefully- otherwise will yield basically
 * The INTEGER.MIN_VALUE  
 * 
 * The input should be given as an argument to the program. The output, the greatest sum if you will, will be both printed
 * to the console and to the .txt file whose path is specified with another argument. 
 * 
 * I do not claim that this is the most efficient program but it is very solid imho :)
 * 
 */
public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		

		File inFile = new File(args[0]);
		File outFile = new File(args[1]);
		
		Scanner scanner = new Scanner(inFile);
		PrintStream printOut = new PrintStream(outFile);
		
		//ArrayList to store the road lengths
		ArrayList<Integer> treeArray = new ArrayList<Integer>();
		//Our graph, which stores the nodes within
		wdg graph = new wdg();
		
		// dummy node
		treeArray.add(0);
		
		
		//Read the file
		while(scanner.hasNext()) {
			treeArray.add(scanner.nextInt());
		}
		
		Node[] nodes = new Node[treeArray.size()];
		
		//Create the nodes and add them in the array
		for(int i=0; i<treeArray.size(); i++) {
			
			
			nodes[i] = new Node(i);
			
		}
		//Add all the nodes in the graph's HashSet
		for(Node n : nodes) {
			graph.addNode(n);
		}
		
		//Connect the dummy node and the first node
		nodes[0].addRoad(nodes[1], treeArray.get(1));
		
		//Connect the nodes. Each node's out degree can be at most 2 since we move diagonally.
		//According to the index of the node we can get the id's of its children by using the indexer variable which.
		//If a value is prime simply do not add it to the graph, either of the source or of the sink node.
		for(int i=1; i<treeArray.size(); i++) {
			int indexer = (int)Math.ceil((Math.sqrt(8*i+1) -1)/2);
			
			if(!isPrime(treeArray.get(i))) {
				
				if(indexer+i<treeArray.size() && !isPrime(treeArray.get(indexer + i))) {
					nodes[i].addRoad(nodes[indexer+i], treeArray.get(indexer+i));
					
				}
				if(indexer+1+i<treeArray.size() && !isPrime(treeArray.get(indexer+1+i))) {
					nodes[i].addRoad(nodes[indexer+1+i], treeArray.get(indexer+1+i));
					
				}
			}
			
		}
		
		//Now use the ReverseDijkstra. Each node's distance to the source node(which is the dummy node) will be set.
		calculateLongestPathFromSRC(graph, nodes[0]);
		
		
		//Now we need to examine the nodes' distances that are located at the bottom of the pyramid. 
		
		// Basically the node with the greatest id
		int a = nodes[nodes.length-1].id; 

		//Given the greatest index in the pyramid (bottom-right node), it calculates the index of first element in the last row(bottom-left node)
		int startingIndex = (int)(2*a+3 - Math.sqrt(1+8*a))/2; 
		
		//Among all of the nodes, find the node whose distance is the greatest.
		int greatest = Integer.MIN_VALUE;
		for(int i=startingIndex; i<a; i++) {
			if(nodes[i].getDistanceToSource() > greatest)
				greatest = nodes[i].getDistanceToSource();
		}
		
		System.out.println(greatest);
		printOut.print(greatest);
		scanner.close();
		printOut.close();

	
	}
	
	public static boolean isPrime(int num){
	    if(num == 1)
	    	return false;
		
		if ( num > 2 && num%2 == 0 ) {
	        return false;
	    }
	    int top = (int)Math.sqrt(num) + 1;
	    for(int i = 3; i < top; i+=2){
	        if(num % i == 0){
	            return false;
	        }
	    }
	    return true; 
	}
	
	public static wdg calculateLongestPathFromSRC(wdg graph, Node srcNode) {
		PriorityQueue<Node> unprcsedNodes = new PriorityQueue<>();
		Set<Node> prcsdNodes = new HashSet<>();
		
		srcNode.setDst(0);
		unprcsedNodes.add(srcNode);
		
		while(!unprcsedNodes.isEmpty()) {
			Node currNode = unprcsedNodes.poll();
			unprcsedNodes.remove(currNode);
			
			for(java.util.Map.Entry<Node, Integer> neighNodeWeight : currNode.getNeighbouringNodes().entrySet()) {
				Node adjacentNode = neighNodeWeight.getKey();
				Integer roadLength = neighNodeWeight.getValue();
				if(!prcsdNodes.contains(adjacentNode)) {
					calculateMaxDistance(adjacentNode, roadLength, currNode);
					unprcsedNodes.add(adjacentNode);
				}
			}
			
			prcsdNodes.add(currNode);
		}
		
		
		
		
		return graph;
	}
	private static void calculateMaxDistance(Node neighNode, Integer weight, Node currNode) {
		Integer sourceDistance = currNode.getDistanceToSource();
	    if (sourceDistance + weight >= neighNode.getDistanceToSource()) {
	    	neighNode.setParent(currNode);
	    	neighNode.setDistanceToSource(sourceDistance + weight);

	        
	    	
	        

	    }
		
	}
	
	
}
class wdg {
	public HashSet<Node> nodes = new HashSet<Node>();
	
	public void addNode(Node node) {
		nodes.add(node);
	}
}

class Node implements Comparable<Node>{
	int weight;
	int id;
	int distanceToSource = 0;
	Map<Node, Integer> neighbouringNodes = new HashMap<Node, Integer>();
	public Node parent;
	
	public Node(int id) {
		this.id = id;
	}
	public Node() {
		
	}
	
	public void setDst(int dst) {
		distanceToSource = dst;
	}
	
	public Map<Node, Integer> getNeighbouringNodes() {
		return neighbouringNodes;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public int getDistanceToSource() {
		return distanceToSource;
	}
	public void setDistanceToSource(int distance) {
		this.distanceToSource = distance;
	}
	public void addRoad(Node node, int weight) {
		neighbouringNodes.put(node, weight);
	}
	public int compareTo(Node o) {
		if(this.distanceToSource > o.distanceToSource) {
			return -1;
		}else if(this.distanceToSource< o.distanceToSource) {
			return 1;
		}
		
		return 0;
	}
}