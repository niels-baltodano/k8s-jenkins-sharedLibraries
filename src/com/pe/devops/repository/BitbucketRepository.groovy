package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient
import groovy.json.JsonException
import groovy.json.JsonSlurper

class BitbucketRepository {
    static root = Script.root
    String server = Defaults.BITBUCKET_SERVER
    String credential = Defaults.BITBUCKET_CREDENTIAL
    CurlHttpClient client = new CurlHttpClient(server: this.server)

    void updatePR(String state, String name, String bitbucketPayload) {
        assert bitbucketPayload != null
        if (!bitbucketPayload) {
            throw new Exception("Bitbucket-Payload es nula o vacia !!!")
        }
        String latestCommit = jsonParse(bitbucketPayload, "fromRef", "latestCommit")
        String bitbucketSourceBranch = jsonParse(bitbucketPayload, "fromRef", "displayId")
        String description = "${bitbucketSourceBranch} - ${root.env.BUILD_NUMBER}"

        applyCommand { Map data ->
            this.client.post(path: "/rest/build-status/1.0/commits/${latestCommit}",
                    headers: data.headers,
                    silent: [s: true],
                    insecure: [s: true],
                    data: ['state': state, 'key': root.env.BUILD_URL, 'name': name, 'url': root.env.BUILD_URL, 'description': description])
        }
    }

    void getZipFileByTag(String project, String repoName, String gitTag) {
        applyCommand { Map data ->
            this.client.get(path: "/rest/api/latest/projects/${project}/repos/${repoName}/archive?at=refs/tags/${gitTag}&format=zip",
                    headers: data.headers,
                    insecure: [s: true],
                    file: [name: "${project}.zip"])
        }
    }

    void applyCommand(Closure closure) {
        root.withCredentials([root.string(credentialsId: this.credential, variable: 'bearer_id')]) {
            closure(headers: ['Content-Type' : 'application/json',
                              'authorization': "Bearer ${root.env.bearer_id}"])
        }
    }

    void applyCommandByCredential(String credential, Closure closure) {
        root.withCredentials([root.usernamePassword(credentialsId: credential, usernameVariable: 'username', passwordVariable: 'password')]) {
            closure(username: root.env.username,
                    password: root.env.password)
        }
    }

    def jsonParse(String json, String root, String key) {
        try {
            JsonSlurper jsonSlurper = new JsonSlurper()
            def parsedJson = jsonSlurper.parseText(json)
            jsonSlurper = null
            return parsedJson.get("pullRequest").get(root).get(key)
        } catch (Exception e) {
            throw new JsonException("Error al traer la llave ${key} del JSON BITBUCKET_PAYLOAD : ", e);
        }
    }
}
