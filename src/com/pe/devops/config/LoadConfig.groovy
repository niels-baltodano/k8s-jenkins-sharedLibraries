package com.pe.devops.config

import com.pe.devops.Script

class LoadConfig {
    private static LoadConfig instance

    private static init(def root) {
        Script.root = root
        Map newData = root.readYaml(text: root.libraryResource('com/pedevops/config/common.yml'))
        Config.nested(newData)
    }

    static void nested(Map moreData) {
        Config.nested(moreData)
    }

    static void nested(String configPath) {
        Map newData = Script.root.readYaml(file: configPath)
        Config.nested(newData)
    }

    static getInstance(def root) {
        if (instance == null) {
            init(root)
            instance = new LoadConfig();
        }
        return instance;
    }
}
