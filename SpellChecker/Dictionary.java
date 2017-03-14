//package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * The class implements the basic methods to for entry processing in dictionary
 * 
 * @author xuanlu
 *
 */
public class Dictionary {
	/**
	 * An instance of a BinarySearchTree which stores this Dictionary's list of
	 * words.
	 */
	private BinarySearchTree<Entry> bst;

	/**
	 * Constructs a new Dictionary which is empty.
	 */
	public Dictionary() {
		bst = new BinarySearchTree<Entry>();
	}

	/**
	 * Get the binarySearchTree
	 * 
	 * @return the instance of binary search tree
	 */
	public BinarySearchTree<Entry> getBST() {
		return bst;
	}

	/**
	 * Constructs a new Dictionary whose word list is exactly (a deep copy of)
	 * the list stored in the given tree. (For testing purposes, you can set
	 * this Dictionary's BST to the given BST, rather clone it, but your final
	 * method must do the deep copy)
	 * 
	 * @param tree
	 *            - The tree of the existing word list
	 */
	public Dictionary(BinarySearchTree<Entry> tree) {

		bst = new BinarySearchTree<Entry>();
		ArrayList<Entry> arr = new ArrayList<>();
		arr = tree.getInorderTravseral();
		for (int i = 0; i < arr.size(); i++) {
			bst.add(arr.get(i));
		}
		bst.setRoot(tree.getRoot());

	}

	/**
	 * Method to copy the node
	 * 
	 * @param r
	 *            the target node to copy
	 * @return the newly copy node
	 */
	private Node<Entry> copyTree(Node<Entry> r) {
		if (r == null)
			return null;
		Node<Entry> retval = new Node<Entry>(r.getData());
		retval.setLeft(copyTree(r.getLeft()));
		retval.setRight(copyTree(r.getRight()));
		return retval;
	}

	/**
	 * Constructs a new Dictionary from the file specified by the given file
	 * name. Each line of the file will contain at least one word with an
	 * optional definition. Each line will have no leading or trailing
	 * whitespace. For each line of the file, create a new Entry containing the
	 * word and definition (if given) and add it to the BST.
	 * 
	 * @param filename
	 *            - The file containing the wordlist
	 * @throws FileNotFoundException
	 *             - If the given file does not exist
	 */
	public Dictionary(String filename) throws FileNotFoundException {
		bst = new BinarySearchTree<Entry>();
		File f = new File(filename);
		Scanner scan = new Scanner(f);
		String w = "";
		String d = "";
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			Scanner in = new Scanner(line);
			in.useDelimiter(":");
			w = in.next().trim();
			if (in.hasNext()) {
				d = in.next().trim();
				Entry ent = new Entry(w, d);
				bst.add(ent);
			} else {
				Entry ent = new Entry(w);
				bst.add(ent);
			}
			in.close();
		}
		scan.close();
	}

	/**
	 * Adds a new Entry to the Dictionary for the given word with no definition.
	 * 
	 * @param word
	 *            - The word to add to the Dictionary
	 * @return true only if the Entry was successfully added to the Dictionary,
	 *         false otherwise.
	 */
	public boolean addEntry(String word) {
		if (hasWord(word))
			return false;
		Entry ent = new Entry(word);
		bst.add(ent);
		return true;
	}

	/**
	 * Adds a new Entry to the Dictionary for the given word and definition.
	 * 
	 * @param word
	 *            - The word to add to the Dictionary
	 * @param definition
	 *            - The definition of the given word
	 * @return true only if the Entry was successfully added to the Dictionary,
	 *         false otherwise.
	 */
	public boolean addEntry(String word, String definition) {
		if (hasWord(word))
			return false;
		Entry ent = new Entry(word, definition);
		bst.add(ent);
		return true;
	}

	/**
	 * Tests whether or not word exists in this Dictionary.
	 * 
	 * @param word
	 *            - The word to test.
	 * @return true is word exists in this Dictionary, false otherwise.
	 */
	public boolean hasWord(String word) {
		Entry ent = new Entry(word);
		return bst.contains(ent);
	}

	/**
	 * Find the entry according to the word
	 * 
	 * @param word
	 *            the given word
	 * @return the entry which the given word is in
	 */
	private Entry findEntry(String word) {
		Entry ent = new Entry(word);
		Entry result = bst.get(ent);
		return result;
	}

	/**
	 * Returns the definition of the given word in the Dictionary, if it is
	 * there.
	 * 
	 * @param word
	 *            - The word to retrieve the definition of.
	 * @return the definition of the word.
	 * @throws IllegalArgumentExeception
	 *             - If word does not exist in the Dictionary.
	 */
	public String getDefinitionOf(String word) throws IllegalArgumentException {
		if (!hasWord(word))
			throw new IllegalArgumentException();
		Entry ent = findEntry(word);
		return ent.def;
	}

	/**
	 * Removes the given word from the word dictionary if it is there.
	 * 
	 * @param word
	 *            - The word to remove from Dictionary.
	 * @return true only if the word is successfully removed from the
	 *         BinarySearchTree, false otherwise.
	 */
	public boolean removeEntry(String word) {
		if (!hasWord(word))
			return false;
		Entry ent = findEntry(word);
		return bst.remove(ent);
	}

	/**
	 * Changes the definition of given word if it is there.
	 * 
	 * @param word
	 *            - The word to change the definition of
	 * @param newDef
	 *            - The new definition of the word
	 * @return true if the definition was successfully updated, false otherwise.
	 */
	public boolean updateEntry(String word, String newDef) {
		if (!hasWord(word))
			return false;
		Entry ent = findEntry(word);
		ent.def = newDef;
		return true;

	}

	/**
	 * Outputs this Dictionary to the given file. The file should be formatted
	 * as follows: 1) One word and definition should appear per line separated
	 * by exactly one space. 2) Lines should not have any leading or trailing
	 * whitespace except for a single newline. 3) Each line of the file should
	 * have text. There should be no empty lines. 4) The words should be sorted
	 * alphabetically (i.e. using the BST's inorder traversal)
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void printToFile(String filename) throws FileNotFoundException {

		ArrayList<Entry> arr = new ArrayList<>();
		arr = getSortedEntries();

		File f = new File(filename);

		Scanner scan = new Scanner(f);

		PrintWriter pw = new PrintWriter(f);
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).def == null)
				pw.println(arr.get(i).word.trim());
			else
				pw.println(arr.get(i).word.trim() + " " + arr.get(i).def.trim());
		}
		pw.close();

	}

	/**
	 * Returns the number of items stored in the Dictionary.
	 */
	public int entryCount() {
		return bst.size();
	}

	/**
	 * Returns a sorted list of Entries (as returned by an inorder traversal of
	 * the BST)
	 * 
	 * @return an ArrayList of sorted Entries
	 */
	public ArrayList<Entry> getSortedEntries() {
		return bst.getInorderTravseral();
	}

	/**
	 * A Key-Value Pair class which represents an entry in a Dictionary.
	 * 
	 * @author Xuan Lu
	 */
	public static class Entry implements Comparable<Entry> {

		/**
		 * Instance variables storing the word of this Entry
		 */
		private String word;
		/**
		 * Instance variables storing definition of this Entry
		 */
		private String def;

		/**
		 * Constructs a new Entry with the given word with no definition
		 * 
		 * @param w
		 *            - The word to create an entry for.
		 */
		public Entry(String w) {
			word = w;
		}

		/**
		 * Constructs a new Entry with the given word and definition
		 * 
		 * @param w
		 *            - The word to create an entry for.
		 * @param d
		 *            - The definition of the given word.
		 */
		public Entry(String w, String d) {
			word = w;
			def = d;
		}

		/**
		 * Compares the word contained in this entry to the word in other.
		 * Returns a value < 0 if the word in this Entry is alphabetically
		 * before the other word, = 0 if the words are the same, and > 0
		 * otherwise.
		 */
		@Override
		public int compareTo(Entry other) {
			int len = Math.min(this.word.length(), other.word.length());
			for (int i = 0; i < len; i++) {
				if (this.word.charAt(i) < other.word.charAt(i))
					return -1;
				else if (this.word.charAt(i) > other.word.charAt(i))
					return 1;

			}
			if (this.word.length() < other.word.length())
				return -1;
			else if (this.word.length() > other.word.length())
				return 1;
			else
				return 0;
		}

		/**
		 * Tests for equality of this Entry with the given Object. Two entries
		 * are considered equal if the words they contain are equal regardless
		 * of their definitions.
		 */
		@Override
		public boolean equals(Object o) {
			if (o == null || o.getClass() != this.getClass())
				return false;
			Entry key = (Entry) o;
			return (this.compareTo(key) == 0);
		}
	}
}
