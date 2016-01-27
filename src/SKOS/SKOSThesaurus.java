package SKOS;

public class SKOSThesaurus {
	private String filePath;
	private String VocabularyName;
	private String version;
	private boolean used;
	public SKOS Thesaurus;
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the use
	 */
	public boolean isUsed() {
		return used;
	}
	/**
	 * @param use the use to set
	 */
	public void setIsUsed(boolean use) {
		this.used = use;
	}
	/**
	 * @return the vocabularyName
	 */
	public String getVocabularyName() {
		return VocabularyName;
	}
	/**
	 * @param vocabularyName the vocabularyName to set
	 */
	public void setVocabularyName(String vocabularyName) {
		VocabularyName = vocabularyName;
	}

}
