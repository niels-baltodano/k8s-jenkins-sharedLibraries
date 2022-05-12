package com.pe.devops.library

class Utils {
    String getProtocolFile(File file) {
        String result
        try {
            result = file.toURI().toURL().getProtocol()
        } catch (Exception e) {
            result = 'unknow file'
        }
        return result
    }

    String getProtocol(String urlOrPathProjectYaml) {
        if (!urlOrPathProjectYaml) {
            throw new Exception('🚩🚩🚩 File config/projects.yaml o variable de entorno de Jenkins URL_PROJECTS_YAML_RAW esta vacia o no existe 🚩🚩🚩')
        }
        String protocol
        try {
            final URI uri = new URI(urlOrPathProjectYaml)
            if (uri.isAbsolute()) {
                protocol = uri.getScheme()
            } else {
                URL url = new URL(urlOrPathProjectYaml)
                protocol = url.getProtocol()
            }
        } catch (Exception e) {
            if (urlOrPathProjectYaml.startsWith("//")) {
                throw new IllegalArgumentException("Relative context: ${urlOrPathProjectYaml}")
            } else {
                File file = new File(urlOrPathProjectYaml)
                protocol = getProtocolFile(file)
            }
        }
        return protocol
    }

    String getKeyNameSonar(String appName, String sourceBranch, String targetBranch) {
        String key_name = (sourceBranch.equals('master')) ? '' : sourceBranch.replace('/', '-')
        String key_name_branch = (key_name.isEmpty()) ? appName : "${appName}-BRANCH-${key_name}"
        return key_name_branch
    }

    Map getJobNameType(String jobName) {
        assert jobName != null
        List jobDataSplit = jobName.split('/')
        Map jobDetails = [
                component     : jobDataSplit.last(),
                projectTecType: jobDataSplit[-2],
                projectType   : jobDataSplit[-3]
        ]
        return jobDetails
    }
    /**
     * Getting ProjectName from SSH_URL_CLONE
     * @param sshCloneUrl string
     * @return projectName in uppercase
     */
    String getProjectFromSshUrl(String sshCloneUrl) {
        assert sshCloneUrl != null
        List urlsplitted = sshCloneUrl.split('/')
        String git = urlsplitted.last()
        String shortUrl = sshCloneUrl - git
        List shortUrlSplit = shortUrl.split('/')
        String project = (shortUrlSplit.last()).trim()
        return project.toLowerCase()
    }
    /**
     * Getting String from List members approvers
     * @param members List
     * @return membersSepComa separated by comma
     */
    String getMembersSepByComa(List members) {
        List approvers = []
        members.each {
            approvers.push(it.key)
        }
        String membersSepComa = approvers.collect { it -> "${it}" }.join(',')
        return membersSepComa.trim()
    }
}
