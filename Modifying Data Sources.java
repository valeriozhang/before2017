package assignment1;

import java.io.IOException;
import java.util.Arrays;

import org.jsoup.Jsoup;

/* ACADEMIC INTEGRITY STATEMENT
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

/**
 * When you run the main method, the program should print the name of the professor
 * that is the best match for the keywords described in the QUERY array using two similarity
 * metrics: the Jaccard index and the relative number of keyword matches. If no result is found,
 * the program should print out: "No result found".
 *  
 *  Complete the code provided as part of the assignment package. 
 *  
 *  Complete the list of professors, but do not remove the ones who are there or their URL.
 *  
 *  You can change the content of the QUERY as you like.
 *  
 *  Do not change any of the function signatures. However, you can write additional helper functions 
 *  and test functions if you want.
 *  
 *  Do not define any new classes.
 *  
 *  Do all the work using arrays. Do not use the Collection classes (List, etc.) The goal of 
 *  this assignment is to develop proficiency in the design of algorithm and the use of basic 
 *  data structures. 
 *  
 *  It is recommended to use the Arrays.sort and Arrays.binarySeach methods.
 *  
 *  Make sure your entire solution is in this file.
 *
 */
public class Assignment1
{
	/**
	 * List of professors to search. Complete the list with all professors in the school
	 * of computer science. Choose the page that has the best description of the professor's
	 * research interests.
	 */
	private static Professor[] PROFESSORS = {
		new Professor("Yang Cai", "http://www.cs.mcgill.ca/~cai/"),
		new Professor("David Avis", "https://www.cs.mcgill.ca/people/faculty/profile?uid=avis"),
		new Professor("Mathieu Blanchette", "http://en.wikipedia.org/wiki/Mathieu_Blanchette_%28computational_biologist%29"),
		new Professor("Xiao-Wen Chang", "https://www.cs.mcgill.ca/people/faculty/profile?uid=chang"),
		new Professor("Jackie Chi Kit Cheung", "http://www.cs.toronto.edu/~jcheung/"),
		new Professor("Claude Crépeau", "https://www.cs.mcgill.ca/people/faculty/profile?uid=crepeau"),
		new Professor("Martin Robillard", "http://www.cs.mcgill.ca/~martin"),
		new Professor("Luc Devroye", "https://www.cs.mcgill.ca/people/faculty/profile?uid=luc"),
		new Professor("Nathan Friedman", "https://www.cs.mcgill.ca/people/faculty/profile?uid=nathan"),
		new Professor("Nathan Friedman", "https://www.cs.mcgill.ca/people/faculty/profile?uid=nathan"),
		new Professor("Mike Hallett", "https://www.cs.mcgill.ca/people/faculty/profile?uid=hallett"),
		new Professor("Hamed Hatami", "https://www.cs.mcgill.ca/people/faculty/profile?uid=hatami"),
		new Professor("Wenbo He", "http://www.cs.mcgill.ca/~wenbohe/"),
		new Professor("Gregory Dudek", "http://www.cim.mcgill.ca/~dudek/dudek_bio.html"),
		new Professor("Laurie Hendren", "https://www.cs.mcgill.ca/people/faculty/profile?uid=hendren"),
		new Professor("Bettina Kemme", "https://www.cs.mcgill.ca/people/faculty/profile?uid=kemme"),
		new Professor("Jörg Kienzle", "https://www.cs.mcgill.ca/people/faculty/profile?uid=joerg"),
		new Professor("Brigitte Pientka", "https://www.cs.mcgill.ca/people/faculty/profile?uid=bp"),
		new Professor("Paul Kry", "http://www.cs.mcgill.ca/~kry/"),
		new Professor("Michael Langer", "https://www.cs.mcgill.ca/people/faculty/profile?uid=langer"),
		new Professor("Xue Liu", "https://www.cs.mcgill.ca/people/faculty/profile?uid=xueliu"),
		new Professor("Muthucumaru Maheswaran", "http://www.cs.mcgill.ca/~maheswar/"),
		new Professor("Prakash Panangaden", "https://www.cs.mcgill.ca/people/faculty/profile?uid=prakash"),
		new Professor("Joelle Pineau", "https://www.cs.mcgill.ca/people/faculty/profile?uid=jpineau"),
		new Professor("Doina Precup", "https://www.cs.mcgill.ca/people/faculty/profile?uid=dprecup"),
		new Professor("Bruce Reed", "https://www.cs.mcgill.ca/people/faculty/profile?uid=breed"),
		new Professor("Derek Ruths", "http://www.derekruths.com/research/"),
		new Professor("Kaleem Siddiqi", "https://www.cs.mcgill.ca/people/faculty/profile?uid=siddiqi"),
		new Professor("Denis Therien", "https://www.cs.mcgill.ca/people/faculty/profile?uid=denis"),
		new Professor("Carl Tropper", "https://www.cs.mcgill.ca/people/faculty/profile?uid=carl"),
		new Professor("Hans Vangheluwe", "https://www.cs.mcgill.ca/people/faculty/profile?uid=hv"),
		new Professor("Clark Verbrugge", "https://www.cs.mcgill.ca/people/faculty/profile?uid=clump"),
		new Professor("Adrian Vetta", "http://www.math.mcgill.ca/~vetta/"),
		new Professor("Jerome Waldispuhl", "http://www.cs.mcgill.ca/~jeromew/"),
		new Professor("Mohit Singh", "https://www.cs.mcgill.ca/people/faculty/profile?uid=mohit"),

	};

	/**
	 * A set of keywords describing an area of interest. Does not have to be sorted, 
	 * but must not contain any duplicates.
	 */
	private static String[] QUERY = {"programming", "engineering", "design"};

	/**
	 * Words with low information content that we want to exclude from the similarity
	 * measure.
	 * 
	 * This array should always be sorted. Don't change anything for the assignment submissions,
	 * but afterwards if you want to keep playing with this code, there are some other words
	 * that would obviously be worth adding.
	 */
	private static String[] STOP_WORDS = {"a", "an", "and", "at", "by", "for", "from", "he", "his", 
		"in", "is", "it", "of", "on", "she", "the", "this", "to", "with" };

	/**
	 * Your program starts here. You should not need to do anything here besides
	 * removing the first two statements once you have copied the required statement
	 * and dealing with the case where there are no results.
	 */
	public static void main(String[] args) throws IOException
	{
		Arrays.sort(QUERY);
		String[] queryWithoutStopWords = removeStopWords(QUERY);

		//if bestMatchJaccard and bestMatchRelHits returns a value, print out the results of both methods
		if(!bestMatchJaccard(queryWithoutStopWords).equals("") && !bestMatchRelHits(queryWithoutStopWords).equals(""))
		{
			System.out.println("Jaccard: " + bestMatchJaccard(queryWithoutStopWords));
			System.out.println("Relhits: " + bestMatchRelHits(queryWithoutStopWords));
		}
		//if bestMatchJaccard and bestMatchRelHits are both empty, print out the message below
		else if(bestMatchJaccard(queryWithoutStopWords).equals("") && bestMatchRelHits(queryWithoutStopWords).equals(""))  
		{
			System.out.println("no result found");
		}
		//if ONLY bestMatchJaccard returns a value, print out the results of the method
		else if(!bestMatchJaccard(queryWithoutStopWords).equals("") && bestMatchRelHits(queryWithoutStopWords).equals(""))
		{
			System.out.println(bestMatchJaccard(queryWithoutStopWords));
		}
		//if ONLY bestMatchRelHits returns a value, print out the the results of the method
		else if(bestMatchJaccard(queryWithoutStopWords).equals("") && !bestMatchRelHits(queryWithoutStopWords).equals(""))
		{
			System.out.println(bestMatchRelHits(queryWithoutStopWords));
		}

	}

	/**
	 * Returns the name of the professor that is the best match according to
	 * the Jaccard similarity index, or the empty String if there are no such
	 * professors. pQuery must not include duplicate or stop
	 * words, and must be sorted before being passed into this function.
	 */
	public static String bestMatchJaccard(String[] pQuery) throws IOException
	{
		assert pQuery != null;

		//this sorts the pQuery
		Arrays.sort(pQuery); 
		//get the full name of the first professor in the document
		String firstProf = PROFESSORS[0].getName();

		//the intersection size of the first professor in the document
		double firstIntersection = intersectionSize(removeStopWords(obtainWordsFromPage(PROFESSORS[0].getWebPageUrl())), pQuery);
		//the union size of the first professor in the document
		double firstUnion = unionSize(removeStopWords(obtainWordsFromPage(PROFESSORS[0].getWebPageUrl())), pQuery);
		//this is the jaccard similarity index of the first professor, which is intersectionSize/unionSize
		double firstSimilarity = firstIntersection / firstUnion;

		//counter will start from 1 as we have already accounted for the professor at position 0
		int i = 1;

		//iterate through the list of professors, professors.length-1 times
		while(i < PROFESSORS.length)
		{
			//the next prof that will be comapred to the prof at position 0 will be the prof at position i
			String nextProf = PROFESSORS[i].getName();
			//the intersection size of the next professor in the document
			double nextIntersection = intersectionSize(removeStopWords(obtainWordsFromPage(PROFESSORS[i].getWebPageUrl())), pQuery);
			//the union size of the first professor in the document
			double nextUnion = unionSize(removeStopWords(obtainWordsFromPage(PROFESSORS[i].getWebPageUrl())), pQuery);
			//this is the jaccard similarity index of the next professor, which is intersectionSize/unionSize
			double nextSimilarity = nextIntersection / nextUnion;
			//if the similarity index of the next prof is greater than the first prof, then the nextProf is the firstProf
			//and the similairty index of the next prof will become the similarity index of the first prof
			if(nextSimilarity > firstSimilarity)
			{
				firstProf = nextProf;
				firstSimilarity = nextSimilarity;
			}
			//if the similarity index of the next prof equals that of the previous prof, then the first prof will be the prof
			//at position 0 + the prof at position i
			else if (nextSimilarity == firstSimilarity)
			{
				firstProf = firstProf + "," + nextProf;
			}
			i++;
		}
		return firstProf;
	}

	/**
	 * Returns the size of the intersection between pDocument and pQuery.
	 * pDocument can contain duplicates, pQuery cannot. Both arrays must 
	 * be sorted in alphabetical order before being passed into this function.
	 */
	public static int intersectionSize(String[] pDocument, String[] pQuery)
	{
		assert pDocument != null && pQuery != null;

		//sort both the document and query arrays
		Arrays.sort(pDocument);
		Arrays.sort(pQuery);

		int intersection = 0;
		//since pQuery can contain no duplicates, it's the reference point of comparison
		for(int i = 0; i < pQuery.length; i++)
		{
			//iterate through pDocument and check if any of the strings at position j equal the string at position i
			//of pQuery. If they equal, increment the intersection counter by one and break out of this for-loop.
			for(int j = 0; j < pDocument.length; j++)
			{
				if (pQuery[i].equals(pDocument[j]))
				{
					intersection++;
					break;
				}
			}
		}
		//this will return the number of strings both pQuery and pDocument share
		return intersection;

	}

	/**
	 * Returns the name of the professor that is the best match according to
	 * the RelHits (relative hits) similarity index, computed as numberOfHits/size of the document.
	 * Returns the empty string if no professor is found.
	 * pQuery must not include duplicate or stop words, and must be sorted before
	 * being passed into this function.
	 */
	public static String bestMatchRelHits(String[] pQuery) throws IOException
	{
		assert pQuery != null;

		//sort the pQuery array
		Arrays.sort(pQuery);
		
		//get the full name of the first professor in the document
		String firstProf = PROFESSORS[0].getName();

		//the number of hits from the page of the first professor with the array of strings pQuery
		double firstHits = numberOfHits(removeStopWords(obtainWordsFromPage(PROFESSORS[0].getWebPageUrl())), pQuery);
		
		//the relative hits calculated as the numberOfHits/size of the document
		double firstHitsSize = firstHits / (removeStopWords(obtainWordsFromPage(PROFESSORS[0].getWebPageUrl()))).length;

		//start counting from 1 as we have accounted for the first professor already
		int i = 1;

		while(i < PROFESSORS.length)
		{
			String temp1 = "";
			String temp2 = "";
			//get the name of the prfessor at position i
			String nextProf = PROFESSORS[i].getName();
			//the number of hits from the page of the professor at position i with the array of strings pQuery
			double nextHits = numberOfHits(removeStopWords(obtainWordsFromPage(PROFESSORS[i].getWebPageUrl())), pQuery);
			//the relative hits calculated as the numberOfHits/size of the document
			double nextHitsSize = nextHits / (removeStopWords(obtainWordsFromPage(PROFESSORS[i].getWebPageUrl()))).length;
			//if the relative hits of the prof at position i is greater than that of the prof at position 0
			//make the nextProf the firstProf and make the relative hits of the firstProf the relativehits of the prof
			//at position 0
			if(nextHitsSize > firstHitsSize)
			{
				firstProf = nextProf;
				firstHitsSize = nextHitsSize;
			}
			//if the relative hits of the prof at position i equals that of the prof at position 0, the firstProf will be the 
			//prof at position 0 + the prof at the ith position
			else if (nextHitsSize == firstHitsSize)
			{
				firstProf = firstProf + "," + nextProf;
			}
			i++;
		}

		return firstProf;
	}

	/**
	 * Returns the Jaccard similarityIndex between pDocument and pQuery,
	 * that is, |intersection(pDocument,pQuery)|/|union(pDocument,pQuery)|
	 */
	public static double jaccardIndex(String[] pDocument, String[] pQuery)
	{
		assert pDocument != null && pQuery != null;
		//absolute value of the intersection size b/w pDocument and pQuery / absolute value of the intersection size
		//b/w pDocument and pQuery
		return (double)((Math.abs(intersectionSize(pDocument,pQuery)))/(Math.abs(unionSize(pDocument,pQuery))));
		// TODO
	}

	/**
	 * Returns the size of the union between pDocument and pQuery. 
	 * pDocument can contain duplicates, pQuery cannot. Both arrays must 
	 * be sorted in alphabetical order before being passed into this 
	 * function.
	 */
	public static int unionSize(String[] pDocument, String[] pQuery)
	{
		assert pDocument != null && pQuery != null;
		
		//sort both pDocument and pQuery
		Arrays.sort(pDocument);
		Arrays.sort(pQuery);
		//counter is the length of pDocument + pQuery
		int counter = pDocument.length + pQuery.length;

		//the strings of pDocument will be the reference point of comparison since they
		//can contain duplicates
		for(int j = 0; j < pDocument.length; j++)
		{
			//if the string at position j of pDocument equals the string at position i of pQuery
			//decrement the counter by a value of one
			for(int i = 0; i < pQuery.length; i++)
			{
				if(pDocument[j].equals(pQuery[i]))
					counter--;
			}
		}
		//this will return the number of unique strings between both arrays and will have no repetitions
		return counter;
	}

	/**
	 * Returns the number of times that any word in pQuery is found in pDocument
	 * for any word, and including repetitions. For example, if pQuery contains 
	 * "design" and "design" is found 3 times in pDocument, this would return 3.
	 * Both pDocument and pQuery should be sorted in alphabetical order before 
	 * being passed into this function.
	 */
	public static int numberOfHits(String[] pDocument, String[] pQuery)
	{
		assert pDocument != null && pQuery != null;
		
		//sort both pDocument and pQuery
		Arrays.sort(pDocument);
		Arrays.sort(pQuery);
		//counter will start at 0
		int counter = 0;
		//the strings of pDocument will be the reference point of comparison since they
		//can contain duplicates
		for(int i = 0; i < pDocument.length; i++)
		{
			//if the string at position j of pDocument equals the string at position i of pQuery
			//increment the counter by a value of one
			for(int j = 0; j < pQuery.length; j++)
			{
				if(pDocument[i].equals(pQuery[j]))
				{
					counter++;
				}
			}
		}
		//this will return the number of times a word in pQuery appears in pDocument
		return counter;
	}

	/**
	 * Returns a new array of words that contains all the words in pKeyWords
	 * that are not in the array of stop words. The order of the original 
	 * array should not be modified except by removing words. If the array is sorted,
	 * the resulting array should also be sorted.
	 * @param pKeyWords The array to trim from stop words
	 * @return A new array without the stop words.
	 */
	public static String[] removeStopWords(String[] pKeyWords)
	{
		assert pKeyWords != null;
		//start counter at 0
		int counter = 0;
		//pKeyWoords will be used as the reference point for comparison as it is the array to trim from
		//stop words
		for(int j = 0; j < pKeyWords.length; j++)
		{
			for(int i = 0; i<STOP_WORDS.length; i++)
			{
				//if the string of stop words at pos i equals that of pKeyWords at pos j, then
				//set the jth position of pKeyWords to null
				//increment the counter by a value of 1 and break from the for-loop
				if (STOP_WORDS[i].equals(pKeyWords[j]))
				{
					pKeyWords[j] = null;
					counter++;
					break;
				}
			}
		}
		//make a new space that is formed from the length of pKeyWords - the value
		//of the counter and use it to create the resulting array called finalArray
		int newSpace = pKeyWords.length - counter;
		String[] finalArray = new String[newSpace];
		int pos = 0;
		//pKeyWoords will be used as the reference point for comparison as it is the array to trim from
		//stop words
		for (int k = 0; k < pKeyWords.length; k++)
		{
			//if there's a string at position k of pKeyWords, put that string in the first position
			//of the finalArray
			if (pKeyWords[k]!= null)
			{
				finalArray[pos] = pKeyWords[k];
				pos++;
			}
		}
		//return the finalArray without stop words
		return finalArray;
	}

	/**
	 * Obtains all the words in a page (including duplicates), but excluding punctuation and
	 * extraneous whitespaces and tabs. The results should be sorted in alphabetical order
	 * and all be completely in lower case.
	 * Consider using String.replaceAll(...) to complete this method.
	 * @throws IOException if we can't download the page (e.g., you're off-line)
	 */
	public static String[] obtainWordsFromPage(String pUrl) throws IOException
	{
		// The statement below connects to the webpage, parses it,
		// and return the text content into inputString. Consider
		// that this is all the text of the webpage that you need.
		String inputString = Jsoup.connect(pUrl).get().text();
		// TODO Remove punctuation
		inputString.replaceAll("\\p{P}", "");
		// TODO Aggregate multiple whitespaces into one
		inputString.replaceAll("\\s+", "");
		// TODO Turn into lower case
		inputString.toLowerCase();
		// TODO Split into different words.
		String[] newString;
		newString = inputString.split(" ");
		// TODO Sort in alphabetical order
		Arrays.sort(newString);
		return newString;
	}
}

/**
 * This simple class just keeps the information about
 * a professor together. Do not modify this class.
 */
class Professor
{
	private String aName;
	private String aWebPageUrl; 

	public Professor(String pName, String pWebpageUrl)
	{
		aName = pName;
		aWebPageUrl = pWebpageUrl;
	}

	public String getName()
	{
		return aName;
	}

	public String getWebPageUrl()
	{
		return aWebPageUrl;
	}
}
