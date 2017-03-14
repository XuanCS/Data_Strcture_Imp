//package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import edu.iastate.cs228.hw4.Dictionary.Entry;

/**
 * The class is implemented application dictionary as test
 * 
 * @author xuanlu
 *
 */

public class SpellChecker {

	/**
	 * Displays usage information.
	 *
	 * There's no reason that you should need to modify this.
	 */
	private static void doUsage() {
		System.out.println("Usage: SpellChecker [-i] <dictionary> <document>\n"
				+ "                    -d <dictionary>\n" + "                    -h");
	}

	/**
	 * Displays detailed usage information and exits.
	 *
	 * There's no reason that you should need to modify this.
	 */
	private static void doHelp() {
		doUsage();
		System.out.println("\n" + "When passed a dictionary and a document, spell check the document.  Optionally,\n"
				+ "the switch -n toggles non-interactive mode; by default, the tool operates in\n"
				+ "interactive mode.  Interactive mode will write the corrected document to disk,\n"
				+ "backing up the uncorrected document by concatenating a tilde onto its name.\n\n"
				+ "The optional -d switch with a dictionary parameter enters dictionary edit mode.\n"
				+ "Dictionary edit mode allows the user to query and update a dictionary.  Upon\n"
				+ "completion, the updated dictionary is written to disk, while the original is\n"
				+ "backed up by concatenating a tilde onto its name.\n\n"
				+ "The switch -h displays this help and exits.");
		System.exit(0);
	}

	/**
	 * Runs the three modes of the SpellChecker based on the input arguments. DO
	 * NOT change this method in any way other than to set the name and sect
	 * variables.
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		if (args.length == 0) {
			doUsage();
			System.exit(-1);
		}

		/*
		 * In order to be considered for the competition, set these variables.
		 */
		String name = "NOT GIVEN"; // First and Last
		String sect = "NOT GIVEN"; // "A" or "B"

		Timer timer = new Timer();

		timer.start();

		if (args[0].equals("-h"))
			doHelp();
		else if (args[0].equals("-n"))
			doNonInteractiveMode(args);
		else if (args[0].equals("-d"))
			doDictionaryEditMode(args);
		else
			doInteractiveMode(args);

		timer.stop();

		System.out.println("Student name:   " + name);
		System.out.println("Student sect:   " + sect);
		System.out.println("Execution time: " + timer.runtime() + " ms");
	}

	/**
	 * Carries out the Interactive mode of the Spell Checker.
	 * 
	 * @param args
	 *            the arguments given to the main. The correct number of
	 *            arguments may or may not be contained in it. Call doUsage()
	 *            and exit if the parameter count is incorrect.
	 * @throws FileNotFoundException
	 *             When file cannot be found
	 */
	public static void doInteractiveMode(String[] args) throws FileNotFoundException {
		String dictionaryFile = args[0];
		String filename = args[1];
		// create file for original file
		File newFile = new File(dictionaryFile);
		String name = newFile.getName();
		String newname = name + "~";

		Dictionary dic = new Dictionary(dictionaryFile);

		// deep copy of binary search tree
		BinarySearchTree<Entry> oldtree = dic.getBST();
		Dictionary olddic = new Dictionary(oldtree);

		try {
			File f = new File(filename);
			Scanner in = new Scanner(f);
			// scan each line
			while (in.hasNextLine()) {
				String sentence = in.nextLine();
				String[] words = sentence.split(" ");
				// transform string[] into ArrayList
				ArrayList<String> al = new ArrayList<String>(Arrays.asList(words));// string

				int errWordNum = 1;
				System.out.println(sentence);
				while (errWordNum != 0) {
					errWordNum = 0;
					ArrayList<String> errWord = new ArrayList<String>();
					ArrayList<Integer> errInd = new ArrayList<Integer>();
					// copy the old misspelling word
					for (int i = 0; i < al.size(); i++) {
						// the case of punctuation
						if (!dic.hasWord(al.get(i).replaceAll("[^a-zA-Z ]", ""))) {
							errWordNum += 1;
							errWord.add(al.get(i));
							errInd.add(i);
							for (int j = 0; j < al.get(i).length(); j++) {
								if (j == 0) {
									System.out.print("^");
								} else
									System.out.print(" ");
							}
							if (i != al.size() - 1)
								System.out.print(" ");
							else
								System.out.println();
						} else {
							for (int j = 0; j < al.get(i).length(); j++) {
								System.out.print(" ");
							}
							if (i != al.size() - 1) {
								System.out.print(" ");
							} else {
								System.out.println();
							}
						}
					}
					// if arrayList still has misspelling words
					if (errWordNum != 0) {
						System.out.print(errWord.get(0) + ": ");
						System.out.print("[r]eplace/[a]ccept? ");
						Scanner in2 = new Scanner(System.in);
						String input2 = in2.next();
						// the case of replacement
						if (input2.equals("r")) {
							System.out.print("Replacement text: ");
							Scanner in3 = new Scanner(System.in);
							String input3 = in3.next();
							al.set(errInd.get(0), input3);
						}
						// the case of acceptance
						if (input2.equals("a")) {
							dic.addEntry(errWord.get(0));
						}

						for (int i = 0; i < al.size(); i++) {
							if (i != al.size() - 1)
								System.out.print(al.get(i) + " ");
							else
								System.out.println(al.get(i));
						}
						if (!input2.equals("r"))
							errWordNum--;
					}
				}
			}
			in.close();
			// keep the original file with ~
			olddic.printToFile(newname);
			// update the change in the new file
			dic.printToFile(dictionaryFile);
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.out.println("check the existance of filename again");
		}

	}

	/**
	 * Carries out the Non-Interactive mode of the Spell Checker.
	 * 
	 * @param args
	 *            the arguments given to the main. The correct number of
	 *            arguments may or may not be contained in it. Call doUsage()
	 *            and exit if the parameter count is incorrect.
	 * @throws FileNotFoundException
	 *             When file cannot be found
	 */
	public static void doNonInteractiveMode(String[] args) throws FileNotFoundException {
		if (args.length == 0) {
			doUsage();
			System.exit(-1);
		}

		String dictionaryFile = args[1];
		String filename = args[2];

		Dictionary dic = new Dictionary(dictionaryFile);

		try {
			File f = new File(filename);
			Scanner in = new Scanner(f);
			while (in.hasNextLine()) {
				String sentence = in.nextLine();
				String[] words = sentence.split(" ");
				// transform string[] into ArrayList
				ArrayList<String> al = new ArrayList<String>(Arrays.asList(words));
				System.out.println(sentence);
				// remove ^ when the operation is finished
				for (int i = 0; i < al.size(); i++) {
					// the case of punctuation
					if (!dic.hasWord(al.get(i).replaceAll("[^a-zA-Z ]", ""))) {
						for (int j = 0; j < al.get(i).length(); j++) {
							if (j == 0)
								System.out.printf("^");
							else
								System.out.printf(" ");
						}
						if (i != al.size() - 1)
							System.out.printf(" ");
						else
							System.out.println();
					} else {
						for (int j = 0; j < al.get(i).length(); j++) {
							System.out.printf(" ");
						}
						if (i != al.size() - 1)
							System.out.printf(" ");
						else
							System.out.println();
					}

				}

			}
			in.close();
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.out.println("check the existance of filename");
		}
	}

	/**
	 * Determine whether the input word is invalid
	 * 
	 * @param s
	 *            the input word
	 * @return true if the input word meet the requirement letters, hyphens, and
	 *         apostrophes, false otherwise
	 */
	private static boolean isValidWord(String s) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-'";
		for (int i = 0; i < s.length(); i++) {
			if (valid.indexOf(s.charAt(i)) < 0) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Carries out the Dictionary Edit mode of the Spell Checker.
	 * 
	 * @param args
	 *            the arguments given to the main. The correct number of
	 *            arguments may or may not be contained in it. Call doUsage()
	 *            and exit if the parameter count is incorrect.
	 * @throws FileNotFoundException
	 *             When file cannot be found
	 */
	public static void doDictionaryEditMode(String[] args) throws FileNotFoundException {
		if (args.length == 0) {
			doUsage();
			System.exit(-1);
		}
		String testDictionary = args[1];
		File f = new File(testDictionary);
		String name = f.getName();
		String newname = name + "~";
		File newFile = new File(newname);

		Dictionary dic;
		try {
			dic = new Dictionary(testDictionary);
			BinarySearchTree<Entry> oldtree = dic.getBST();
			Dictionary olddic = new Dictionary(oldtree);
			System.out.println("Editing dictionary.txt");
			System.out.printf("Word: ");
			Scanner in = new Scanner(System.in);
			String input = in.next();
			outloop:
			// the case to exit the program
			while (!input.equals("!quit")) {
				// check whether the input is valid
				if (!isValidWord(input)) {
					System.out.println("'" + input + "'" + " is not valid. Please enter a valid word");
				} else {
					if (dic.hasWord(input)) {
						System.out.println("'" + input + "'" + "was found");
						System.out.printf("[r]emove/[g]et the definition/[c]hange definition/do [n]othing: ");
						Scanner in2 = new Scanner(System.in);
						String input2 = in2.next();
						// the case of get definition
						if (input2.equals("g")) {
							if (dic.getDefinitionOf(input) == null)
								System.out.println("<undefined>");
							else
								System.out.println(dic.getDefinitionOf(input));
							// the case of change definition
						} else if (input2.equals("c")) {
							Scanner in3 = new Scanner(System.in);
							System.out.printf("Definition: ");
							in3.useDelimiter("\n");
							String input3 = in3.next();

							// in3.close();
							dic.updateEntry(input, input3);
							// the case of remove the word
						} else if (input2.equals("r")) {
							dic.removeEntry(input);
						} else if (input2.equals("n")) {
							// do nothing
						} else if (input2.equals("!quit"))
							break outloop;
						// if the input is not in dictionary
					} else {
						System.out.println("'" + input + "'" + "not found");
						System.out.printf("[a]dd/add with [d]efinition/[n]othing: ");
						Scanner in4 = new Scanner(System.in);
						String input4 = in4.next();
						// the case of add new word
						if (input4.equals("a"))
							dic.addEntry(input);
						// the case of get definition
						else if (input4.equals("d")) {
							System.out.printf("Definition: ");
							Scanner in5 = new Scanner(System.in);
							String input5 = in5.next();
							dic.addEntry(input4, input5);
							// the case of doing nothing
						} else if (input4.equals("n")) {
						} else if (input4.equals("!quit"))
							break outloop;
					}
				}
				System.out.printf("Word: ");
				input = in.next();
			}
			in.close();
			// update in the new File and add~ into the old File
			olddic.printToFile(newFile.getName());
			dic.printToFile(testDictionary);
			System.exit(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Timer class used for this project's competition. DO NOT modify this class
	 * in any way or you will be ineligible for Eternal Glory.
	 */
	private static class Timer {
		private long startTime;
		private long endTime;

		public void start() {
			startTime = System.nanoTime();
		}

		public void stop() {
			endTime = System.nanoTime();
		}

		public long runtime() {
			return endTime - startTime;
		}
	}
}
