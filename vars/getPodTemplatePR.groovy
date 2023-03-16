def call() {
"""
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: jnlp
    env:
    - name: CONTAINER_ENV_VAR
      value: jnlp
  - name: maven
    image: maven:3.8.1-jdk-8
    command:
    - sleep
    args:
    - 99d
    env:
    - name: CONTAINER_ENV_VAR
      value: maven
  - name: busybox
    image: busybox
    command:
    - cat
    tty: true
    env:
    - name: CONTAINER_ENV_VAR
      value: busybox
"""
}
