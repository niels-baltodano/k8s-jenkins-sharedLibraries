package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient

class ConfluencePageRepository {
	static root 			= Script.root
	def server 				= Defaults.CONFLUENCE_SERVER
	def credential 			= Defaults.CONFLUENCE_CREDENTIAL
	CurlHttpClient client 	= new CurlHttpClient(server: this.server)

	Map get(String id, String expand = "body.storage,version") {
		Map responseJson	= [:]

		applyCommand { Map data ->
			responseJson = this.client.get(
				username: data.username,
				password: data.password,
				path: "/rest/api/content/${id}",
				query: [
					expand: expand
				]
			)
		}
		return responseJson
	}

	Map updateBody(String id, String type, String title, int version, String bodyValue = '') {
		Map responseJson	= [:]

		applyCommand { Map data ->
			responseJson = new CurlHttpClient(server: this.server).put(
				username: data.username,
				password: data.password,
				path: "/rest/api/content/${id}",
				headers: [
					'Content-Type': 'application/json'
				],
				data: [
					id: id,
					type: type,
					title: title,
					version: [number: (version + 1)],
					body: [storage: [value: bodyValue, representation: 'storage']]
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