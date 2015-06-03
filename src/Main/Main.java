package Main;

import java.util.LinkedList;

import WordNet.Wordnet;

public class Main {
	public static String text;
	public static LinkedList<Word> words = new LinkedList<Word> ();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		text = args[0];
		System.out.println(text);
		Wordnet wn = new Wordnet();

	}

}
