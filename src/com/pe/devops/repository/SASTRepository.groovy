package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.Utils

class SASTRepository {
    static root = Script.root
    String fortifyServer = Defaults.SAST_FORTIFY_SERVER
    String blackduckServer = Defaults.SAST_BLACKDUCK_SERVER
    String blackduckCredential = Defaults.SAST_BLACKDUCK_CREDENTIAL
    String fortifycredentialGradle = Defaults.SAST_FORTIFY_CREDENTIAL_GRADLE
    String fortifycredential = Defaults.SAST_FORTIFY_CREDENTIAL
    Utils util = new Utils()

    void fortifyTranslateGradle(String fortify_project, String excludeList) {
        applyCommandByCredential(this.fortifycredentialGradle, { Map data ->
            def gradle_tasks = "clean assemble --no-daemon -Partifactory_user=${data.username} -Partifactory_password=${data.password}".trim()
            excludeList = excludeList.trim().split(",").collect { i -> "-exclude $i" }.join(" ")
            def sourceanalyzer_cmd = "sourceanalyzer -b \"${fortify_project}\" -logfile \"${fortify_project}-translate.log\" ${excludeList} ./gradlew ${gradle_tasks}"
            root.sh(label: "Translate --> ${fortify_project}", script: sourceanalyzer_cmd)
        })
    }

    void fortifyTranslateNode(String fortify_project, String include, Map exclude) {
        //Se eliminan los archivos de exclude
        String patternFolder
        String patternFile
        try{
            patternFolder = exclude.get('folder', '')
            patternFile = exclude.get('file', '')
        }catch(e){
            throw new Exception('🚩🚩🚩El patron de files include/exclude es nulo o vacio revisar en projects.yaml --> sast.fortify... 🚩🚩🚩')
        }
        //validamos los patrones
        if (!patternFolder) {
            throw new Exception('🚩🚩🚩El patron de folder a excluir es nulo o vacio revisar en projects.yaml --> sast.fortify.exclude.folder 🚩🚩🚩')
        }
        if (!patternFile) {
            throw new Exception('🚩🚩🚩El patron de files a excluir es nulo o vacio revisar en projects.yaml --> sast.fortify.exclude.file 🚩🚩🚩')
        }
        List listExcludePatternFolder = patternFolder.split(',')
        List listExcludePatternFile = patternFile.split(',')
        listExcludePatternFolder.each {
            root.sh(label: "Delete folder 📁📁📁 with pattern: ${it}", script: "find ${root.env.WORKSPACE}/src -type d -iname \"${it}\" -exec rm -rf {} +")
        }
        listExcludePatternFile.each {
            root.sh(label: "Delete file 📓📓📓 with pattern: ${it}", script: "find ${root.env.WORKSPACE}/src -type f -iname \"${it}\" -exec rm -rf {} +")
        }
        root.sh(label: "Check deleted files", script: """
                                                git status --porcelain -s -b
                                                tree -I 'src/test|src/integrationTest|gradle|node_modules|cache|test_*'
                                            """)
        //validamos
        if (!include) {
            throw new Exception('🚩🚩🚩El patron de files a incluir es nulo o vacio revisar en projects.yaml --> sast.fortify.include 🚩🚩🚩')
        }
        List listIncludeFilesPattern = include.split(',')
        String listFilesInclude = ''
        listIncludeFilesPattern.each {
            String files = root.sh(label: "Concat files with pattern: ${it}", returnStdout: true, script: "find ${root.env.WORKSPACE}/src -type f -name \"${it}\" | paste -sd \" \"").trim()
            if (!files) {
                throw new Exception("🚩🚩🚩 😡😡😡 No se encontraron archivos con el patron: ${it}, revisar patron de archivos en --> sast.fortify.include... 😡😡😡 🚩🚩🚩")
            }
            listFilesInclude += " ${files}"
        }
        if (!listFilesInclude) {
            throw new Exception("🚩🚩🚩 🤨🤨🤨 No se encontraron archivos que analizar con el patron: ${include} 🤨🤨🤨 🚩🚩🚩")
        }
        String excludeSourceAnalyzer = listExcludePatternFile.collect { i -> "-exclude ./src/${i}" }.join(" ")
        root.sh(label: "🚀🚀🚀 Execute Translate Project: ${fortify_project} 🚀🚀🚀", script: "sourceanalyzer -b \"${fortify_project}\" -64 -Xmx8G ${listFilesInclude} -logfile \"${fortify_project}.log\" ${excludeSourceAnalyzer}")
    }

    void fortifyUpload(String fortify_project, String application_version) {
        applyCommandByCredential(this.fortifycredential, { Map data ->
            def cmd = """
                fortifyclient -url ${this.fortifyServer} uploadFPR -f \"${fortify_project}.fpr\" \
                -application \"${fortify_project}\" \
                -applicationVersion ${application_version} \
                -user ${data.username} \
                -password "${data.password}"
               """
            root.sh(label: "Upload Fortify Project: ${fortify_project}, version: ${application_version} --> ${this.fortifyServer}", script: cmd)
        })
    }

    void blackduckScan(String detect_timeout, String blackduck_project, String blackduck_version, boolean isApproved = false) {
        def blackduck_phase = ''
        try {
            if (blackduck_version == 'BRANCH-master') {
                blackduck_phase = 'PRERELEASE'
            } else if (blackduck_version == 'BRANCH-pull-request') {
                blackduck_phase = 'DEVELOPMENT'
            }
        } catch (e) {
            echo 'BlackDuck project version not recognized'
        }
        root.println(
                """
        🚀::::::blackduck_phase::::::🚀 
            ${blackduck_phase}
            ${this.blackduckCredential}
        🚀::::::blackduck_phase::::::🚀 
        """
        )
        applyCommand(this.blackduckCredential, { Map data ->
            List blackduck_prop = ["blackduck.url=${this.blackduckServer}",
                                   "detect.timeout=${detect_timeout}",
                                   "blackduck.trust.cert=true",
                                   "insecure",
                                   "detect.java.path=/opt/java/openjdk/bin/java",
                                   "detect.code.location.name=${blackduck_project}-${blackduck_version}",
                                   "detect.project.name=${blackduck_project}",
                                   "detect.project.version.name=${blackduck_version}",
                                   "detect.project.version.phase=${blackduck_phase}",
                                   "detect.detector.buildless=true",
                                   "detect.policy.check.fail.on.severities=BLOCKER",
                                   "detect.source.path=${root.env.WORKSPACE}",
                                   "detect.wait.for.results=true",
                                   "detect.risk.report.pdf.path=${root.env.WORKSPACE}",
                                   "logging.level.detect=INFO",
                                   "detect.cleanup=true",
                                   "detect.risk.report.pdf=true",
                                   "blackduck.api.token=${data.token}"]
            if (isApproved) {
                blackduck_prop.push("detect.force.success=true")
            }
            def str = blackduck_prop.collect { i -> "--$i \\" }.join("\n")
            def str_final = """
            java  -jar "/blackduck/synopsys-detect.jar" \
            ${str}
            """.trim()
            root.println(str_final)
            root.sh(label: "BlackDuck --> ${blackduck_project} with isApproved : ${isApproved}", script: str_final)
        })// closure
    }


    void applyCommand(String credential, Closure closure) {
        root.withCredentials([root.string(credentialsId: credential, variable: 'token')]) {
            closure(token: root.env.token)
        }
    }

    void applyCommandByCredential(String credential, Closure closure) {
        root.withCredentials([root.usernamePassword(credentialsId: credential, usernameVariable: 'username', passwordVariable: 'password')]) {
            closure(username: root.env.username,
                    password: root.env.password)
        }
    }
}
