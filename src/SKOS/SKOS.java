package SKOS;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSLiteral;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;

import Main.MarvinSemAnnotator;
import Main.Word;
import Main.WordMeaningOutputElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

public class SKOS {
	//public static LinkedList<SKOSSingleConcept> concepts = new LinkedList<SKOSSingleConcept>();
	public static HashMap<String, SKOSSingleConcept> UriMap = new HashMap<String, SKOSSingleConcept>(); 
	//Using GUAVA Multimap for maps with multiple values https://github.com/google/guava, https://code.google.com/p/guava-libraries/wiki/Release18,
	//http://tomjefferys.blogspot.com.tr/2011/09/multimaps-google-guava.html
	public static Multimap<String,SKOSSingleConcept> termMap = ArrayListMultimap.create();
	public SKOS(String FilePath,String Version)
	{
		Model m = FileManager.get().loadModel( FilePath );
		List<Statement> statements = m.listStatements().toList();
		  /*
	        * How to load a SKOS vocabulay and print out the concepts and any assertions on these
	        * The main object in the API include a SKOSManager which manages the loading, saving a dn editing of dataset,
	        * The SKOSDataset object is a container for your SKOS vocabularies, each manager can have multiple dataset which
	        * are accessed via a URIs. Finally there is a SKOSDataFactory object for creating and retrieving SKOSObject from your dataset.
	         */

	        try {

	            // First create a new SKOSManager
	            SKOSManager manager = new SKOSManager();

	            // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)

	            SKOSDataset dataset = manager.loadDataset(URI.create("file:////"+FilePath));
	            //"file:/Users/simon/ontologies/skos/apitest.owl"
	            //"http://potato.cs.man.ac.uk/seanb/skos/diving-skos.rdf"

	            // get all the concepts in this vocabulay and print out the URI

	            for (SKOSConcept concept : dataset.getSKOSConcepts()) {
	            	SKOSSingleConcept conc = new SKOSSingleConcept();

	               // System.out.println("Concept: " + concept.getURI());
	                conc.URI = concept.getURI().toString();
	                conc.id = concept.getURI().toString();

	                /*
	                * ObjectPropertyAssertions - These are relationships between two SKOS entities
	                * DataPropertyAssertion - These relate entities to Literal values
	                * SKOSAnnotation - These are either literal or entity annotation on a particular entity
	                 */

	                // finally get any OWL annotations - the object of a annotation property can be a literal or an entity
	             //   System.out.println("\tAnnotation property assertions:");
	                for (SKOSAnnotation assertion : dataset.getSKOSAnnotations(concept)) {

	                    // if the annotation is a literal annotation?
	                    String lang = "";
	                    String value = "";

	                    if (assertion.isAnnotationByConstant()) {

	                        SKOSLiteral literal = assertion.getAnnotationValueAsConstant();
	                        value = literal.getLiteral();
	                        if(assertion.getURI().getFragment().equals("altLabel"))
	                        {
	                        	conc.altLabels.add(value);
	                        	//System.out.println("Alternative:"+value);
	                        }
	                        if(assertion.getURI().getFragment().equals("prefLabel"))
	                        {
	                        	conc.preferedLabel =value ;
	                        }
	                        if(assertion.getURI().getFragment().equals("definition"))
	                        {
	                        	conc.Description = value;
	                     //   	System.out.println("Definition:"+value);
	                        }
	                        if (!literal.isTyped()) {
	                            // if it has  language
	                            SKOSUntypedLiteral untypedLiteral = literal.getAsSKOSUntypedLiteral();
	                            if (untypedLiteral.hasLang()) {
	                                lang = untypedLiteral.getLang();
	                            }
	                        }
	                    }
	                    else {
	                        // annotation is some resource
	                        SKOSEntity entity = assertion.getAnnotationValue();
	                        value = entity.getURI().getFragment();
	                        if(assertion.getURI().getFragment().equals("narrower"))
	                        {
	                        	value = assertion.getAnnotationValue().getURI().toString();
	                        }
	                        if(assertion.getURI().getFragment().equals("broader"))
	                        {
	                        	value = assertion.getAnnotationValue().getURI().toString();
	                        	conc.broaderURLs.add(value);
	                        	
	                        }
	                    }
	                    //System.out.println("\t\t" + assertion.getURI().getFragment() + " " + value + " Lang:" + lang);
	                }
	              //  System.out.println("");
	                UriMap.put(conc.URI,  conc);
	                for(int i = 0; i<conc.altLabels.size();i++)
	                {
	                	String[] labels = conc.altLabels.get(i).split(" ");
	                	for(String lab : labels)
	                	{
	                		termMap.put(lab, conc);
	                	}
	                	
	                }
	                String [] labels = conc.preferedLabel.split(" ");
                	for(String lab : labels)
                	{
                		termMap.put(lab, conc);
                	}
	                //concepts.add(conc);
	            }
	            
	        } catch (SKOSCreationException e) {
	            e.printStackTrace();
	        }
	}
	
	public void annotate(String text,SKOSThesaurus Thesauri)
	{
		String[] text_tokens = MarvinSemAnnotator.tokenizer.tokenize(text);
		Set<String> temp = new HashSet<String>(Arrays.asList(text_tokens));
		text_tokens = temp.toArray(new String[temp.size()]);
		for(String token:text_tokens)
		{
			Collection<SKOSSingleConcept> concepts = termMap.get(token);
			for(SKOSSingleConcept conc:concepts)
			{
				String search_term = conc.preferedLabel;
				int index = text.indexOf(search_term);
				while (index >= 0) {
				    //System.out.println(index);
					boolean wordExists = false;
					Word w = null;
					for(Word wor:MarvinSemAnnotator.words)
					{
						if(wor.starting == index && wor.ending == index+search_term.length())
						{
							w = wor;
							wordExists = true;
							break;
						}
					}
					if(w==null)
					{
						w = new Word();
					}
					w.starting = index;
					w.ending = index + search_term.length();
					w.word = search_term;
					WordMeaningOutputElement wo = new WordMeaningOutputElement();
					wo.appearingWord = search_term;
					wo.id = conc.id;
					wo.AgentName = Thesauri.getVocabularyName();
					wo.AgentVersion = Thesauri.getVersion();
					wo.Description = conc.Description;
					wo.EnvironmentDesc = MarvinSemAnnotator.Environment;
					wo.Location = MarvinSemAnnotator.Location;
					wo.URL = conc.URI;
					wo.Source = "SKOS";
					wo.startAt = index;
					wo.endAt = index+search_term.length();
					boolean containsAnnotations = false;
					for(WordMeaningOutputElement meaning:w.wordmeanings)
					{
						if(wo.URL.equals(meaning.URL))
						{
							containsAnnotations = true;
						}
					}
					if(!containsAnnotations){
					w.wordmeanings.add(wo);
					}
					for(String URI:conc.broaderURLs)
					{
						AnnotateWithBroader(URI,w,Thesauri);
					}
					if(!wordExists)
					{
						MarvinSemAnnotator.words.add(w);
					}
				    index = text.indexOf(search_term, index + 1);
				}
				for(int i = 0;i<conc.altLabels.size();i++)
				{
					search_term = conc.altLabels.get(i);
					index = text.indexOf(search_term);
					while (index >= 0) {
					    //System.out.println(index);
						boolean wordExists = false;
						Word w = null;
						for(Word wor:MarvinSemAnnotator.words)
						{
							if(wor.starting == index && wor.ending == index+search_term.length())
							{
								w = wor;
								wordExists = true;
								break;
							}
						}
						if(w==null)
						{
							w = new Word();
						}
						w.starting = index;
						w.ending = index + search_term.length();
						w.word = search_term;
						WordMeaningOutputElement wo = new WordMeaningOutputElement();
						wo.appearingWord = search_term;
						wo.id = conc.id;
						wo.AgentName = Thesauri.getVocabularyName();
						wo.AgentVersion = Thesauri.getVersion();
						wo.Description = conc.Description;
						wo.EnvironmentDesc = MarvinSemAnnotator.Environment;
						wo.Location = MarvinSemAnnotator.Location;
						wo.URL = conc.URI;
						wo.Source = "SKOS";
						wo.startAt = index;
						wo.endAt = index+search_term.length();
						boolean containsAnnotations = false;
						for(WordMeaningOutputElement meaning:w.wordmeanings)
						{
							if(wo.URL.equals(meaning.URL))
							{
								containsAnnotations = true;
							}
						}
						if(!containsAnnotations){
						w.wordmeanings.add(wo);
						}
						for(String URI:conc.broaderURLs)
						{
							AnnotateWithBroader(URI,w,Thesauri);
						}
						if(!wordExists)
						{
							MarvinSemAnnotator.words.add(w);
						}
					    index = text.indexOf(search_term, index + 1);
					}
				}
			}
			
		}
	}
	
	public void AnnotateWithBroader(String broaderURI, Word w,SKOSThesaurus Thesauri)
	{
		SKOSSingleConcept conc = UriMap.get(broaderURI);
		WordMeaningOutputElement wo = new WordMeaningOutputElement();
		wo.appearingWord = w.word;
		wo.id = conc.id;
		wo.AgentName = Thesauri.getVocabularyName();
		wo.AgentVersion = Thesauri.getVersion();
		wo.Description = conc.Description;
		wo.EnvironmentDesc = MarvinSemAnnotator.Environment;
		wo.Location = MarvinSemAnnotator.Location;
		wo.URL = conc.URI;
		wo.Source = "SKOS";
		wo.startAt = w.starting;
		wo.endAt = w.ending;
		boolean containsAnnotations = false;
		for(WordMeaningOutputElement meaning:w.wordmeanings)
		{
			if(wo.URL.equals(meaning.URL))
			{
				containsAnnotations = true;
			}
		}
		if(!containsAnnotations){
		w.wordmeanings.add(wo);
		}
		for(String URI:conc.broaderURLs)
		{
			AnnotateWithBroader(URI,w,Thesauri);
		}
		
	}

}
