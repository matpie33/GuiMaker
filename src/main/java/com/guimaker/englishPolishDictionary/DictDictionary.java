package com.guimaker.englishPolishDictionary;

import com.guimaker.model.PolishEnglishColumnIndexes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DictDictionary implements EnglishPolishDictionary {

	private static final String API_TEMPLATE = "http://edict.pl/dict?word=%s&words=&lang=EN";
	private static final String TRANSLATIONS_TABLE_SELECTOR = ".resTable";
	private static final String ROW_SELECTOR = "tr";
	private static final String CELL_SELECTOR = "td";
	private static final String HEADER_SELECTOR = "th";
	private static final String HEADER_FOR_PL_TRANSLATIONS = "Polish";
	private static final String HEADER_FOR_EN_TRANSLATIONS = "English";
	private static final int TIMEOUT_MILLISECONDS = 3000;

	@Override
	public String getApiUrlTemplate() {
		return API_TEMPLATE;
	}

	@Override
	public List<String> getWordMeaningsFromJSON(URLConnection request,
			String wordToCheck) throws IOException {
		Document document = Jsoup.parse(request.getURL(), TIMEOUT_MILLISECONDS);
		Element table = document.select(TRANSLATIONS_TABLE_SELECTOR)
								.get(0);
		Elements rows = table.select(ROW_SELECTOR);
		List<String> translations = new ArrayList<>();
		PolishEnglishColumnIndexes indexes = getPolishEnglishColumnIndexes(
				rows);
		extractTranslations(wordToCheck, rows, translations, indexes);
		return translations;
	}

	private void extractTranslations(String wordToCheck, Elements rows,
			List<String> translations,
			PolishEnglishColumnIndexes polishEnglishColumnIndexes) {
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements columns = row.select(CELL_SELECTOR);

			if (columns.get(polishEnglishColumnIndexes.getEn())
					   .text()
					   .equalsIgnoreCase(wordToCheck)) {
				translations.add(columns.get(polishEnglishColumnIndexes.getPl())
										.text());
			}
		}
	}

	private PolishEnglishColumnIndexes getPolishEnglishColumnIndexes(
			Elements rows) {
		Element element = rows.get(0);
		Elements headers = element.select(HEADER_SELECTOR);
		PolishEnglishColumnIndexes indexes = new PolishEnglishColumnIndexes();
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i)
					   .text()
					   .equalsIgnoreCase(HEADER_FOR_PL_TRANSLATIONS)) {
				indexes.setPl(i);
			}
			else if (headers.get(i)
							.text()
							.equalsIgnoreCase(HEADER_FOR_EN_TRANSLATIONS)) {
				indexes.setEn(i);
			}
		}
		return indexes;
	}
}
