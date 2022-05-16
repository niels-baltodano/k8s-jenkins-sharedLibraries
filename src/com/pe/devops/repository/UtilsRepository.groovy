package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.library.Utils

class UtilsRepository {
    static root = Script.root
    Utils util = new Utils()

    String getKeyNameSonar(String appName, String sourceBranch, String targetBranch) {
        return this.util.getKeyNameSonar(appName, sourceBranch, targetBranch)
    }

    Map getJobNameType() {
        return this.util.getJobNameType(root.env.JOB_NAME)
    }

    String getProjectFromSshUrl(String sshCloneUrl) {
        return this.util.getProjectFromSshUrl(sshCloneUrl)
    }

    String getMembersSepByComa(List members) {
        return this.util.getMembersSepByComa(members)
    }
    void fetchCode(String sshCloneUrl, String branch){
        root.cleanWs()
        root.checkout([$class: 'GitSCM', branches: [[name: branch]], extensions: [],
                        userRemoteConfigs: [[credentialsId: root.env.GIT_CREDENTIAL_ID, url: sshCloneUrl]]])
    }

}
