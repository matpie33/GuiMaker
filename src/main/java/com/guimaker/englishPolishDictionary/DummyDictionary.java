package com.guimaker.englishPolishDictionary;

import java.net.URLConnection;
import java.util.List;

public class DummyDictionary implements EnglishPolishDictionary {
	@Override
	public String getApiUrlTemplate() {
		return "http://edict.pl/test";
	}

	@Override
	public List<String> getWordMeaningsFromJSON(URLConnection request,
			String wordToCheck) {
		throw new UnsupportedOperationException(
				"This class is only for tests.");
	}
}
