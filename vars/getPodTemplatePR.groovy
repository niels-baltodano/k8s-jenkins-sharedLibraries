def call() {
"""
apiVersion: v1
kind: Pod
metadata:
    name: jenkins-slave
spec:
  serviceAccountName: pe-jenkins-jenkins
  containers:
    - name: jnlp
      image: jenkins/inbound-agent:4.11.2-4
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
            memory: 4Gi
    - name: maven
      image: maven:3.8.5-openjdk-11-slim
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
          ephemeral-storage: 2Gi
      securityContext:
        runAsUser: 0
    - name: node
      image: node:lts-stretch-slim
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
      securityContext:
        runAsUser: 0
    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      imagePullPolicy: Always   
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 200m
          memory: 512Mi
        limits:
          memory: 8Gi
          cpu: 1024m
          ephemeral-storage: 5Gi
      securityContext:
        runAsUser: 0
      volumeMounts:
        - name: jenkins-docker-cfg
          mountPath: /kaniko/.docker
          readOnly: false
  volumes:
  - name: jenkins-docker-cfg                                 
    secret:
      secretName: pull-secret-docker-hub
      items:
      - key: .dockerconfigjson
        path: config.json
"""
}