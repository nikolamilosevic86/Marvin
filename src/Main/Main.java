package Main;

import opennlp.tools.tokenize.Tokenizer;

public class Main {
	public static String text;
	public static Tokenizer tokenizer;
	//public static LinkedList<Word> words = new LinkedList<Word>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		text = args[0];
		System.out.println(text);
		MarvinSemAnnotator msa = new MarvinSemAnnotator();
		try {

			MarvinSemAnnotator.words = msa.annotate(text);

			for (int i = 0; i < MarvinSemAnnotator.words.size(); i++) {
				System.out.println("Word: " + MarvinSemAnnotator.words.get(i).word);
				System.out.println("Meaninigs:");
				for (int j = 0; j < MarvinSemAnnotator.words.get(i).wordmeanings.size(); j++) {
					System.out.println("Meaninig ("
							+ MarvinSemAnnotator.words.get(i).wordmeanings.get(j).Source + "): "+MarvinSemAnnotator.words.get(i).wordmeanings.get(j).id+"   "
							+ MarvinSemAnnotator.words.get(i).wordmeanings.get(j).Description);
				}
			}
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
