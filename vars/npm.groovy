import com.pe.devops.repository.NPMLoginRepository

def call(){
    init()
    NPMLoginRepository npmLogin = new NPMLoginRepository()
    npmLogin.loginArtifactory()
}