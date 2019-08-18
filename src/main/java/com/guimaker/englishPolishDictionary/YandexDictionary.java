package com.guimaker.englishPolishDictionary;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class YandexDictionary implements EnglishPolishDictionary {

	private final static String YANDEX_API_TRANSLATE =
			"https://translate.yandex.net/api/v1.5/tr/translate?"
					+ "key=trnsl.1.1.20190427T122550Z.f773943926114283"
					+ ".7d3889921f2b6992a8d674d67a2aabc285575d0f&text="
					+ "%s&lang=pl";

	@Override
	public String getApiUrlTemplate() {
		return YANDEX_API_TRANSLATE;
	}

	@Override
	public List<String> getWordMeaningsFromJSON(URLConnection request,
			String wordToCheck) {
		try {

			return getWords(request);
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	private List<String> getWords(URLConnection request)
			throws ParserConfigurationException, SAXException, IOException {
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
}
