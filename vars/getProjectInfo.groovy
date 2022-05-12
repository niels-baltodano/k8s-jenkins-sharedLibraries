import com.pe.devops.repository.ProjectDataRepository

def call(String urlProjectYaml) {
    init()
    ProjectDataRepository repository = new ProjectDataRepository(urlProjectYaml)
    return repository.getDataMap()
}