/* 
 Please see submission instructions for what to write here. 
 */
//`Group members`: **Huy Tran & Eric Tran** <br/>
//`Group honor hode`: “All group members were present and contributing during all work on this project” <br/>
//`Middlebury honor hode`: “I have neither given nor received unauthorized aid on this assignment”

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SentAnalysis {

	final static File TRAINFOLDER = new File("train");
	// initializes the positive and negative dictionaries
	static HashMap<String, Integer> PosDictionary = new HashMap<String, Integer>();
	static HashMap<String, Integer> NegDictionary = new HashMap<String, Integer>();
	static double numPosReviews = 0;
	static double numNegReviews = 0;
    static boolean running = true;
		
	public static void main(String[] args) throws IOException
	{	
		ArrayList<String> files = readFiles(TRAINFOLDER);		
		
		train(files);
		/// If command line argument is "evaluate", runs evaluation mode
		if (args.length == 1 && args[0].equals("evaluate")) {
	 	 	evaluate();
		 }
		 else {//otherwise, runs interactive mode
            while(running) {
		 	    @SuppressWarnings("resource")
                Scanner scan = new Scanner(System.in);
                System.out.print("Text to classify>> ");
                String textToClassify = scan.nextLine();
                System.out.println("Result: "+classify(textToClassify));
            }
		 }
		
	}
	
	/**
	 * Takes as parameter the name of a folder and returns a list of filenames (Strings) 
	 * in the folder.
	 * @param folder - The folder to read the files from
	 * @return An Arraylist of Strings of the names of the files in the folder
	 */
	public static ArrayList<String> readFiles(File folder) {
		System.out.println("Populating list of files");
		
		//List to store filenames in folder
		ArrayList<String> filelist = new ArrayList<String>();
		
		for (File fileEntry : folder.listFiles()) {
	        String filename = fileEntry.getName();
	        filelist.add(filename);
		}
	    
		/*
		for (String fileEntry : filelist) {
	        System.out.println(fileEntry);
		}
		
		System.out.println(filelist.size());
		*/
		
		
		return filelist;
	}
	
	/**
	 * Trainer: Reads text from data files in folder datafolder and stores counts 
	 * to be used to compute probabilities for the Bayesian formula.
	 * @param files - An Arraylist of the names of the files to train on 
	 */
	public static void train(ArrayList<String> files) throws FileNotFoundException {
		// Goes through each file in the folder
		for (String filename : files) {
			// Splits the filename at each hyphen to create an array of its info
			String[] info = filename.split("-");
			// Saves the file's label
			String label = info[1];
			// Makes the current file a File object
			File file = new File("train/" + filename);
			// Creates a scanner for the File object
			Scanner scan = new Scanner(file);
			// Splits the String at each space to create an array of the words in the file
			StringBuilder contentBuilder = new StringBuilder();
			while (scan.hasNextLine()) {
				contentBuilder.append(scan.nextLine());
			}
			String fileContent = contentBuilder.toString();
			String lower = fileContent.toLowerCase();
			String[] words = lower.split(" ");

			// Loops through the array of words
			for (int i = 0; i < words.length; i++) {
				// Removes any punctuation in the word
				words[i] = words[i].replaceAll("[\\p{Punct}\\d]", "");
				// If the label of the file is positive
				if (label.equals("5")) {
					// If the word is contained in the positive dictionary, update its value (occurrence)
					if (PosDictionary.containsKey(words[i])) {
						int value = PosDictionary.get(words[i]);
						PosDictionary.replace(words[i], value + 1);
					}
					// If the word is not contained in the positive dictionary, add it
					else {
						PosDictionary.put(words[i], 1);
					}
				}
				// If the label of the file is negative
				else if (label.equals("1")) {
					// If the word is contained in the negative dictionary, update its value (occurrence)
					if (NegDictionary.containsKey(words[i])) {
						int value = NegDictionary.get(words[i]);
						NegDictionary.replace(words[i], value + 1);
					}
					// If the word is not contained in the negative dictionary, add it
					else {
						NegDictionary.put(words[i], 1);
					}
				}
			}
			scan.close();
		}
	}


	/**
	 * Classifier: Classifies the input text (type: String) as positive or negative
	 * @param text - The text to be classified
	 * @return The label of the text 
	 */
	public static String classify(String text) {
		// The resulting label of the classification
		String result= "";
		// The probabilities
		double probPos = 0.0;
		double probNeg = 0.0;
		HashMap<String, Integer> uniqueWord = new HashMap<String, Integer>();
		// Separates each word in the text
		String lower = text.toLowerCase();
		String[] words = lower.split(" ");
		for(String word: words){
			uniqueWord.put(word, null);
		}
		double lambda = 0.0001;
		double n = uniqueWord.size();

		double smoothingFactor = 0.0001; // Adjust the smoothing factor as needed

		// Loop through each word
		for (String currentWord: words) {
			// If a dictionary contains the key, update its value
			if (PosDictionary.containsKey(currentWord))
				probPos += Math.log((double) (PosDictionary.get(currentWord) + lambda) / (PosDictionary.size() + n * lambda));
			if (NegDictionary.containsKey(currentWord))
				probNeg += Math.log((double) (NegDictionary.get(currentWord) + lambda) / (NegDictionary.size() + n * lambda));
			if (!PosDictionary.containsKey(currentWord)) {
				probPos += Math.log((double)smoothingFactor / (PosDictionary.size() + n * smoothingFactor));
			}
			if (!NegDictionary.containsKey(currentWord)) {
				probNeg += Math.log((double) smoothingFactor / (NegDictionary.size() + n * smoothingFactor));
			}
		}
	
        if (numPosReviews + numNegReviews > 0) {
            probPos += log2((double) numPosReviews / (numPosReviews + numNegReviews));
            probNeg += log2((double) numNegReviews / (numPosReviews + numNegReviews));
        }
    
		// Assign the label that is more likely
        // System.out.print("ProbPos: " +probPos);
        // System.out.print("ProbNeg: " +probNeg);
		if (probPos > probNeg) {
			result = "positive";
		}
		else {
			result = "negative";
		}
		return result;
	}

	/**	
	 * Computes log_2(d)
	 * @param d - value
	 * @return log_2(d)
	 */
	public static double log2(double d) {
		return Math.log(d)/Math.log(2);
	}
	

	/**
	 * Classifier: Classifies all of the files in the input folder (type: File) as positive or negative
	 */
	public static void evaluate() throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);

		// Opens the folder taken by user input
		System.out.print("Enter folder name of files to classify: ");
		String foldername = scan.nextLine();
		File folder = new File(foldername);

		// An ArrayList of strings of the names of the files in the folder
		ArrayList<String> filesToClassify = readFiles(folder);
		
		// The number of reviews correctly estimated
		int numCorrectPos = 0;
		int numCorrectNeg = 0;

		// Goes through each file in the folder
		for (String filename : filesToClassify) {
			// Splits the filename at each hyphen to create an array of its info
			String[] info = filename.split("-");
			// Saves the file's label
			String correctLabel = info[1];
			
			if (correctLabel.equals("5"))
				numPosReviews++;
			else if (correctLabel.equals("1"))
				numNegReviews++;
		}

		// Goes through each file in the folder
		for (String filename : filesToClassify) {
			// Splits the filename at each hyphen to create an array of its info
			String[] info = filename.split("-");
			// Saves the file's label
			String correctLabel = "";
			if (info[1].equals("5"))
				correctLabel = "positive";
			else if (info[1].equals("1")) {
				correctLabel = "negative";
			}

			// Makes the current file a File object
			File file = new File(foldername + "/" + filename);
			// Creates a scanner for the File object
			Scanner scanFile = new Scanner(file);
			// Creates a String of the content in the file
			String fileContent = scanFile.nextLine(); // notice that this only takes the first line from the file
			// Removes any punctuation in the word
			String text = fileContent.replaceAll("\\p{Punct}", ""); 
			String label = classify(text);
			// Check whether the label was correct
			if (label.equals(correctLabel)) {
				if (label.equals("positive"))
					numCorrectPos++;
				else if (label.equals("negative")) {
					numCorrectNeg++;
				}
			}
			scanFile.close();
		}
		
		double totalReviews = numPosReviews + numNegReviews;
		double numCorrectReviews = numCorrectPos + numCorrectNeg;
		double percentCorrectReviews = (numCorrectReviews / totalReviews) * 100;
		double percentCorrectPos = (numCorrectPos / numPosReviews) * 100;
		double percentCorrectNeg = (numCorrectNeg / numNegReviews) * 100;
		
		System.out.println("Accuracy");
		System.out.println("\t" + "Number of correctly classified reviews / Total number of reviews:  " + numCorrectReviews + " / " + totalReviews + " = " + percentCorrectReviews + "%");
		System.out.println("Precision");
		System.out.println("\t" + "Number correctly classified as positive / Total classified as positive:  " + numCorrectPos + " / " + numPosReviews + " = " + percentCorrectPos + "%");
		System.out.println("\t" + "Number correctly classified as negative / Total classified as negative:  " + numCorrectNeg + " / " + numNegReviews + " = " + percentCorrectNeg + "%");
	}
}