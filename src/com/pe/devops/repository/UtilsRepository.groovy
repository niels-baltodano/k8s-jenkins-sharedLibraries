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
                GOOGLE_APPLICATION_CREDENTIALS=/kaniko/.docker
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
}
