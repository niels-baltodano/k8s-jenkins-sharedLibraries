def call(def application_name, def application_version) {
    script {
        container('fortify-scanner') {
            withCredentials([usernamePassword(credentialsId: 'devops_fortify', usernameVariable: 'FORTIFY_USERNAME', passwordVariable: 'FORTIFY_PASSWORD')]) {
                sh """
                fortifyclient -url https://ssc.appsec.bns/ssc uploadFPR -f "${application_name}.fpr" \
                -application "${application_name}" \
                -applicationVersion ${application_version} \
                -user ${FORTIFY_USERNAME} \
                -password "${FORTIFY_PASSWORD}"
               """
            }
        }
    }
}
