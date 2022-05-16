package com.pe.devops.defaults

import com.pe.devops.config.Config

class Defaults {
    static Boolean DEBUG = Config.get('common')['debug']

    static String GITHUB_CREDENTIAL = Config.get('common')['github']['credential']

}