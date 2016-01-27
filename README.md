# Marvin - semantic text annotator
## Abstract
Marvin is a semantic text annotation tool that uses various external sources to annotate inputed text. Marvin text annotator can be also used as a java library

##Installation

Marvin semantic text annotatior is a java program that can be also used as a java library for other application. This means it is an .jar file, which contains all the resources inside it. However, for use of Wordnet, Wordnet has to be installed from https://wordnet.princeton.edu/wordnet/download/current-version/

After the installation it is necessary to configure Wordnet path in file_properties.xml file. Tag that currently states following:

```<param name="dictionary_path" value="C:\Program Files (x86)\WordNet\2.1\dict"/>```

has to be changed with the correct path within the machine where Wordnet is installed. 

Before running, you will also need to set the configuration of what resuources you would like to use in settings.cfg file. This file should be supplied with information on resources (what should be used, host and port of MetaMap if used, DBPedia SPARQL gateway if DBPedia is used, location of SKOS files and some information about your system and location for provenance)

There are no other requirements for the installation.

##Running Marvin semantic text annotator

In order to run Marvin semantic annotator you can type in command line 

```java -jar Marvin.jar "Sentence to be semantically annotated."```

##Running Marvin as a library

How to run marvin as a library would be best described in a code snippet bellow. You need just to create object of MarvinSemAnnotator and invoke method "annotate" with the text to be annotated.

Marvin returns a linked list of Word objects and each Word object contains a LinkedList of annotations in list called wordmeanings.


```java
public void Annotate(String valueToParse){
	MarvinSemAnnotator marvin = new MarvinSemAnnotator();
	LinkedList<Word> words = marvin.annotate(valueToParse);
	for(Word w:words){
		for(int s = 0;s<words.get(p).wordmeanings.size();s++){
			String source =  words.get(p).wordmeanings.get(s).Source;
			String ID =  words.get(p).wordmeanings.get(s).id;
			String URL = words.get(p).wordmeanings.get(s).URL;
			int startAt = words.get(p).wordmeanings.get(s).startAt;
			int endAt = words.get(p).wordmeanings.get(s).endAt;
			String appearing_word = words.get(p).wordmeanings.get(s).appearingWord;
			String Description = words.get(p).wordmeanings.get(s).Description;
			String Location = words.get(p).wordmeanings.get(s).Location;
			String EnvironmentDesc = words.get(p).wordmeanings.get(s).EnvironmentDesc;
			String AgentName = words.get(p).wordmeanings.get(s).AgentName;
			String AgentVersion = words.get(p).wordmeanings.get(s).AgentVersion;
		}
	}
}
```

##Methodology


##Credits

Developed by Nikola Milosevic - nikola.milosevic[at nospam] manchester.ac.uk

Feel free to contact me for any kind of information or queries regarding the project. 
