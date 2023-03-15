def call() {
"""
metadata:
  namespace: default
  labels:
    app: jenkins-agent
spec:
  serviceAccountName: myjenkins
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
            memory: 1Gi
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
          memory: 1Gi
          cpu: 1024m
          ephemeral-storage: 1Gi
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
          memory: 1Gi
          cpu: 1024m
          ephemeral-storage: 1Gi
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
          memory: 1Gi
          cpu: 1024m
          ephemeral-storage: 1Gi
      securityContext:
        runAsUser: 0
      env:
       - name: GOOGLE_APPLICATION_CREDENTIALS
         value: /kaniko/.docker
      volumeMounts:
        - name: kaniko-secret
          mountPath: /kaniko/.docker
          readOnly: false
  volumes:
  - name: kaniko-secret                                 
    secret:
      secretName: pull-secret-docker-hub
      items:
      - key: .dockerconfigjson
        path: config.json
"""
}
