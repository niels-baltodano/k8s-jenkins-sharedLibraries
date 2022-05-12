def call(String sourceBranch, String targetBranch){
    sh(label: "Automerge ${sourceBranch} ---> ${targetBranch}", script: """
        git config --global user.email "devsecops@devops.com.pe"
        git config --global user.name "devsecops"
        git checkout ${sourceBranch}
        git checkout ${targetBranch}
        git merge --no-ff ${sourceBranch}
    """)
}