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