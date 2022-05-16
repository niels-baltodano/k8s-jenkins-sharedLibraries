import com.pe.devops.repository.ProjectDataRepository
import com.pe.devops.repository.UtilsRepository


//JOB_NAME
Map getJobNameType() {
    init()
    UtilsRepository utilsRepository = new UtilsRepository()
    return utilsRepository.getJobNameType()
}

Map getProjectInfo(String urlProjectYaml){
    init()
    ProjectDataRepository projectDataRepository = new ProjectDataRepository(urlProjectYaml)
    return projectDataRepository.getDataMap()
}
void fetchCode(String sshCloneUrl, String branch){
    init()
    UtilsRepository repository = new UtilsRepository()
    repository.fetchCode(sshCloneUrl, branch)
}
void build(String cmdBuild){
    init()
    UtilsRepository repository = new UtilsRepository()
    repository.build(cmdBuild)
}
void pushKaniko(String baseImage, String tagImage){
    init()
    UtilsRepository repository = new UtilsRepository()
    repository.pushKaniko(baseImage, tagImage)
}