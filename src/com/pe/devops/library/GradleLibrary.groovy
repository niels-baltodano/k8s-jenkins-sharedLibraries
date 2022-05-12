package com.pe.devops.library

import com.pe.devops.Script

class GradleLibrary {
    static root = Script.root
    String credential
    GradleLibrary(String credential){
        this.credential = credential
    }
    void replace_with_credentials(Map param = [:]) {
            applyCommand { Map data ->
                param.put("data", data)
            }
            //root.println(param)
        replace_values(param)
    }

    void replace_in_file(Map param = [:]) {
        String path_file = param.get("path_file")
        String sep = param.get('sep')
        root.sh("sed -i.bak 's${sep}${param.lkey}=${param.rvalue}${sep}${param.lkey}=${param.dkey}${sep}g' $path_file")
    }

    void replace_values(Map param = [:]){
        def names = param.get('names')
        String path_file = param.get('path_file')
        def data= param.get('data')

        def str = """
                    sed -i.bak 's/${names.user}=/${names.user}=${data.username}/g' ${path_file}
                    sed -i.bak 's/${names.password}=/${names.password}=${data.password}/g' ${path_file}
                    """
        root.withCredentials([
                root.usernamePassword(credentialsId: this.credential, usernameVariable: 'username', passwordVariable: 'password')
        ]) {
            root.sh(str)
        }


    }

    void build_project(String gradlecmd){
        root.sh("./gradlew ${gradlecmd}")
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
