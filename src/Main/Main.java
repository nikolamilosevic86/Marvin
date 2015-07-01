package Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import DBPedia.DBPediaQuery;
import MetaMap.MetaMap;
import WordNet.Wordnet;

public class Main {
	public static String text;
	public static Tokenizer tokenizer;
	public static LinkedList<Word> words = new LinkedList<Word>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		text = args[0];
		System.out.println(text);
		Wordnet wn = new Wordnet();
		MetaMap mm= new MetaMap();
		DBPediaQuery db = new DBPediaQuery();
		InputStream is;
		try {
			InputStream modelIn = null;
			// Loading tokenizer model
			modelIn = new FileInputStream("en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);
			modelIn.close();

			POSTaggerME _posTagger = new POSTaggerME(posModel);

			is = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(is);
			tokenizer = new TokenizerME(model);
			String[] tokens = tokenizer.tokenize(text);
			Span[] tokens2 = tokenizer.tokenizePos(text);
			String[] tags = _posTagger.tag(tokens);
			for (int i = 0; i < tokens.length; i++) {
				Word w = new Word();
				w.starting = tokens2[i].getStart();
				w.ending = tokens2[i].getEnd();
				w.word = tokens[i];
				w.wordmeanings.addAll(wn.getSencesFromWordnet(w.word, tags[i],
						w.starting, w.ending));
				w.wordmeanings.addAll(db.queryDBPedia(w.word, w.starting,
						w.ending));
				if (i + 1 < tokens.length) {
					w.wordmeanings.addAll(db.queryDBPedia(w.word + " "
							+ tokens[i + 1], w.starting,
							tokens2[i + 1].getEnd()));
				}
				words.add(w);
			}
			
			mm.getMetaMapMeanings(text);
			for (int i = 0; i < words.size(); i++) {
				System.out.println("Word: " + words.get(i).word);
				System.out.println("Meaninigs:");
				for (int j = 0; j < words.get(i).wordmeanings.size(); j++) {
					System.out.println("Meaninig ("
							+ words.get(i).wordmeanings.get(j).Source + "): "
							+ words.get(i).wordmeanings.get(j).Description);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
