package WordNet;

import java.io.FileInputStream;
import java.util.LinkedList;

import Main.WordOutputElement;
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
	
	public LinkedList<WordOutputElement> getSencesFromWordnet(String word,int start, int end)
	{
		
		LinkedList<WordOutputElement> sences = new LinkedList<WordOutputElement>();
		try{
		IndexWord Iword = null;
		Iword = dict.lookupIndexWord(POS.NOUN,word);
		Synset[] senses = Iword.getSenses();
		for (int i=0; i<senses.length; i++) {
			WordOutputElement o = new WordOutputElement();
			System.out.println(senses[i]);
			o.Description = senses[i].getGloss();
			o.appearingWord = word;
			o.Source = "WordNet";
			o.id = senses[i].getKey()+"";
			o.startAt = start;
			o.endAt = end;
			o.URL = "http://wordnetweb.princeton.edu/perl/webwn?s="+senses[i].getWord(0).getLemma();
			
		}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return sences;
	}
	
	

}
