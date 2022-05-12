def call() {
    withCredentials([usernamePassword(credentialsId: 'ARTIFACTORY', usernameVariable: 'ARTIFACTORY_USER', passwordVariable: 'ARTIFACTORY_TOKEN')]) {
        sh """
        export GRADLE_OPTS="-Xmx4096m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"
        ./gradlew clean assemble --daemon \
            -Partifactory_user=${ARTIFACTORY_USER} \
            -Partifactory_password=${ARTIFACTORY_TOKEN}
        """
    }
    //\
    //            -Partifactory_contextUrl=https://af.cds.bns/artifactory
}