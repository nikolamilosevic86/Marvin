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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	        StringBuilder sb = new StringBuilder();
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
		// theOptions.add("-R SNOMEDCT,ICD10CM,ICD9CM,ICF,ICF-CY,RXNORM");
		for (String opt : theOptions)
			api.setOptions(opt);
	}

	public MetaMap(String host, int port) {
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader("SemanticTypes_2013AA.txt"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	        	String[] parts = line.split("|");
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
		// theOptions.add("-R SNOMEDCT,ICD10CM,ICD9CM,ICF,ICF-CY,RXNORM");
		for (String opt : theOptions)
			api.setOptions(opt);
	}

	public void getMetaMapMeanings(String term)
			throws Exception {
		// String[] classfication = new String[3];
		//Map<Object, String> mp = new HashMap<Object, String>();

		// Certain characters may cause MetaMap to throw an exception;
		// filter terms before passing to mm.
		term = term.replaceAll("'", "");
		term = term.replaceAll("\"", "");

		//System.out.println(api.getSession());

		api.setTimeout(5000);

		List<Result> resultList = api.processCitationsFromString(term);
		int i = 0;
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
							//wm.appearingWord =  mapEv.getMatchedWords().toString();
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
							for(int s= 0;s<Main.Main.words.size();s++)
							{
								if(Main.Main.words.get(s).starting<=wm.startAt && Main.Main.words.get(s).ending>=wm.endAt)
								{
									boolean containsId= false;
									for(int k = 0;k<Main.Main.words.get(s).wordmeanings.size();k++)
									{
										if(Main.Main.words.get(s).wordmeanings.get(k).id.equals(wm.id))
										{
											containsId = true;
											found = true;
										}
									}
									if(containsId)continue;
									Main.Main.words.get(s).wordmeanings.add(wm);
									found = true;
									System.out.println(mapEv.getConceptId());
									System.out.println(mapEv.getMatchedWords().toString());
									System.out.println(mapEv.getPositionalInfo().toString());
									System.out.println( mapEv.getSemanticTypes().get(0));
								}
							}
							if(!found)
							{
								Word w = new Word();
								w.starting = wm.startAt;
								w.ending = wm.endAt;
								w.word = wm.appearingWord;
								w.wordmeanings.add(wm);
								Main.Main.words.add(w);	
							}
//							mp.put(i++, mapEv.getConceptId());
//							mp.put(i++, mapEv.getMatchedWords().toString());
//							mp.put(i++, mapEv.getPositionalInfo().toString());
//							mp.put(i++, mapEv.getSemanticTypes().get(0)); 
//							mp.put(i++, mapEv.getTerm().getName());
//							mp.put(i++, mapEv.getConceptName());
//							mp.put(i++, mapEv.getMatchMap().toString());
//							mp.put(i++, mapEv.getSources().toString());
//							mp.put(i++, mapEv.getPreferredName());
						}
					}
				}
			}
		}
	//	return mp;
	}

}
