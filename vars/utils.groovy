#!/usr/bin/env groovy
import com.pe.devops.library.GradleLibrary
import com.pe.devops.repository.Approvers
import com.pe.devops.repository.ProjectDataRepository
import com.pe.devops.repository.SonarqubeRepository
import com.pe.devops.repository.UtilsRepository


String getKeyNameSonar(String appName, String sourceBranch, String targetBranch) {
    init()
    UtilsRepository utilsRepository = new UtilsRepository()
    return utilsRepository.getKeyNameSonar(appName, sourceBranch, targetBranch)
}
//JOB_NAME
Map getJobNameType() {
    init()
    UtilsRepository utilsRepository = new UtilsRepository()
    return utilsRepository.getJobNameType()
}
//frontend/node/affluent
Map getProjectInfo(String projectType, String projectTecType, String component){
    init()
    ProjectDataRepository projectDataRepository = new ProjectDataRepository(env.URL_PROJECTS_YAML_RAW)
    return projectDataRepository.getProjectInfo(projectType, projectTecType, component)
}
Map getProjectInfo(String urlProjectYaml){
    init()
    ProjectDataRepository projectDataRepository = new ProjectDataRepository(urlProjectYaml)
    return projectDataRepository.getDataMap()
}
void deleteProjetsSonar(){
    init()
    SonarqubeRepository sonarqubeRepository = new SonarqubeRepository()
    sonarqubeRepository.deleteProjectsSonar()
}
String getProjectFromSshUrl(String sshCloneUrl){
    init()
    UtilsRepository utilsRepository = new UtilsRepository()
    return utilsRepository.getProjectFromSshUrl(sshCloneUrl)
}

String getMembersSepByComa(String projectName){
    init()
    Approvers approvers = new Approvers(env.URL_RAW_FILE_WHITELIST)
    return approvers.getMembersSepByComa(projectName)
}
String singleQuote(String fortifyProject) {
    if(fortifyProject.contains(' ')){
        return "\'${fortifyProject}\'".trim()
    }
    return fortifyProject
}
String inputFile(Map param = [:]) {
    String message = param.get('message', 'Upload file')
    String filename = param.get('filename', 'data.txt')
    String stashName = param.get('stashName', filename)
    def filedata = null
    def inputFile = input(message: message, parameters: [file(name: filename)])
    filedata = inputFile
    writeFile(file: filename, encoding: 'Base64', text: filedata.read().getBytes().encodeBase64().toString())
    filedata.delete()
    String filePath = sh(label: "Exist ${filename} ?", returnStdout: true, script: "find * -type f -iname '${filename}'").trim()
    if (filePath.isEmpty()) {
        throw new Exception("El path del file ${filename} no fue encontrado")
    }
    String realPath = sh(label: "get realpath de ${filename} ", returnStdout: true, script: "realpath '${filename}'").trim()
    if (realPath.isEmpty()) {
        throw new Exception("El realpath ${filename} no fue encontrado")
    }
    return realPath
}

def proxyUrl(Map param = [:]) {
    def host = param.get('credential', 'latam-proxy.glb.lnm.bns')
    def port = param.get('port', '8000')
    def scheme = param.get('scheme', 'http')
    def user = param.get('user')
    def pass = param.get('pass')
    String response = """$scheme://$user:$pass@$host:$port"""
    return response
}

def getMapConexion(String name, String dominio, String user, String pass) {

    def conexion = [:]
    conexion.name = name
    conexion.host = dominio
    conexion.user = user
    conexion.password = pass
    conexion.allowAnyHosts = true

    //println("conexion return: $conexion")

    return conexion
}

def doGetFileSSH(Map remote = [:], String path, String file) {
    sshGet remote: remote, from: "$path/$file", into: '.', override: true
}

def doSearchLogRename(Map remote = [:], String path, String file) {

    doGetFileSSH(remote, path, file)
    sh "mv ${file} ${remote.host}_${file}"
}

def doZipLogAndUpload(String appName) {

    sh """
		mkdir Log_${appName}
		mv *.log Log_${appName}
		zip -r Log_${appName}.zip Log_${appName}
	"""

    archiveArtifacts(artifacts: "Log_${appName}.zip")
}

def doProcessStatus(String statusRunApp, String BUILD_URL, String appNameBase) {

    def message = ""
    def appName = appnamebase.toUpperCase()

    if (statusRunApp == 'NOT_RUNNING') {
        currentBuild.result = 'FAILURE'
        message = "[${appName}] <!here>, no se pudo completar el despliegue. Revisar reporte: ${BUILD_URL}${appName}-Deployment"
        currentBuild.description = "Despliegue Fallido"
        //utils.doSearchLogRename(remote,'/usr/IBM/WebSphere/AppServer/profiles/AppSrv03/logs/omwas1','SystemOut.log')
        //utils.doZipLogAndUpload('notification-api-pe')
    } else if (statusRunApp == 'RUNNING_PARTIALLY') {
        currentBuild.result = 'UNSTABLE'
        message = "[${appName}] <!here>, no se pudo completar el despliegue correctamente en todos los nodos. Revisar reporte: ${BUILD_URL}${appName}-Deployment"
        currentBuild.description = "Despliegue Fallido"
        //utils.doSearchLogRename(remote,'/usr/IBM/WebSphere/AppServer/profiles/AppSrv03/logs/omwas1','SystemOut.log')
        //utils.doZipLogAndUpload('notification-api-pe')
    } else {
        message = "[${appName}] <!here>, se finalizó satisfactoriamente despliegue en DESA :monkey_prank:"
        /*node('fomalhaut') {
            sh "echo ${lastCITag} > '/Users/fomalhaut/dadesa.txt'"
        }*/
    }
}

def replace_with_credentials(Map param = [:]) {
    def credential = param.get('credentialsId') //required
    GradleLibrary gradleLibrary = new GradleLibrary(credential);
    gradleLibrary.replace_with_credentials(param)
}

def replace_in_file(Map param = [:]) {
    GradleLibrary gradleLibrary = new GradleLibrary();
    gradleLibrary.replace_in_file(param)
}