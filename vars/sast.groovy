#!/usr/bin/env groovy
import com.pe.devops.repository.SASTRepository

void blackduckScan(String detect_timeout, String blackduck_project, String blackduck_version, boolean isApproved=false){
    init()
    SASTRepository sastRepository = new SASTRepository()
    sastRepository.blackduckScan(detect_timeout, blackduck_project, blackduck_version, isApproved)
}
void fortifyUpload(String fortify_project, String application_version){
    init()
    SASTRepository sastRepository = new SASTRepository()
    sastRepository.fortifyUpload(fortify_project,application_version)
}
void fortifyTranslateNode(String fortify_project, String include, Map exclude){
    init()
    SASTRepository sastRepository = new SASTRepository()
    sastRepository.fortifyTranslateNode(fortify_project, include, exclude)
}