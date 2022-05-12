package com.pe.devops.service.confluence

class ConfluenceAttachment {

	String replace(Map param = [:]) {
		String pageId 	= param.get('pageId')
		String filepath = param.get('filepath')

		def attachment = new com.pe.devops.repository.ConfluenceAttachmentRepository().replace(
				pageId: pageId,
				filepath: filepath
		)
		if (attachment['size'] == 0) {
			return false
		}
		return attachment['results'][0]['id']
    }
}