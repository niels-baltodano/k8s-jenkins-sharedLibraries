package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.interfaces.GradleInterface
import com.pe.devops.library.GradleLibrary

class GradleRepository implements GradleInterface{
    static root = Script.root
    GradleLibrary gradleLibrary = new GradleLibrary()

    void replace_with_credentials(String credentialsId, Map replaces_values, String path_file) {
        gradleLibrary.replace_with_credentials(credentialsId, replaces_values, path_file)
    }


    void build_project(String gradlecmd) {
        gradleLibrary.build_project(gradlecmd)
    }
}
