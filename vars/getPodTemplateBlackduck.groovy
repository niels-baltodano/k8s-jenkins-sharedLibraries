def call() {
"""
apiVersion: v1
kind: Pod
metadata:
    name: jenkins-slave
    annotations:
        sidecar.istio.io/inject: "false"
spec:
  serviceAccountName: pe-jenkins-jenkins
  containers:
    - name: jnlp
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/jenkins-agent:v1
      imagePullPolicy: Always
      args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
      env:
        - name: GIT_SSL_NO_VERIFY
          value: "true"
      resources:
        requests:
            cpu: 10m
            memory: 256Mi
        limits:
            cpu: 512m
            memory: 3Gi
    - name: blackduck
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/sast/blackduck:6.9.1
      imagePullPolicy: Always
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 512m
          memory: 512Mi
        limits:
          memory: 4Gi
          cpu: 512m
      securityContext:
        runAsUser: 0
    - name: gradle
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/gradle-maven:jdk11-alpine
      imagePullPolicy: Always
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 512m
          memory: 512Mi
        limits:
          memory: 6Gi
          cpu: 1024m
      volumeMounts:
      - name: jenkins-agent-vol
        mountPath: /opt/java/openjdk/lib/security/cacerts
        subPath: cacerts  
    - name: net-core
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/netcore/sdk:3.1
      imagePullPolicy: Always   
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 10m
          memory: 256Mi
        limits:
          memory: 2Gi
          cpu: 512m    
    - name: node
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/node:14-alpine
      imagePullPolicy: Always   
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 100m
          memory: 256Mi
        limits:
          memory: 8Gi
          cpu: 1024m
          ephemeral-storage: 4Gi
  volumes:
  - name: jenkins-agent-vol
    configMap:
      name: pe-jenkins-java-cacerts
"""
}
