def call() {
"""
apiVersion: v1
kind: Pod
metadata:
    name: jenkins-slave
spec:
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
    - name: kubectl
      image: bitnami/kubectl:1.21.12-debian-10-r25
      imagePullPolicy: Always
      env:
        - name: GIT_SSL_NO_VERIFY
          value: "true"
      command: ['cat']
      tty: true
      resources:
        requests:
          cpu: 10m
          memory: 256Mi
        limits:
          memory: 512Mi
          cpu: 100m
      securityContext:
        runAsUser: 0
"""
}
