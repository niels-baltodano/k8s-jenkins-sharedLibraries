package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient

class RawFileRepository {
    static root = Script.root
    String server = Defaults.BITBUCKET_SERVER
    String credential = Defaults.BITBUCKET_CREDENTIAL
    CurlHttpClient client = new CurlHttpClient(server: this.server)

    void getFile(String branch, String path, String filename){
        if(root.fileExists("${filename}")){
            root.sh("rm -rf ${filename}")
        }
        //path : /projects/DBPSRE/repos/devops-scripts/raw/$filename?at=refs/heads/$branch
        applyCommand { Map data ->
            this.client.get(
                    headers: data.headers,
                    raw: 'yes',
                    insecure:[s: true],
                    path: "$path/$filename?at=refs/heads/$branch"
            )
        }//fin closure

        if(!root.fileExists("${filename}")){
            throw new Exception("No se pudo descargar el archivo $filename")
        }
    }
    String getFile(String url){
        String url_base = url.replace('%2F', '/')
        def url_split = url_base.split("\\?")
        String url_file = url_split.first()
        String filename = root.sh(label: 'get filename', returnStdout: true, script: "basename ${url_file}").trim()
        String path = url_base.replace(this.server,'')

        if(root.fileExists("${filename}")){
            root.sh("rm -rf ${filename}")
        }
        applyCommand { Map data ->
            this.client.get(
                    headers: data.headers,
                    raw: 'yes',
                    silent: [s: true],
                    insecure:[s: true],
                    path: path
            )
        }//fin closure

        if(!root.fileExists("${filename}")){
            throw new Exception("No se pudo descargar el archivo $url")
        }
        return filename
    }

    void applyCommand(Closure closure) {
        root.withCredentials([root.string(credentialsId: this.credential, variable: 'bearer_id')]) {
            closure(
                    headers: [
                            'authorization': "Bearer ${root.env.bearer_id}"
                    ]
            )
        }
    }
}