#!/usr/bin/env groovy
import com.pe.devops.config.LoadConfig

def call(Map data = [:]) {
    def config = data.get('config')
    LoadConfig loadConfig = LoadConfig.getInstance(this)

    if (config) {
        loadConfig.nested(config)
    }
}