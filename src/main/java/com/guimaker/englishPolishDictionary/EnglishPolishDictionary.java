package com.guimaker.englishPolishDictionary;

import java.net.URLConnection;
import java.util.List;

public interface EnglishPolishDictionary {

	public String getApiUrlTemplate ();
	public List<String> getWordMeaningsFromJSON(URLConnection request);

}
