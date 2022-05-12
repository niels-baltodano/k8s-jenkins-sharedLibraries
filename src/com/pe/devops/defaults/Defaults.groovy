package com.pe.devops.defaults

import com.pe.devops.config.Config

class Defaults {
    static Boolean DEBUG = Config.get('common')['debug']
    static String CONFLUENCE_SERVER = Config.get('common')['confluence']['server']
    static String CONFLUENCE_CREDENTIAL = Config.get('common')['confluence']['credential']
    //Bitbucket
    static String BITBUCKET_SERVER = Config.get('common')['bitbucket']['server']
    static String BITBUCKET_CREDENTIAL = Config.get('common')['bitbucket']['credential']
    static String BITBUCKET_CREDENTIAL_CLONE = Config.get('common')['bitbucket']['credential_clone']
    //Gradle
    static String GRADLE_CMD="gradle_cms"
    //Jira
    static String JIRA_SERVER = Config.get('common')['jira']['server']
    static String JIRA_CREDENTIAL = Config.get('common')['jira']['credential']
    //artifactory-npm
    static String ARTIFACTORY_SERVER_NPM = Config.get('common')['artifactory-npm']['server']
    static String ARTIFACTORY_SERVER_NPM_CREDENTIAL = Config.get('common')['artifactory-npm']['credential']
    //sast
    static String SAST_FORTIFY_SERVER = Config.get('common')['sast']['fortify']['server']
    static String SAST_FORTIFY_CREDENTIAL_GRADLE = Config.get('common')['sast']['fortify']['credential_artifactory_gradle']
    static String SAST_FORTIFY_CREDENTIAL = Config.get('common')['sast']['fortify']['credential']
    //blackduck
    static String SAST_BLACKDUCK_SERVER = Config.get('common')['sast']['blackduck']['server']
    static String SAST_BLACKDUCK_CREDENTIAL = Config.get('common')['sast']['blackduck']['credential']
    //sonarqube
    static String SONARQUBE_SERVER = Config.get('common')['sast']['sonarqube']['server']
    static String SONARQUBE_CREDENTIAL = Config.get('common')['sast']['sonarqube']['credential']
}