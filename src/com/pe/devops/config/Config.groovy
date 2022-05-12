package com.pe.devops.config

import com.cloudbees.groovy.cps.NonCPS

class Config {
    private static Map data = [:]

    static Map all(){
        return data
    }

    static set(String key, def value){
        data[key] = value
    }

    @NonCPS
    static get(String key){
        return data[key]
    }

    static nested(Map moreData) {
        mergeMaps(data, moreData)
    }

    static Map mergeMaps(Map lhs, Map rhs) {
        rhs.each { k, v ->
            lhs[k] = (lhs[k] in Map ? mergeMaps(lhs[k], v) : v)
        }
        return lhs
    }
}
