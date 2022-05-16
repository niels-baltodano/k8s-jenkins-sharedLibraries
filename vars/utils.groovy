import com.pe.devops.repository.ProjectDataRepository

Map getProjectInfo(String urlPathYaml) {
    ProjectDataRepository projectRepository = new ProjectDataRepository(urlPathYaml)
    return projectRepository.getProjectInfo()
}