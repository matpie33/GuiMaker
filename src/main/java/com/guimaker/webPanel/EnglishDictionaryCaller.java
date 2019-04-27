package com.guimaker.webPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EnglishDictionaryCaller {
	private final static String YANDEX_API_TRANSLATE =
			"https://translate." + "yandex.net/api/v1.5/tr/translate?"
					+ "key=trnsl.1.1.20190427T122550Z.f773943926114283"
					+ ".7d3889921f2b6992a8d674d67a2aabc285575d0f&text="
					+ "%s&lang=pl";
	public static final String LETTER_REGEX = "[a-zA-z]";
	//TODO make it easy to switch between different apis and detect, if an api
	//is working or not

	//called from javascript
	public String callDictionaryForEnglishWord(String wordToCheck)
			throws IOException, ParserConfigurationException, SAXException {
		wordToCheck = wordToCheck.trim();
		wordToCheck = removeNonLetterCharsFromFirstAndLastIndex(wordToCheck);
		wordToCheck = URLEncoder.encode(wordToCheck, "UTF-8");
		URLConnection request = makeApiCallToDictionary(wordToCheck);
		return getWordMeaningsFromJSON(request).toString();
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

	private List<String> getWordMeaningsFromJSON(URLConnection request)
			throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(request.getInputStream());
		doc.getDocumentElement()
		   .normalize();
		NodeList root = doc.getElementsByTagName("Translation");
		Node item = root.item(0);
		NodeList childNodes = item.getChildNodes();
		List<String> translations = new ArrayList<>();
		translations.add(childNodes.item(0)
							 .getChildNodes()
							 .item(0)
							 .getNodeValue());

		return translations;
	}

	private URLConnection makeApiCallToDictionary(String wordToCheck)
			throws IOException {
		URL url = new URL(String.format(YANDEX_API_TRANSLATE, wordToCheck));
		URLConnection request = url.openConnection();
		request.connect();
		return request;
	}

}
