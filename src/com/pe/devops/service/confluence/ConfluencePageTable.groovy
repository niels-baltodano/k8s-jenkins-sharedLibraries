package com.pe.devops.service.confluence


import com.pe.devops.Script
import com.pe.devops.library.JsoupTableUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ConfluencePageTable {
	static root = Script.root
	com.pe.devops.repository.ConfluencePageRepository repository = new com.pe.devops.repository.ConfluencePageRepository()

	String rowReplace(Map param = [:]) {
		String pageId 	= param.get("pageId")
		Map search 		= param.get("search", [:])
		Map replace 	= param.get("replace", [:])
		String selector = param.get("selector", 'table')

		Map data 		= repository.get(pageId)
		String body		= objectRowReplace(data['body']['storage']['value'] as String, search, replace, selector)
		Map response 	= repository.updateBody(
			pageId,
			data['type'] as String,
			data['title'] as String,
			data['version']['number'] as int,
			body
		)
		return response['id']
	}

	private static String objectRowReplace(String html, Map search, Map replace, String selector = 'table') {
		Document document 	= Jsoup.parse(html)
		JsoupTableUtils.findAndReplaceRow(document, search, replace, selector)

		root.println "- Search: " + search.toString()
		root.println "- Replace: " + replace.toString()

		return document.html()
	}
}