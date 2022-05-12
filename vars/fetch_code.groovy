def call(String from_branch, String ssh_clone_url, String credentialsId) {
   retry(2) {
            println """
                    🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺
                    SSH_CLONE_URL: ${ssh_clone_url}
                    FROM_BRANCH: ${from_branch}
                    🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺🍺
                    """
            cleanWs()
            checkout(changelog: false, scm: [$class: 'GitSCM', branches: [[name: from_branch]], doGenerateSubmoduleConfigurations: false,
            extensions: [[$class: 'CheckoutOption', timeout: 1500],
                        [$class: 'CloneOption', depth: 1, noTags: false, reference: '',
            shallow: false, timeout: 1500]],
            submoduleCfg: [],
            userRemoteConfigs: [[credentialsId: credentialsId, url: ssh_clone_url]]])

        }
}