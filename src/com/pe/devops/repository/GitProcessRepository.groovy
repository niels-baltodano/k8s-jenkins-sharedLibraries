package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults

class GitProcessRepository {
    static root = Script.root
    String credentialClone = Defaults.BITBUCKET_CREDENTIAL_CLONE

    void fetchCode(String from_branch, String ssh_clone_url) {
        root.println("""
        🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺
        SSH_CLONE_URL: ${ssh_clone_url}
        FROM_BRANCH: ${from_branch}
        🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺
        """)
        root.retry(2) {
            root.cleanWs()
            root.checkout(changelog: false, scm: [$class                           : 'GitSCM', branches: [[name: from_branch]],
                                                  doGenerateSubmoduleConfigurations: false,
                                                  extensions                       : [[$class: 'CheckoutOption', timeout: 1500],
                                                                                      [$class : 'CloneOption', depth: 1, noTags: false, reference: '',
                                                                                       shallow: false, timeout: 1500]],
                                                  submoduleCfg                     : [],
                                                  userRemoteConfigs                : [[credentialsId: this.credentialClone, url: ssh_clone_url]]])
        }
    }

    void autoMerge(String sourceBranch, String targetBranch) {
        root.sh(label: "Automerge ${sourceBranch} ---> ${targetBranch}", script: """
                git config --global user.email "devsecops@devops.com.pe"
                git config --global user.name "devsecops"
                git checkout ${sourceBranch}
                git checkout ${targetBranch}
                git merge --no-ff ${sourceBranch}
        """)
    }
}