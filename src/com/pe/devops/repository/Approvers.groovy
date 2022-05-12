package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.library.Utils

class Approvers {
    static root = Script.root
    String pathWhiteListYaml
    RawFileRepository rawFileRepository = new RawFileRepository()
    Map whiteListMap
    Utils util = new Utils()

    Approvers(String pathWhiteListYaml) {
        this.pathWhiteListYaml = pathWhiteListYaml
    }

    Map getWhiteListDataYaml() {
        Map data = [:]
        String pathType = this.util.getProtocol(this.pathWhiteListYaml)
        //path dentro del repo al hacer checkout
        if (pathType.equals('file')) {
            data = root.readYaml(file: this.pathWhiteListYaml)
        } else {
            // hace el curl
            String fileName = this.rawFileRepository.getFile(this.pathWhiteListYaml)
            data = root.readYaml(file: fileName)
        }
        root.println(data)
        return data
    }

    List getAproversByProject(String projectName) {
        assert projectName != null
        root.println("""
        🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢        
        PROJECT_NAME ::::::: ${projectName}
        🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢  
        """)
        this.whiteListMap = getWhiteListDataYaml()
        return this.whiteListMap.get('whitelist_auth_map').get(projectName).get('members')
    }

    String getMembersSepByComa(String projectName){
        List members = getAproversByProject(projectName)
        return this.util.getMembersSepByComa(members)
    }
}
