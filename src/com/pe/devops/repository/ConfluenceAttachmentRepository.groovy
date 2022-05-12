package com.pe.devops.repository

import com.pe.devops.Script

//@Grab(group='commons-io', module='commons-io', version='2.7')
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient
import org.apache.commons.io.FilenameUtils

/**
 * @see https://docs.atlassian.com/atlassian-confluence/REST/6.6.0/#content/{id}/child/attachment-getAttachments
 */
class ConfluenceAttachmentRepository {
	static root 			= Script.root
	String server 			= Defaults.CONFLUENCE_SERVER
	String credential 		= Defaults.CONFLUENCE_CREDENTIAL
	CurlHttpClient client 	= new CurlHttpClient(server: this.server)

	Map replace(Map param = [:]) {
		String pageId 	= param.get('pageId')
    	String filepath = param.get('filepath')
    	String filename = FilenameUtils.getName(filepath)

		Map details 	= this.getDetails(pageId: pageId, filename: filename)
		if (details['size'] != 0) {
			String attachmentId = details['results'][0]['id']
			def isdeleted 		= this.delete(id: attachmentId)
		}
    	return this.create(pageId: pageId, filepath: filepath)
    }

    Map getDetails(Map param = [:]) {
		String pageId 		= param.get('pageId')	// required
		String filename 	= param.get('filename') // required
		Map responseJson	= [:]

		applyCommand { Map data ->
			responseJson = this.client.get(
				username: data.username,
				password: data.password,
				path: "/rest/api/content/${pageId}/child/attachment",
				query: [
					filename: 	filename,
					start: 		param.get("start", 0),
					limit: 		param.get("limit", 10)
				]
			)
		}
		return responseJson
    }

    Boolean delete(Map param = [:]) {
		String id 			= param.get('id')
		Map responseJson	= [:]

		applyCommand { Map data ->
    		responseJson = this.client.delete(
				username: data.username,
				password: data.password,
				path: "/rest/api/content/${id}",
				headers: [
					'Content-Type': 'application/json',
					'Accept': 'application/json'
				],
				query: [
		    		status: 'current'
			    ]
			)
    	}
		return true
    }

    Map create(Map param = [:]) {
    	def pageId 			= param.get('pageId')
    	def filepath 		= param.get('filepath')
    	Map responseJson	= [:]

		applyCommand { Map data ->
    		responseJson = new CurlHttpClient(server: this.server).post(
				username: data.username,
				password: data.password,
				path: "/rest/api/content/${pageId}/child/attachment",
				headers: [
					'X-Atlassian-Token': 'nocheck'
				],
				form: [
		    		file: """@${filepath}"""
			    ]
			)
    	}
		return responseJson
    }

	void applyCommand(Closure closure) {
		root.withCredentials([
			root.usernamePassword(credentialsId: this.credential, usernameVariable: 'username', passwordVariable: 'password')
		]) {
			closure(
				username: root.env.username,
				password: root.env.password
			)
		}
	}
}