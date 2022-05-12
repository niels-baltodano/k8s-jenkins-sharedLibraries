package com.pe.devops.library

import com.pe.devops.Script
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JsoupTableUtils {
	static root = Script.root

	static findAndReplaceRow(Document document, Map search, Map replace, String selector = 'table') {
		Element table = document.select(selector).get(0)
		Elements rows = table.select("tbody tr")
		// Each per row
		for (Element row : rows) {
			Elements tds = row.select("td");
			int exist = 0
			// Verify search
			if (tds.size() > 0) {
				search.each { key, val ->
					if (tds.get(key).text().equals(val)) {
						exist++
					}
				}
			}
			// Replace values
			if (exist > 0 && exist.equals(search.size())) {
				replace.each { key, val ->
					tds.get(key).html(val)
				}
			}
		}
	}
}