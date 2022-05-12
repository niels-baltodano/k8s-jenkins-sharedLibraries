import com.pe.devops.repository.RawFileRepository

def call(String url) {
    init()
    def rawFileRepository=  new RawFileRepository()
    rawFileRepository.getFile(url)
}
