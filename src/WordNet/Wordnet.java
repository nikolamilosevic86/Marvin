package WordNet;

import java.io.FileInputStream;
import java.util.LinkedList;

import Main.WordMeaningOutputElement;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;

public class Wordnet {
	private Dictionary dict = null;
	//http://wordnetweb.princeton.edu/perl/webwn?s=world
	
	public Wordnet()
	{
		String propsFile = "file_properties.xml";
		try {
			JWNL.initialize(new FileInputStream(propsFile));
			dict = Dictionary.getInstance();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public LinkedList<WordMeaningOutputElement> getSencesFromWordnet(String word,int start, int end)
	{
		
		LinkedList<WordMeaningOutputElement> sences = new LinkedList<WordMeaningOutputElement>();
		try{
		IndexWord Iword = null;
		Iword = dict.lookupIndexWord(POS.NOUN,word);
		if(Iword==null)
			return sences;
		Synset[] senses = Iword.getSenses();
		for (int i=0; i<senses.length; i++) {
			WordMeaningOutputElement o = new WordMeaningOutputElement();
			System.out.println(senses[i]);
			o.Description = senses[i].getGloss();
			o.appearingWord = word;
			o.Source = "WordNet";
			o.id = senses[i].getKey()+"";
			o.startAt = start;
			o.endAt = end;
			o.URL = "http://wordnetweb.princeton.edu/perl/webwn?s="+senses[i].getWord(0).getLemma();
			sences.add(o);
			
		}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return sences;
	}
	
	

}
