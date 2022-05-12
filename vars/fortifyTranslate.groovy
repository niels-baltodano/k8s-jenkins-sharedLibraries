import com.pe.devops.repository.SASTRepository

def call(String fortify_project, String excludeList){
    init()
    SASTRepository sastRepository = new SASTRepository()
    sastRepository.fortifyTranslateGradle(fortify_project, excludeList)
}
