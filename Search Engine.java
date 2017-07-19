package a4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;


/* ACADEMIC INTEGRITY STATEMENT
 * 
 * By submitting this file, we state that all group members associated
 * with the assignment understand the meaning and consequences of cheating, 
 * plagiarism and other academic offenses under the Code of Student Conduct 
 * and Disciplinary Procedures (see www.mcgill.ca/students/srr for more information).
 * 
 * By submitting this assignment, we state that the members of the group
 * associated with this assignment claim exclusive credit as the authors of the
 * content of the file (except for the solution skeleton provided).
 * 
 * In particular, this means that no part of the solution originates from:
 * - anyone not in the assignment group
 * - Internet resources of any kind.
 * 
 * This assignment is subject to inspection by plagiarism detection software.
 * 
 * Evidence of plagiarism will be forwarded to the Faculty of Science's disciplinary
 * officer.
 */



/* A Simple Search Engine exploring subnetwork of McGill University's webpages.
 * 	
 *	Complete the code provided as part of the assignment package. Fill in the \\TODO sections
 *  
 *  Do not change any of the function signatures. However, you can write additional helper functions 
 *  and test functions if you want.
 *  
 *  Do not define any new classes. Do not import any data structures. 
 *  
 *  Make sure your entire solution is in this file.
 *  
 *  We have simplified the task of exploring the network. Instead of doing the search online, we've
 *  saved the result of an hour of real-time graph traversal on the McGill network into two csv files.
 *  The first csv file "vertices.csv" contains the vertices (webpages) on the network and the second csv 
 *  file "edges.csv" contains the links between vertices. Note that the links are directed edges.
 *  
 *  An edge (v1,v2) is link from v1 to v2. It is NOT a link from v2 to v1.
 * 
 */

public class Search {

	private ArrayList<Vertex> graph;
	private ArrayList<Vertex> BFS_inspector;
	private ArrayList<Vertex> DFS_inspector;
	private Comparator<SearchResult> comparator = new WordOccurrenceComparator();
	private PriorityQueue<SearchResult> wordOccurrenceQueue;

	/**
	 * You don't have to modify the constructor. It only initializes the graph
	 * as an arraylist of Vertex objects
	 */
	public Search(){
		graph = new ArrayList<Vertex>();
	}

	/**
	 * Used to invoke the command line search interface. You only need to change
	 * the 2 filepaths and toggle between "DFS" and "BFS" implementations.
	 */
	public static void main(String[] args) {
		String pathToVertices = "/Users/philipmodupe/Downloads/Assignment4/vertices.csv";
		String pathToEdges = "/Users/philipmodupe/Downloads/Assignment4/edges.csv";

		Search mcgill_network = new Search();
		try 
		{
			mcgill_network.loadGraph(pathToVertices, pathToEdges);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scanner scan = new Scanner(System.in);
		String keyword;

		do{
			System.out.println("\nEnter a keyword to search: ");
			keyword = scan.nextLine();

			if(keyword.compareToIgnoreCase("EXIT") != 0){
				mcgill_network.search(keyword, "BFS");		//You should be able to change between "BFS" and "DFS"
				mcgill_network.displaySearchResults();
			}

		} while(keyword.compareToIgnoreCase("EXIT") != 0);

		System.out.println("\n\nExiting Search...");
		scan.close();
	}

	/**
	 * Do not change this method. You don't have to do anything here.
	 * @return
	 */
	public int getGraphSize(){
		return this.graph.size();
	}

	/**
	 * This method will either call the BFS or DFS algorithms to explore your graph and search for the
	 * keyword specified. You do not have to implement anything here. Do not change the code.
	 * @param pKeyword
	 * @param pType
	 */
	public void search(String pKeyword, String pType){
		resetVertexVisits();
		wordOccurrenceQueue = new PriorityQueue<SearchResult>(1000, comparator);
		BFS_inspector = new ArrayList<Vertex>();
		DFS_inspector = new ArrayList<Vertex>();

		if(pType.compareToIgnoreCase("BFS") == 0){
			Iterative_BFS(pKeyword);
		}
		else{
			Iterative_DFS(pKeyword);
		}
	}

	/**
	 * This method is called when a new search will be performed. It resets the visited attribute
	 * of all vertices in your graph. You do not need to do anything here.
	 */
	public void resetVertexVisits(){
		for(Vertex k : graph){
			k.resetVisited();
		}
	}

	/**
	 * Do not change the code of this method. This is used for testing purposes. It follows the 
	 * your graph search traversal track to ensure a BFS implementation is performed.
	 * @return
	 */
	public String getBFSInspector(){
		String result = "";
		for(Vertex k : BFS_inspector){
			result = result + "," + k.getURL();
		}

		return result;
	}

	/**
	 * Do not change the code of this method. This is used for testing purposes. It follows the 
	 * your graph search traversal track to ensure a DFS implementation is performed.
	 * @return
	 */
	public String getDFSInspector(){
		String result = "";
		for(Vertex k : DFS_inspector){
			result = result + "," + k.getURL();
		}
		return result;
	}

	/**
	 * This method prints the search results in order of most occurrences. It utilizes
	 * a priority queue (wordOccurrenceQueue). You do not need to change the code.
	 * @return
	 */
	public int displaySearchResults(){

		int count = 0;
		while(this.wordOccurrenceQueue.size() > 0){
			SearchResult r = this.wordOccurrenceQueue.remove();

			if(r.getOccurrence() > 0){
				System.out.println("Count: " + r.getOccurrence() + ", Page: " + r.getUrl());
				count++;
			}
		}

		if(count == 0) System.out.println("No results found for your search query");

		return count;

	}

	/**
	 * This method returns the graph instance. You do not need to change the code.
	 * @return
	 */
	public ArrayList<Vertex> getGraph(){
		return this.graph;
	}

	/**
	 * This method takes in the 2 file paths and creates your graph. Each Vertex must be 
	 * added to the graph arraylist. To implement an edge (v1, v2), add v2 to v1.neighbors list
	 * by calling v1.addNeighbor(v2)
	 * @param pVerticesPathFile
	 * @param pEdgesFilePath
	 * @throws FileNotFoundException 
	 */
	public void loadGraph(String pVerticesFilePath, String pEdgesFilePath) throws FileNotFoundException
	{

		// **** LOADING VERTICES ***///

		//TODO: Load the vertices from the pVerticesFilePath into this.graph. A Vertex needs a url and the words on the page. The 
		//		first column of the vertices.csv file contains the urls. The other columns contain the words on the pages, one word per column.
		//		Each row is 1 page.

		Scanner sc1 = new Scanner(new File(pVerticesFilePath));
		Scanner dataScanner = null;
		int i = 0;
		ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		//		System.out.println("1");

		//while the file has a line occupied with data
		while(sc1.hasNextLine())
		{
			dataScanner = new Scanner(sc1.nextLine());
			//separate the lines using a ","
			dataScanner.useDelimiter(",");
			Vertex v = null;

			//the line has a column with data
			while(dataScanner.hasNext())
			{
				String urlOrWord = dataScanner.next();

				// first column is a url
				if(i == 0)
				{
					v = new Vertex(urlOrWord);
				}

				//other columns are words
				else
				{
					v.addWord(urlOrWord);
				}

				i++;

			}

			i = 0;
			//add it to the vertexList
			vertexList.add(v);
		}
		//		System.out.println("2");
		sc1.close();

		// **** END LOADING VERTICES ***///


		// **** LOADING EDGES ***///

		//TODO: Load the edges from edges.csv. The file contains 2 columns. An edge is a link from column 1 to column 2.
		//		Each row is an edge. Read the edges.csv file line by line. For every line, find the two Vertices that 
		//		contain the urls in columns 1 and 2. Add an edge from Vertex v1 to Vertex v2 by calling v1.addNeighbor(v2);


		Scanner sc2 = new Scanner(new File(pEdgesFilePath));
		Scanner dataScanner2 = null;
		int j = 0;
		//		System.out.println("3");

		//while the there's a line occupied with data
		while(sc2.hasNextLine())
		{
			//separate the lines by using a ","
			dataScanner2 = new Scanner(sc2.nextLine());
			dataScanner2.useDelimiter(",");
			Vertex v1 = null;
			Vertex v2 = null;

			//while there's a non-empty column
			while(dataScanner2.hasNext())
			{
				//first column is a url
				String url = dataScanner2.next();
				if(j == 0)
				{
					for(int a = 0; a < vertexList.size(); a++)
					{
						//if the url matches a url in the vertexList, create a new vector using the url
						if(vertexList.get(a).getURL().equals(url))
						{
							v1 = vertexList.get(a);
						}
					}
				}
				else
				{
					for(int b = 0; b < vertexList.size(); b++)
					{
						//if the url matches a url in the vertexList, create a new vector using the url
						if(vertexList.get(b).getURL().equals(url))
						{
							v2 = vertexList.get(b);
						}
					}
				}

				j++;
			}

			//vector v1 and vector v2 are connected as neighbors
			v1.addNeighbor(v2);
			j = 0;		
		}

		//		System.out.println("4");
		sc2.close();
		graph = vertexList;

		// **** END LOADING EDGES ***///

	}


	/**
	 * This method must implement the Iterative Breadth-First Search algorithm. Refer to the lecture
	 * notes for the exact implementation. Fill in the //TODO lines
	 * @param pKeyword
	 */
	public void Iterative_BFS(String pKeyword){

		if(pKeyword.isEmpty() || pKeyword.equals(""))
		{
			System.out.println("No keyword");
			return;
		}

		//System.out.println("5");
		ArrayList<Vertex> BFSQ = new ArrayList<Vertex>();	//This is your breadth-first search queue.
		Vertex start = graph.get(0);						//We will always start with this vertex in a graph search

		start.setVisited();
		BFSQ.add(start);
		BFS_inspector.add(start);

		int hits = 0;
		//		System.out.println("5");


		for(String s1 : start.getWords())
		{
			//if the first vertex contains words that match the keyword, increment the counter
			if(s1.contains(pKeyword))
			{
				hits++;
			}
		}
		SearchResult resultOne = new SearchResult(start.getURL(), hits);
		this.wordOccurrenceQueue.add(resultOne);

		//TODO: Complete the Code. Please add the line BFS_inspector.add(vertex); immediately after any insertion to your Queue BFSQ.add(vertex); This
		//		is used for testing the validity of your code. See the above lines.
		while(!BFSQ.isEmpty())
		{
			Vertex bfs = BFSQ.remove(0);

			ArrayList<Vertex> nextElement = bfs.getNeighbors();
			for(int i = 0; i < nextElement.size(); i++)
			{
				int count = 0;
				//if vertex hasn't been visited, visit the vertex,
				//then check if the visited vertex contains any
				//words that matches the keyword, if so increment the counter
				if(!nextElement.get(i).getVisited())
				{
					nextElement.get(i).setVisited();
					for(String s2 : nextElement.get(i).getWords())
					{
						if(s2.contains(pKeyword))
						{
							count++;
						}
					}

					SearchResult result = new SearchResult(nextElement.get(i).getURL(), count);
					this.wordOccurrenceQueue.add(result);
					BFSQ.add(nextElement.get(i));
					BFS_inspector.add(nextElement.get(i));
				}
			}
			//			System.out.println("6");
		}

		//		System.out.println("7");

		//TODO: When you explore a page, count the number of occurrences of the pKeyword on that page. You can use the String.contains() method to count.
		//		Save your results into a SearchResult object "SearchResult r = new SearchResult(vertex.getURL(), occurrence);"
		//		Also, add the SearchResult into this.wordOccurrenceQueue queue.


	}

	/**
	 * This method must implement the Iterative Depth-First Search algorithm. Refer to the lecture
	 * notes for the exact implementation. Fill in the //TODO lines
	 * @param pKeyword
	 */
	public void Iterative_DFS(String pKeyword){

		if(pKeyword.isEmpty() || pKeyword.equals(""))
		{
			System.out.println("No keyword");
			return;
		}

		Stack<Vertex> DFSS = new Stack<Vertex>();	//This is your depth-first search stack.
		Vertex start = graph.get(0);				//We will always start with this vertex in a graph search

		//TODO: Complete the code. Follow the same instructions that are outlined in the Iterative_BFS() method.		

		start.setVisited();
		DFSS.push(start);
		DFS_inspector.add(start);

		int hits = 0;
		
		//if the first vertex contains words that match the keyword, increment the counter
		for(String s1 : start.getWords())
		{
			if(s1.contains(pKeyword))
			{
				hits++;
			}
		}

		SearchResult resultOne = new SearchResult(start.getURL(), hits);
		this.wordOccurrenceQueue.add(resultOne);

		while(!DFSS.isEmpty())
		{
			Vertex dfs = DFSS.pop();

			ArrayList<Vertex> nextElement = dfs.getNeighbors();
			for(int i = 0; i < nextElement.size(); i++)
			{
				//if vertex hasn't been visited, visit the vertex,
				//then check if the visited vertex contains any
				//words that matches the keyword, if so increment the counter
				if(!nextElement.get(i).getVisited())
				{
					int count = 0;
					nextElement.get(i).setVisited();
					for(String s2 : nextElement.get(i).getWords())
					{
						if(s2.contains(pKeyword))
						{
							count++;
						}
					}
					SearchResult result = new SearchResult(nextElement.get(i).getURL(), count);
					this.wordOccurrenceQueue.add(result);
					DFSS.push(nextElement.get(i));
					DFS_inspector.add(nextElement.get(i));
				}
			}
		}

	}


	/**
	 * This simple class just keeps the information about a Vertex together. 
	 * You do not need to modify this class. You only need to understand how it works.
	 */
	public class Vertex{
		private String aUrl;
		private boolean visited;
		private ArrayList<String> aWords;
		private ArrayList<Vertex> neighbors;

		public Vertex(String pUrl){
			this.aUrl = pUrl;
			this.visited = false;
			this.neighbors = new ArrayList<Vertex>();
			this.aWords = new ArrayList<String>();
		}

		public String getURL(){
			return this.aUrl;
		}

		public void setVisited(){
			this.visited = true;
		}

		public void resetVisited(){
			this.visited = false;
		}

		public boolean getVisited(){
			return this.visited;
		}

		public void addWord(String pWord){
			this.aWords.add(pWord);
		}

		public ArrayList<String> getWords(){
			return this.aWords;
		}

		public ArrayList<Vertex> getNeighbors(){
			return this.neighbors;
		}

		public void addNeighbor(Vertex pVertex){
			this.neighbors.add(pVertex);
		}

	}

	/**
	 * This simple class just keeps the information about a Search Result. It stores
	 * the occurrences of your keyword in a specific page in the graph. You do not need to modify this class. 
	 * You only need to understand how it works.
	 */
	public class SearchResult{
		private String aUrl;
		private int aWordCount;

		public SearchResult(String pUrl, int pWordCount){
			this.aUrl = pUrl;
			this.aWordCount = pWordCount;
		}

		public int getOccurrence(){
			return this.aWordCount;
		}

		public String getUrl(){
			return this.aUrl;
		}
	}

	/**
	 * This class enables us to use the PriorityQueue type. The PriorityQueue needs to know how to 
	 * prioritize its elements. This class instructs the PriorityQueue to compare the SearchResult 
	 * elements based on their word occurrence values.
	 * You do not need to modify this class. You only need to understand how it works.
	 */
	public class WordOccurrenceComparator implements Comparator<SearchResult>{
		@Override
		public int compare(SearchResult o1, SearchResult o2){
			int x = o1.getOccurrence();
			int y = o2.getOccurrence();

			if (x > y)
			{
				return -1;
			}
			if (x < y)
			{
				return 1;
			}
			return 0;
		}
	}
}
