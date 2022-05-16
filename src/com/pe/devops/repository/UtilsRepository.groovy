package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.library.Utils
import com.pe.devops.defaults.Defaults
class UtilsRepository {
    static root = Script.root
    String credential = Defaults.GITHUB_CREDENTIAL
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

    void fetchCode(String sshCloneUrl, String branch) {
        root.println("""
        SSH_CLONE_URL :::: ${sshCloneUrl}
        BRANCH ::::::::::: ${branch}
        """)
        root.cleanWs()
        root.checkout([$class           : 'GitSCM', branches: [[name: branch]], extensions: [],
                       userRemoteConfigs: [[credentialsId: root.env.GIT_CREDENTIAL_ID, url: sshCloneUrl]]])
    }

    void build(String cmdBuild) {
        root.println("""
        BUILD :::: ${cmdBuild}
        """)
        root.sh(label: "Maven build",script: "${cmdBuild}")
    }
    void pushKaniko(String baseImage, String tagImage){
        if (!baseImage) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 BASE_IMAGE es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        if (!tagImage) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 VERSION_IMAGE es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
//        if (root.env.HTTP_PROXY_TEST) {
//            kanikoArgs.put('http_proxy', root.env.HTTP_PROXY_TEST)
//            kanikoArgs.put('https_proxy', root.env.HTTP_PROXY_TEST)
//        }
        def imageLabelPush = "${baseImage}:${tagImage}"
        //${kanikoBuildArgs} \
        //String kanikoBuildArgs = kanikoArgs.collect { k, v -> "--build-arg '${k}=${v}'" }.join(' ')
        def kanikoCmd = """
                /kaniko/executor --context . \
                --dockerfile "./Dockerfile" \
                --insecure \
                --skip-tls-verify \
                --destination "${imageLabelPush}"
        """
        root.println("""
            🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳
            IMAGE: ${imageLabelPush}
            🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀
        """)
        root.sh(label: "Push --> ${imageLabelPush}", script: kanikoCmd)
    }
    void kubectlPatch(String pathManifest, String appName, String finalTag) {
        if (!pathManifest) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 PATH_MANIFEST es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        if (!appName) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 APP_NAME es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        if (!finalTag) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 FINAL_TAG es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        appName = appName.replace('_', '-')

        def kubctlPatchCmd = """
                ls -lha
                kubectl patch \
                --local \
                -o yaml \
                -f ${appName}-deployment.yaml \
                -p 'spec:
                      template:
                        spec:
                          containers:
                          - name: ${appName}
                            image: ${finalTag}' \
                    > ${appName}-newdeployment.yaml
            mv ${appName}-newdeployment.yaml ${appName}-deployment.yaml
            cat -n ${appName}-deployment.yaml
            """
        root.dir(pathManifest) {
            root.sh(label: "Update ☸ ☸ ☸ ☸ File Deployment: ${appName} --> ${pathManifest}", script: kubctlPatchCmd)
        }
    }
    void pushKubectlPatch(String appName, String fromBranch, String finalTag) {
        if (!appName) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 APP_NAME es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        if (!fromBranch) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 BRANCH_CD es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        if (!finalTag) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 FINAL_TAG es null o vacia 🤨🤨🤨 🚩🚩🚩")
        }
        appName = appName.replace('_', '-')
        def pathManifest = "apps/pe-${appName}"
        root.println("""
            🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳🐳 
            PATH_MANIFESTS ::::: ${pathManifest}
            APP_NAME_SNZ :::::: ${appName}
            ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ ☸ 
            """)
        def commitMsg = """
                git config --global user.email "niels.baltodano@gmail.com"
                git config --global user.name "DevSecOps"
                git status --porcelain -s -b
                git add .
                git status --porcelain -s -b
                git commit -am 'feat(DevOps-123): 🐳🐳🐳 Se actualiza ${appName}-deployment.yaml desde el pipeline ➡️➡️  ${root.env.BUILD_URL} 🐳🐳🐳'
                git status --porcelain -s -b
            """
        def sshPushCmd = """
                #!/usr/bin/env bash
                set -x
                git push origin HEAD:${fromBranch}
           """
        root.sh(label: "Se agrega el commit DevOps-123", script: commitMsg)
        root.sshagent([this.credential]) {
            root.sh(label: "Push 🐳🐳🐳🐳 cambios en Deployment ${appName} --> ${pathManifest}", script: sshPushCmd)
        }
    }
}
