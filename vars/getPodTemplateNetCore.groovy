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
    - name: kaniko-executor
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/kaniko-executor:debug
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
          ephemeral-storage: 4Gi
      securityContext:
        runAsUser: 0
      volumeMounts:
        - name: jenkins-docker-cfg
          mountPath: /kaniko/.docker
          readOnly: false
    - name: sonar-scanner-net
      image: northamerica-northeast2-docker.pkg.dev/nbrna-0109-anthosperu-cfc55dcf/pe-artifacts/ci/sonar-scanner-net:v1
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
  volumes:
  - name: jenkins-docker-cfg                                 
    secret:
      secretName: image-push-secret-nbrna-0109-anthosperu
      items:
      - key: .dockerconfigjson
        path: config.json
"""
}
