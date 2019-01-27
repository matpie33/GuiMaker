package com.guimaker.webPanel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EnglishDictionaryCaller {
	private final static String DICTIONARY_API_TEMPLATE = "https://glosbe"
			+ ".com/gapi/translate?from=%s&dest=%s&format=json&phrase"
			+ "=%s&pretty=true";
	private static final String ROOT_NODE = "tuc";
	private static final String MEANING_ROOT_NODE = "phrase";
	private static final String MEANING_CHILD_NODE = "text";
	private static final String POLISH_LANGUAGE = "pol";
	private static final String ENGLISH_LANGUAGE = "eng";
	public static final String LETTER_REGEX = "[a-zA-z]";

	//called from javascript
	public String callDictionaryForEnglishWord(String wordToCheck)
			throws IOException {
		wordToCheck = wordToCheck.trim();
		wordToCheck = removeNonLetterCharsFromFirstAndLastIndex(wordToCheck);
		wordToCheck = URLEncoder.encode(wordToCheck, "UTF-8");
		URLConnection request = makeApiCallToDictionary(wordToCheck,
				ENGLISH_LANGUAGE, POLISH_LANGUAGE);
		return getWordMeaningsFromJSON(request).toString();
	}

	private String removeNonLetterCharsFromFirstAndLastIndex(
			String wordToCheck) {
		while (!(wordToCheck.charAt(0) + "").matches(LETTER_REGEX)) {
			wordToCheck = wordToCheck.substring(1, wordToCheck.length());
		}

		while ((!(wordToCheck.charAt(wordToCheck.length() - 1) + "").matches(
				"[a-zA-Z]"))) {
			wordToCheck = wordToCheck.substring(0, wordToCheck.length() - 1);
		}
		return wordToCheck;
	}

	private List<String> getWordMeaningsFromJSON(URLConnection request)
			throws IOException {
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(
				new InputStreamReader((InputStream) request.getContent(),
						Charset.forName("Utf-8")));
		JsonObject rootObject = root.getAsJsonObject();
		JsonArray nodesList = rootObject.getAsJsonArray(ROOT_NODE);

		List<String> meanings = new ArrayList<>();
		for (JsonElement jsonElement : nodesList) {
			JsonObject meaningNode = jsonElement.getAsJsonObject();
			if (meaningNode.has(MEANING_ROOT_NODE)) {
				JsonObject phrase = meaningNode.getAsJsonObject(
						MEANING_ROOT_NODE);
				String text = phrase.getAsJsonPrimitive(MEANING_CHILD_NODE)
									.getAsString();
				meanings.add(text);
			}
		}
		return meanings;
	}

	private URLConnection makeApiCallToDictionary(String wordToCheck,
			String sourceLanguage, String targetLanguage) throws IOException {
		URL url = new URL(String.format(DICTIONARY_API_TEMPLATE, sourceLanguage,
				targetLanguage, wordToCheck));
		URLConnection request = url.openConnection();
		request.connect();
		return request;
	}

}
