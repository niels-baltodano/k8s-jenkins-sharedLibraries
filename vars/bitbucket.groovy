#!/usr/bin/env groovy
import com.pe.devops.repository.BitbucketRepository
import com.pe.devops.repository.GitProcessRepository

void bitbucketPR(String state, String description, String bitbucketPayload){
    init()
    BitbucketRepository bitbucketrepo =  new BitbucketRepository()
    bitbucketrepo.updatePR(state, description, bitbucketPayload)
}
void fetchCode(String from_branch, String ssh_clone_url){
    init()
    GitProcessRepository gitProcessRepository =  new GitProcessRepository()
    gitProcessRepository.fetchCode(from_branch, ssh_clone_url)
}
void autoMerge(String sourceBranch, String targetBranch){
    init()
    GitProcessRepository gitProcessRepository =  new GitProcessRepository()
    gitProcessRepository.autoMerge(sourceBranch, targetBranch)
}