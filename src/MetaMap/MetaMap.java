package MetaMap;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Main.MarvinSemAnnotator;
import Main.Word;
import Main.WordMeaningOutputElement;

public class MetaMap {
	private class UMLSSemanticType
	{
		public String shortName;
		public String LongName;
		public String TypeID;
	}
	
	private LinkedList<UMLSSemanticType> UMLSSemTypes = new LinkedList<MetaMap.UMLSSemanticType>();
	
	private static MetaMapApi api;

	public MetaMap() {
		
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader("SemanticTypes_2013AA.txt"));
	        String line = br.readLine();

	        while (line != null) {
	        	String[] parts = line.split("\\|");
	        	UMLSSemanticType type = new UMLSSemanticType();
	        	type.shortName = parts[0];
	        	type.TypeID = parts[1];
	        	type.LongName = parts[2];
	        	UMLSSemTypes.add(type);
	            line = br.readLine();
	        }
	        br.close();
	    }catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    }

		
		/**
		 * set parameters (set parameters in conf/metamap.properties)
		 */
		api = new MetaMapApiImpl();
		api.setHost("localhost");// "gnode1.mib.man.ac.uk"); 
		api.setPort(8066);
		List<String> theOptions = new ArrayList<String>();
		theOptions.add("-y"); // turn on Word Sense Disambiguation
		theOptions.add("-i");
		theOptions.add("-l");
		for (String opt : theOptions)
			api.setOptions(opt);
	}

	public MetaMap(String host, int port) {
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader("SemanticTypes_2013AA.txt"));
	        String line = br.readLine();

	        while (line != null) {
	        	String[] parts = line.split("\\|");
	        	UMLSSemanticType type = new UMLSSemanticType();
	        	type.shortName = parts[0];
	        	type.TypeID = parts[1];
	        	type.LongName = parts[2];
	        	UMLSSemTypes.add(type);
	            line = br.readLine();
	        }
	        br.close();
	    }catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    }
		/**
		 * set parameters (set parameters in conf/metamap.properties)
		 */
		api = new MetaMapApiImpl();
		api.setHost(host);// "gnode1.mib.man.ac.uk");
		api.setPort(port);
		List<String> theOptions = new ArrayList<String>();
		theOptions.add("-y"); // turn on Word Sense Disambiguation
		theOptions.add("-i");
		theOptions.add("-l");
		for (String opt : theOptions)
			api.setOptions(opt);
	}

	public void getMetaMapMeanings(String term)
			throws Exception {
		byte[] b = term.getBytes(StandardCharsets.US_ASCII);
		term =  new String(b);
		// Certain characters may cause MetaMap to throw an exception;
		// filter terms before passing to mm.
		term = term.replaceAll("'", "");
		term = term.replaceAll("\"", "");
		term = term.trim();
		term = term.replace("\n", "");
		//normalizing input for just string that are processable by MetaMap
		String patternString = "[a-zA-Z0-9 +-=~\\/()\\[\\]@\"\'.%£^#&\\*{};:]*";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(term);
		String sa = "";
		while (matcher.find()) {
			sa += matcher.group();
		}
		term = sa;

		api.setTimeout(5000);
		if(term.equals(""))
			return;

		List<Result> resultList = api.processCitationsFromString(term);
		for (Result result : resultList) {
			for (Utterance utterance : result.getUtteranceList()) {
				for (PCM pcm : utterance.getPCMList()) {
					for (Mapping map : pcm.getMappingList()) {
						for (Ev mapEv : map.getEvList()) {
							WordMeaningOutputElement wm = new WordMeaningOutputElement();
							wm.appearingWord = "";
							for(int k = 0;k<mapEv.getMatchedWords().size();k++)
							{
								if(k==mapEv.getMatchedWords().size()-1)
								{
									wm.appearingWord+=mapEv.getMatchedWords().get(k);
								}
								else
								{
									wm.appearingWord+=mapEv.getMatchedWords().get(k)+" ";
								}
							}
							wm.startAt = mapEv.getPositionalInfo().get(0).getX();
							wm.endAt = wm.startAt+mapEv.getPositionalInfo().get(0).getY();
							wm.Source = "MetaMap";
							wm.id = mapEv.getConceptId();
							wm.URL = "http://www.medindex.am/glossary/mesh/defini.php?action=search&type=cui&word="+mapEv.getConceptId();
							wm.Description = mapEv.getSemanticTypes().get(0);
							for(int l = 0; l<UMLSSemTypes.size();l++)
							{
								if(UMLSSemTypes.get(l).shortName.equals(wm.Description))
								{
									wm.Description = UMLSSemTypes.get(l).LongName+" ("+wm.Description+")";
									break;
								}
							}
							boolean found = false;
							for(int s= 0;s<MarvinSemAnnotator.words.size();s++)
							{
								if(MarvinSemAnnotator.words.get(s).starting<=wm.startAt && MarvinSemAnnotator.words.get(s).ending>=wm.endAt)
								{
									boolean containsId= false;
									for(int k = 0;k<MarvinSemAnnotator.words.get(s).wordmeanings.size();k++)
									{
										if(MarvinSemAnnotator.words.get(s).wordmeanings.get(k).id.equals(wm.id))
										{
											containsId = true;
											found = true;
										}
									}
									if(containsId)continue;
									MarvinSemAnnotator.words.get(s).wordmeanings.add(wm);
									found = true;
//									System.out.println(mapEv.getConceptId());
//									System.out.println(mapEv.getMatchedWords().toString());
//									System.out.println(mapEv.getPositionalInfo().toString());
//									System.out.println( mapEv.getSemanticTypes().get(0));
								}
							}
							if(!found)
							{
								Word w = new Word();
								w.starting = wm.startAt;
								w.ending = wm.endAt;
								w.word = wm.appearingWord;
								w.wordmeanings.add(wm);
								MarvinSemAnnotator.words.add(w);	
							}
						}
					}
				}
			}
		}
	}

}
