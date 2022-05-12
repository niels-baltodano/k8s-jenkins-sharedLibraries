package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.interfaces.WasDeploy

class WasDeployRepository implements WasDeploy {

    static root = Script.root
    Map backend_data = [:]
    String envi
    String component
    //full path of ear
    String artifact

    WasDeployRepository(Map backend_data, String envi, String component, String artifact) {
        this.backend_data = backend_data
        this.envi = envi
        this.component = component
        this.artifact = artifact
    }
    Map getBackendMap(){
        return this.backend_data.get('backend').get(this.envi.toLowerCase()).get(this.component)
    }
    Map getRemoteMap(){
        Map backend = getBackendMap()
        Map remote = backend.get('remote')
        String credential_manager_console_was = backend.get('credential_manager_console_was')
        String credential_manager_ssh_was = backend.get('credential_manager_ssh_was')
            applyCommand(credential_manager_ssh_was, credential_manager_console_was, {Map dat ->
                remote.put('user', dat.username_ssh)
                remote.put('password', dat.password_ssh)
                remote.put('username_was', dat.username_was)
                remote.put('password_was', dat.password_was)
                remote.put('allowAnyHosts', true)
            })
        return remote
    }

    @Override
    void doUninstall() {
        // verificamos si existe el app en el server
        Map was_data_exist = getExistAppByName()
        if (!was_data_exist.get('app_status')) {
            root.println(
                    """
                    🚀::::::Result::::::🚀 
                        No se encontro el app : ${this.component}
                    🚀::::::Result::::::🚀 
                    """
            )

        }else{
            Map backend = getBackendMap()
            LinkedHashMap remote = backend.get('remote')


            String credential_manager_console_was = backend.get('credential_manager_console_was')
            String credential_manager_ssh_was = backend.get('credential_manager_ssh_was')
            String path_main = backend.get('path_main')
            def paramConWSAdmin = ""
            def path_devops = "${path_main}/devops/scripts"

            Map jythonFileParams = getJythonFileParams()

            applyCommand(credential_manager_ssh_was, credential_manager_console_was, { Map dat ->
                remote.put('user', dat.username_ssh)
                remote.put('password', dat.password_ssh)
                remote.put('username_was', dat.username_was)
                remote.put('password_was', dat.password_was)
                remote.put('allowAnyHosts', true)
                String result
                try {
                    paramConWSAdmin = "${path_main} ${remote.username_was} ${remote.password_was} ${remote.host} ${remote.portDmgr}"
                    def orderParam = jythonFileParams.get('inEarNameFromServer')
                    def commandUninstall = "sh ${path_devops}/param_wsadminAssistant.sh ${paramConWSAdmin} ${path_devops}/uninstall.py ${orderParam} | tr '\\n' ','"
                    result = root.sshCommand(remote: remote, command: commandUninstall).trim()
                } catch (err) {
                    root.println(err.getMessage())
                }
                wasException(result)
            })
        }
    }
    Map getJythonFileParams(){
        Map backend = getBackendMap()
        String path_tmp_deploy = backend.get('path_tmp_deploy')
        String nameWar = root.sh(returnStdout: true, script: "jar tf ${this.artifact} | grep war").trim()
        def list_clusters = backend.get('list_clusters')
        String virtual_host = backend.get('virtual_host')
        def list_nodes = backend.get('list_nodes')

        Map was_data_exist = getExistAppByName()
        def app_name = root.sh(returnStdout: true, script: "basename ${this.artifact}").trim()
        Map jythonFileParams = [
                inAppName          : app_name,
                inLocation         : "${path_tmp_deploy}/${app_name}",
                inNameFullWar      : nameWar,
                inListCluster      : list_clusters,
                inVirtualHost      : virtual_host,
                inListNode         : list_nodes,
                inEarNameFromServer: was_data_exist.get('app_name', '')
        ]
        return jythonFileParams
    }
    @Override
    Map getExistAppByName() {
        Map data = [:]
        data.put('app_status', false)
        Map backend = getBackendMap()
        LinkedHashMap remote = backend.get('remote')

        String credential_manager_console_was = backend.get('credential_manager_console_was')
        String credential_manager_ssh_was = backend.get('credential_manager_ssh_was')

        String path_main = backend.get('path_main')

        def path_devops = "${path_main}/devops/scripts"
        def paramConWSAdmin = ""
        applyCommand(credential_manager_ssh_was, credential_manager_console_was, { Map dat ->
            remote.put('user', dat.username_ssh)
            remote.put('password', dat.password_ssh)
            remote.put('username_was', dat.username_was)
            remote.put('password_was', dat.password_was)
            remote.put('allowAnyHosts', true)

            String result
            try {
                paramConWSAdmin = "${path_main} ${remote.username_was} ${remote.password_was} ${remote.host} ${remote.portDmgr}"
                result = root.sshCommand(remote: remote, command: "sh ${path_devops}/param_wsadminAssistant.sh ${paramConWSAdmin} ${path_devops}/listApp.py | tr '\\n' ','").trim()
            } catch (err) {
                root.println(err.getMessage())
            }
            wasException(result)
            root.println("""
            🚀::::::Result::::::🚀 
            ${result}
            🚀::::::Fin Result::::::🚀 
            """)
            def rst = result.split(",")
            rst.each {
                boolean app_status = it.contains(this.component)
                if (app_status) {
                    data.put('app_name', it)
                    data.put('app_status', app_status)
                }
            }

        })
        root.sleep(time: 1, unit: "SECONDS")
        root.println("""
            ✅---> Busqueda de componente : ${this.component} status de instalacion es : ${data.get('app_status')}✅
            ✅<--- FINISH getExistAppByName()✅
        """)
        return data
    }

    @Override
    void cleanPath() {
        root.println "🧹🧹🧹🧹🧹🧹🧹🧹🧹Limpieza🧹🧹🧹🧹🧹🧹🧹🧹🧹"
        try {
            Map remote = getRemoteMap()
            String path_tmp_deploy = getBackendMap().get('path_tmp_deploy')
            root.println("Creacion y Limpieza del path:   📂📂📂 ${path_tmp_deploy} 📂📂📂")
            String result = root.sshCommand(remote: remote, command: "mkdir -p ${path_tmp_deploy} && rm -f ${path_tmp_deploy}/*").trim()
            wasException(result)
        }catch(err){
            root.println(err.getMessage())
        }
        root.println "🧹🧹🧹🧹🧹🧹🧹🧹🧹Fin Limpieza🧹🧹🧹🧹🧹🧹🧹🧹🧹"
    }

    void applyCommand(String credential_ssh, String credential_was, Closure closure) {
        root.withCredentials([root.usernamePassword(credentialsId: "${credential_ssh}", passwordVariable: 'password_ssh', usernameVariable: 'username_ssh'),
                              root.usernamePassword(credentialsId: "${credential_was}", passwordVariable: 'password_was', usernameVariable: 'username_was')]) {
            closure(
                    username_ssh: root.env.username_ssh,
                    password_ssh: root.env.password_ssh,
                    username_was: root.env.username_was,
                    password_was: root.env.password_was
            )
        }
    }

    void applyCommand(String credential_ssh, Closure closure) {
        root.withCredentials([root.usernamePassword(credentialsId: "${credential_ssh}", passwordVariable: 'password_ssh', usernameVariable: 'username_ssh')]) {
            closure(
                    username_ssh: root.env.username_ssh,
                    password_ssh: root.env.password_ssh
            )
        }
    }

    void wasException(String result) {
        if (result.contains("WASX7213I")) {
            throw new Exception("This scripting client is not connected")
        }
        if (result.contains("WASX8011W")) {
            throw new Exception("AdminTask object is not available")
        }
        if (result.contains("WASX7017E")) {
            throw new Exception("Exception received while running file, exception information: com.ibm.ws.scripting.ScriptingException")
        }
        if (result.contains("WASX7206W")) {
            throw new Exception("The application management service is not running. Application management commands will not run.")
        }
        if (result.contains("ERROR")) {
            throw new Exception(result)
        }
    }
}
