package com.pe.devops.interfaces

interface GradleInterface {
    void replace_with_credentials(String credentialsId, Map replaces_values, String path_file)
    void build_project(String gradlecmd)
    //void applyCommand(Closure closure)
}