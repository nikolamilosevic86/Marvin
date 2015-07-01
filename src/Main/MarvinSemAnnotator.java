package Main;

import java.io.FileInputStream;
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

/**
 * The Class Annotator. For use as a library use its method annotate(string)
 */
public class MarvinSemAnnotator {
	
	/** The tokenizer. */
	private Tokenizer tokenizer;
	
	/** The words. */
	public LinkedList<Word> words;
	
	/** The wn. */
	Wordnet wn = new Wordnet();
	
	/** The db. */
	DBPediaQuery db = new DBPediaQuery();
	
	/** The is. */
	InputStream is;
	
	/** The _pos tagger. */
	POSTaggerME _posTagger = null;
	MetaMap mms;
	
	/**
	 * Instantiates a new annotator.
	 */
	public MarvinSemAnnotator()
	{
		try {
			InputStream modelIn = null;
			// Loading tokenizer model
			modelIn = new FileInputStream("en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);
			modelIn.close();
			_posTagger = new POSTaggerME(posModel);
			is = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(is);
			tokenizer = new TokenizerME(model);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new annotator.
	 */
	public MarvinSemAnnotator(String mm)
	{
		try {
			InputStream modelIn = null;
			// Loading tokenizer model
			modelIn = new FileInputStream("en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);
			modelIn.close();
			_posTagger = new POSTaggerME(posModel);
			is = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(is);
			tokenizer = new TokenizerME(model);
			if(mm=="mm")
			{
				mms = new MetaMap();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Annotate.
	 *
	 * @param text the text
	 * @return the linked list of word objects
	 */
	public LinkedList<Word> annotate(String text)
	{
		try{
			words = new LinkedList<Word>();
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
				
				//TODO: ADD DBPedia as local instance
				w.wordmeanings.addAll(db.queryDBPedia(w.word, w.starting,
						w.ending));
				if (i + 1 < tokens.length) {
					w.wordmeanings.addAll(db.queryDBPedia(w.word + " "
							+ tokens[i + 1], w.starting,
							tokens2[i + 1].getEnd()));
				}
				words.add(w);
			}
			mms.getMetaMapMeanings(text);
			
			return words;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	public LinkedList<Word> annotateWordNetOnly(String text)
	{
		try{
			words = new LinkedList<Word>();
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
				
				//TODO: ADD DBPedia as local instance
//				w.wordmeanings.addAll(db.queryDBPedia(w.word, w.starting,
//						w.ending));
//				if (i + 1 < tokens.length) {
//					w.wordmeanings.addAll(db.queryDBPedia(w.word + " "
//							+ tokens[i + 1], w.starting,
//							tokens2[i + 1].getEnd()));
//				}
				words.add(w);
			}
			return words;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	public LinkedList<Word> annotateMetaMapOnly(String text)
	{
		try{
			words = new LinkedList<Word>();
			mms.getMetaMapMeanings(text);
			return words;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	
	public LinkedList<Word> annotateDBPediaOnly(String text)
	{
		try{
			words = new LinkedList<Word>();
			String[] tokens = tokenizer.tokenize(text);
			Span[] tokens2 = tokenizer.tokenizePos(text);
			String[] tags = _posTagger.tag(tokens);
			for (int i = 0; i < tokens.length; i++) {
				Word w = new Word();
				w.starting = tokens2[i].getStart();
				w.ending = tokens2[i].getEnd();
				w.word = tokens[i];
				
				//TODO: ADD DBPedia as local instance
				w.wordmeanings.addAll(db.queryDBPedia(w.word, w.starting,
						w.ending));
				if (i + 1 < tokens.length) {
					w.wordmeanings.addAll(db.queryDBPedia(w.word + " "
							+ tokens[i + 1], w.starting,
							tokens2[i + 1].getEnd()));
				}
				words.add(w);
			}
			return words;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}

}
