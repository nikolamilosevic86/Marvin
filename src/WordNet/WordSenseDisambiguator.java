package WordNet;

import java.util.LinkedList;

import Main.MarvinSemAnnotator;
import net.didion.jwnl.data.Synset;

public class WordSenseDisambiguator {
	
	
	
	public static Synset[] getRightSenses(Synset[] sences, String[] tokens,int tokenPos)
	{
		LinkedList<Synset> outputSynset = new  LinkedList<Synset>();
		LinkedList<SynsetCnt> synsets = new LinkedList<SynsetCnt>();
		float bestProp = 0;
		LinkedList<String> tokensa = new LinkedList<String>();
		for(int i = tokenPos -15;i<tokenPos+15;i++)
		{
			if(i>=0 && i<tokens.length)
			{
				tokensa.add(tokens[i]);
			}
		}
		tokens = tokensa.toArray(new String[tokensa.size()]);
		for(int i = 0;i<sences.length;i++){
			String[] defTokens = MarvinSemAnnotator.tokenizer.tokenize(sences[i].getGloss());
			int currentSameCnt = 0;
			for(String tok:defTokens)
			{
				for(String token:tokens)
				{
					if(tok.equals(token))
					{
						currentSameCnt++;
					}
				}
			}
			float prop = (float)currentSameCnt/(float)defTokens.length;
			if(prop>bestProp)
			{
				bestProp = prop;
			}
			SynsetCnt synsetcnta = new SynsetCnt();
			synsetcnta.synset = sences[i];
			synsetcnta.proportion = prop;
			synsets.add(synsetcnta);
		}
		for(int i = 0;i<synsets.size();i++)
		{
			if(synsets.get(i).proportion==bestProp)
			{
				outputSynset.add(synsets.get(i).synset);
			}
		}
		return outputSynset.toArray(new Synset[outputSynset.size()]);
	}

}
