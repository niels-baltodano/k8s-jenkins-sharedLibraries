def call() {
"""
apiVersion: v1
kind: Pod
metadata:
  namespace: default
  labels:
    job: bootvar-build-pod
spec:
  containers:
  - name: bootvar-container
    image: alpine:latest
    tty: true
    command: ['cat']
"""
}
