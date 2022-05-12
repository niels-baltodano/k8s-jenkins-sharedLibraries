package com.pe.devops.repository

import com.pe.devops.Script

class K8sPodTemplateYaml {
    static root = Script.root
    String path_tpl

    K8sPodTemplateYaml(String path_tpl) {
        this.path_tpl = path_tpl
    }

    String getPath_tpl() {
        return path_tpl
    }

    void setPath_tpl(String path_tpl) {
        this.path_tpl = path_tpl
    }

    String getPodTemplate() {
       String fileContent
       root.println("Println fullpath")
       def req = root.libraryResource(getPath_tpl())
       File file = new File(req)
       fileContent = file.getText("UTF-8")
       return fileContent.trim()
    }
}
