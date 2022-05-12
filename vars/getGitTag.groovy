def call(String from_branch) {
        String gitCommit = sh(returnStdout: true, script: 'git rev-parse --short=8 HEAD').trim()
        int tagStatus = sh(returnStatus: true, script: "git describe --exact-match --match \"SBP_*\" --tags HEAD 2> /dev/null") as int
        String gitTag = ''
        if (tagStatus == 0) {
            gitTag = sh(returnStdout: true, script: "git describe --exact-match --match \"SBP_*\" --tags HEAD").trim()
            if ((gitTag.startsWith("SBP_JOY")) || (gitTag.startsWith("SBP_SynCDJoy"))) {
                println "Tag associated with commit: ${gitTag}"
            }
        } else {
            println 'No tag associated with commit'
        }
        println """
         FROM_BRANCH: ${from_branch}
         GITCOMMIT:   $gitCommit
        """
        return gitTag
}