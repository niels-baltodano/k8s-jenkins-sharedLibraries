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
    - name: kubectl
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/kubectl:debug
      imagePullPolicy: Always
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 10m
          memory: 256Mi
        limits:
          memory: 512Mi
          cpu: 100m
    - name: kaniko-executor
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/kaniko-executor:debug
      imagePullPolicy: Always   
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 200m
          memory: 512Mi
        limits:
          memory: 15Gi
          cpu: 2048m
          ephemeral-storage: 25Gi
      securityContext:
        runAsUser: 0
      volumeMounts:
        - name: jenkins-docker-cfg
          mountPath: /kaniko/.docker
          readOnly: false
  volumes:
  - name: jenkins-agent-vol
    configMap:
      name: pe-jenkins-java-cacerts
  - name: jenkins-docker-cfg                                 
    secret:
      secretName: image-push-secret-nbrna-0109-anthosperu
      items:
      - key: .dockerconfigjson
        path: config.json
"""
}