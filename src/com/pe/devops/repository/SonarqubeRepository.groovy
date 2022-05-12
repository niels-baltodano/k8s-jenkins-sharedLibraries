package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient

class SonarqubeRepository {
    static root = Script.root
    String server = Defaults.SONARQUBE_SERVER
    String credential = Defaults.SONARQUBE_CREDENTIAL
    CurlHttpClient client = new CurlHttpClient(server: this.server)
    Map sonarProjects = [:]
    Map getProjets() {
        def date = new Date()
        String date_minus = date.plus(-7).format("yyyy-MM-dd")
        Map res = [:]
        applyCommand { Map data ->
            res = this.client.get(
                    path: "/api/projects/search?analyzedBefore=${date_minus}",
                    headers: data.headers,
                    insecure: [s: true]
            )
        }
        return res
    }
    void deleteProjectsSonar(){
        this.sonarProjects = getProjets()
        List deleteProjects = []
        List components = this.sonarProjects.get('components')
        deleteProjects = components.findAll{it -> (it.key).contains('-BRANCH-')}.collect{it -> it}
        deleteProjects.each {
            apiDelete(it.key)
        }
    }
    void apiDelete(String key){
        applyCommand { Map data ->
            this.client.post(
                    path: "/api/projects/delete?project=${key}",
                    headers: data.headers,
                    insecure: [s: true]
            )
        }
    }
    void applyCommand(Closure closure) {
        root.withCredentials([root.string(credentialsId: this.credential, variable: 'bearer_id')]) {
            def encoded = ("${root.env.bearer_id}:").bytes.encodeBase64().toString()
            closure(
                    headers: [
                            'Content-Type' : 'application/json',
                            'Authorization': "Basic ${encoded}"
                    ]
            )
        }
    }
}
