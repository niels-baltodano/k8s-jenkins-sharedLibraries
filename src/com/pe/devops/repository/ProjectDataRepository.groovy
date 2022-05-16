package com.pe.devops.repository

import com.pe.devops.Script

import com.pe.devops.library.Utils


class ProjectDataRepository {
    static root = Script.root
    String pathProjectYaml
 //   RawFileRepository rawFileRepository = new RawFileRepository()
    LinkedHashMap ProjectInfo
    Utils util = new Utils()

    ProjectDataRepository(String pathProjectYaml) {
        this.pathProjectYaml = pathProjectYaml
    }
    //get map completo del yaml
    LinkedHashMap getDataYaml() {
        LinkedHashMap data = [:]
        String pathType = this.util.getProtocol(this.pathProjectYaml)
        if (pathType.equals('file')) {
            data = root.readYaml(file: this.pathProjectYaml)
        } else {
            // hace el curl
            //String fileName = this.rawFileRepository.getFile(this.pathProjectYaml)
            data = root.readYaml(file: fileName)
        }
        return data
    }

    LinkedHashMap getProjectInfo(String projectType, String projectTecType, String component) {
        this.ProjectInfo = getDataYaml()
        return this.ProjectInfo.get(projectType).get(projectTecType).get(component)
    }

    LinkedHashMap getDataMap() {
        Map jobNameType = this.util.getJobNameType(root.env.JOB_NAME)
        String component = jobNameType.get('component')
        String projectTecType = jobNameType.get('projectTecType')
        String projectType = jobNameType.get('projectType')
        root.println("""
        🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢        
        PROJECT_TYPE ::::::: ${projectType}
        PROJECT_TEC_TYPE ::: ${projectTecType}
        COMPONENT :::::::::: ${component}
        🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢🏢  
        """)
        return getProjectInfo(projectType, projectTecType, component)
    }
}
