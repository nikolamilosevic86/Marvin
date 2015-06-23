package DBPedia;

import java.util.LinkedList;

import org.apache.commons.lang3.text.WordUtils;

import Main.WordMeaningOutputElement;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DBPediaQuery {
	
	public LinkedList<WordMeaningOutputElement> queryDBPedia(String word,int start, int end)
	{
		LinkedList<WordMeaningOutputElement> elements = new LinkedList<WordMeaningOutputElement>();
		word = WordUtils.capitalize(word);
		
		  ParameterizedSparqlString qs = new ParameterizedSparqlString( "" +
	        		"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n"+
	        			"prefix dbpedia-owl: <http://dbpedia.org/ontology/>\n"+
	        			"prefix dbpprop: <http://dbpedia.org/property/>\n"+
	        			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+

	        			"SELECT DISTINCT * \n"+
	        			"WHERE { \n"+
	        		  " ?resource  rdfs:label ?label ;\n"+
	        			        " dbpedia-owl:abstract ?abstract .\n"+
	        			   "FILTER ( lang(?abstract) = 'en' ) \n"+ 
	        			"}");

		  Literal wordLiteral = ResourceFactory.createLangLiteral( word, "en" );
	        qs.setParam( "label", wordLiteral );
	        QueryExecution exec = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", qs.asQuery() );

	        // Normally you'd just do results = exec.execSelect(), but I want to 
	        // use this ResultSet twice, so I'm making a copy of it.  
	        ResultSet results = ResultSetFactory.copyResults( exec.execSelect() );

	        while ( results.hasNext() ) {
	            // As RobV pointed out, don't use the `?` in the variable
	            // name here. Use *just* the name of the variable.
	        	QuerySolution sol = (QuerySolution) results.next();
	        	WordMeaningOutputElement o = new WordMeaningOutputElement();
	        	o.Description = sol.get("?abstract").toString();
				o.appearingWord = word;
				o.Source = "DBPedia";
				o.id = sol.get( "resource" ).toString();
				o.startAt = start;
				o.endAt = end;
				o.URL = sol.get( "resource" ).toString();
				boolean exists = false;
				for(int k = 0;k<elements.size();k++)
				{
					if(elements.get(k).id.equals(o.id))
					{
						exists = true;
					}
				}
				if(!exists){
					elements.add(o);
				}

	        }
	        word = word.toLowerCase();
	        word = word.substring(0,1).toUpperCase()+word.substring(1);
	         qs = new ParameterizedSparqlString( "" +
	        		"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n"+
	        			"prefix dbpedia-owl: <http://dbpedia.org/ontology/>\n"+
	        			"prefix dbpprop: <http://dbpedia.org/property/>\n"+
	        			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+

	        			"SELECT DISTINCT * \n"+
	        			"WHERE { \n"+
	        		  " ?resource  rdfs:label ?label ;\n"+
	        			        " dbpedia-owl:abstract ?abstract .\n"+
	        			   "FILTER ( lang(?abstract) = 'en' ) \n"+ 
	        			"}");

		    wordLiteral = ResourceFactory.createLangLiteral( word, "en" );
	        qs.setParam( "label", wordLiteral );
	        exec = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", qs.asQuery() );

	        // Normally you'd just do results = exec.execSelect(), but I want to 
	        // use this ResultSet twice, so I'm making a copy of it.  
	        results = ResultSetFactory.copyResults( exec.execSelect() );

	        while ( results.hasNext() ) {
	            // As RobV pointed out, don't use the `?` in the variable
	            // name here. Use *just* the name of the variable.
	        	QuerySolution sol = (QuerySolution) results.next();
	        	WordMeaningOutputElement o = new WordMeaningOutputElement();
	        	o.Description = sol.get("?abstract").toString();
				o.appearingWord = word;
				o.Source = "DBPedia";
				o.id = sol.get( "resource" ).toString();
				o.startAt = start;
				o.endAt = end;
				o.URL = sol.get( "resource" ).toString();
				boolean exists = false;
				for(int k = 0;k<elements.size();k++)
				{
					if(elements.get(k).id.equals(o.id))
					{
						exists = true;
					}
				}
				if(!exists){
					elements.add(o);
				}

	        }
	        

	       // ResultSetFormatter.out( results );
		
		
		return elements;
	}
	
	 public static void main(String[] args) {
	        ParameterizedSparqlString qs = new ParameterizedSparqlString( "" +
	        		"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n"+
	        			"prefix dbpedia-owl: <http://dbpedia.org/ontology/>\n"+
	        			"prefix dbpprop: <http://dbpedia.org/property/>\n"+
	        			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+

	        			"SELECT DISTINCT * \n"+
	        			"WHERE { \n"+
	        		  " ?resource  rdfs:label ?label ;\n"+
	        			        " dbpedia-owl:abstract ?abstract .\n"+
	        			   "FILTER ( lang(?abstract) = 'en' ) \n"+ 
	        			"}");

	        Literal london = ResourceFactory.createLangLiteral( "London", "en" );
	        qs.setParam( "label", london );

	        System.out.println( qs );

	        QueryExecution exec = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", qs.asQuery() );

	        // Normally you'd just do results = exec.execSelect(), but I want to 
	        // use this ResultSet twice, so I'm making a copy of it.  
	        ResultSet results = ResultSetFactory.copyResults( exec.execSelect() );

	        while ( results.hasNext() ) {
	            // As RobV pointed out, don't use the `?` in the variable
	            // name here. Use *just* the name of the variable.
	        	QuerySolution sol = (QuerySolution) results.next();
	        	RDFNode node = sol.get( "resource" );
	        	
	        	System.out.println(sol.get("?abstract"));
	        	
	        
	            System.out.println( node);
	        }

	        // A simpler way of printing the results.
	        ResultSetFormatter.out( results );
	    }
}
