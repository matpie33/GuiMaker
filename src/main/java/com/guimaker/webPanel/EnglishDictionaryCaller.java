package com.guimaker.webPanel;

import com.guimaker.englishPolishDictionary.DictDictionary;
import com.guimaker.englishPolishDictionary.DummyDictionary;
import com.guimaker.englishPolishDictionary.EnglishPolishDictionary;
import com.guimaker.englishPolishDictionary.YandexDictionary;
import com.guimaker.strings.ExceptionsMessages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class EnglishDictionaryCaller {
	private EnglishPolishDictionary defaultAPI = new DictDictionary();
	private List<EnglishPolishDictionary> englishPolishAlternativeApis = Arrays.asList(
			defaultAPI, new DummyDictionary());
	private static final String LETTER_REGEX = "[a-zA-z]";

	//called from javascript
	public String callDictionaryForEnglishWord(String wordToCheck)
			throws IOException {
		wordToCheck = wordToCheck.trim();
		wordToCheck = removeNonLetterCharsFromFirstAndLastIndex(wordToCheck);
		wordToCheck = URLEncoder.encode(wordToCheck, "UTF-8");
		URLConnection request = makeApiCallToDictionary(wordToCheck);
		return request == null ?
				ExceptionsMessages.NO_WORKING_DICTIONARY_API :
				defaultAPI.getWordMeaningsFromJSON(request, wordToCheck)
						  .toString();
	}

	private String removeNonLetterCharsFromFirstAndLastIndex(
			String wordToCheck) {
		while (!wordToCheck.isEmpty() && !(wordToCheck.charAt(0) + "").matches(
				LETTER_REGEX)) {
			wordToCheck = wordToCheck.substring(1, wordToCheck.length());
		}

		while (!wordToCheck.isEmpty() && (!(
				wordToCheck.charAt(wordToCheck.length() - 1) + "").matches(
				"[a-zA-Z]"))) {
			wordToCheck = wordToCheck.substring(0, wordToCheck.length() - 1);
		}
		return wordToCheck;
	}

	private URLConnection makeApiCallToDictionary(String wordToCheck)
			throws IOException {
		URLConnection urlConnection = connectToAPI(wordToCheck, defaultAPI);
		if (statusIsOK(urlConnection)) {
			return urlConnection;
		}
		for (EnglishPolishDictionary api : englishPolishAlternativeApis) {
			if (api == defaultAPI) {
				continue;
			}
			urlConnection = connectToAPI(wordToCheck, api);
			if (statusIsOK(urlConnection)) {
				defaultAPI = api;
				return urlConnection;
			}
		}
		return null;

	}

	private boolean statusIsOK(URLConnection urlConnection) throws IOException {
		return ((HttpURLConnection) urlConnection).getResponseCode()
				== HttpURLConnection.HTTP_OK;
	}

	private URLConnection connectToAPI(String wordToCheck,
			EnglishPolishDictionary apiToUse) throws IOException {
		URL url = new URL(
				String.format(apiToUse.getApiUrlTemplate(), wordToCheck));
		URLConnection request = url.openConnection();
		request.connect();
		return request;
	}

}
