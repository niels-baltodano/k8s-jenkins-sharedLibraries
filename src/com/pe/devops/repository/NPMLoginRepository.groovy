package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient

class NPMLoginRepository {
    static root = Script.root
    String server = "https://${Defaults.ARTIFACTORY_SERVER_NPM}"
    String credential = Defaults.ARTIFACTORY_SERVER_NPM_CREDENTIAL
    CurlHttpClient client = new CurlHttpClient(server: this.server)

    void loginArtifactory(){
        Map rsp
        applyCommand{ Map data ->
            rsp = this.client.put(
                    headers: ['Content-Type':'application/json', 'Accept': 'application/json'],
                    data: [name: data.username, password: data.password],
                    silent: [s: true],
                    insecure: [s: true],
                    path: "/-/user/org.couchdb.user:'${data.username}'"
            )
        }//fin closure npm set sass_binary_dir=$root.WORKSPACE/npm-packages-offline-cache
        root.sh(label: "npm set registry --> ${this.server}" , script: """
        	echo "" > \$(npm config get userconfig)
            npm set registry "${this.server}/"
            npm set //${Defaults.ARTIFACTORY_SERVER_NPM}/:_authToken ${rsp.token}
            npm set strict-ssl false
        """)
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
