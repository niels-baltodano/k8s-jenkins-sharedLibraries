package com.pe.devops.library


import com.pe.devops.Script
import groovy.json.JsonOutput

import groovy.json.JsonSlurperClassic

class CurlHttpClient {
    static root = Script.root
    String server

    Map get(Map params) {
        params.put('request', 'GET')
        return generate(params)
    }

    Map post(Map params) {
        params.put('request', 'POST')
        return generate(params)
    }

    Map put(Map params) {
        params.put('request', 'PUT')
        return generate(params)
    }

    Map delete(Map params) {
        params.put('request', 'DELETE')
        return generate(params)
    }

    private Map generate(Map params) {
        def request = params.get('request', 'GET')
        def username = params.get('username')
        def password = params.get('password')
        def path = params.get('path')
        Map query = params.get('query', [:])
        Map headers = params.get('headers', [:])
        Map form = params.get('form', [:])
        Map data = params.get('data', [:])
        Map silent = params.get('silent', [:])
        Map insecure = params.get('insecure', [:])
        Map file = params.get('file', [:])
        def raw = params.get('raw')
        def url = this.server + path + (query ? "?" + queryStr(query) : "")
        def result = "curl --request ${request}"

        if (raw) {
            result += " -LJO "
        }
        if (username && password) {
            result += """ --user \"${username}:${password}\""""
        }

        if (headers) {
            result += " " + headerStr(headers)
        }

        if (form) {
            result += " --form '" + formStr(form) + "'"
        }

        if (data) {
            result += " --data '" + dataToStr(data, headers) + "'"
        }
        if (silent) {
            result += " --silent "
        }
        if (insecure) {
            result += " --insecure "
        }
        result += " --url '${url}'"

        if(file){
            result += " -o ${file.name}"
        }
        def responseStr = root.sh(label: "Curl url: ${url}",returnStdout: true, script: result).trim()

        if (com.pe.devops.defaults.Defaults.DEBUG) {
            root.println "Response: ${responseStr}"
        }

        return jsonParse(responseStr)
    }

    private static String queryStr(Map params) {
        return params.findAll { it.value != null }.collect { k, v -> "$k=$v" }.join('&')
    }

    private static String headerStr(Map params) {
        if (!params) return null
        return params.collect { k, v -> "--header '$k: $v'" }.join(' ')
    }

    private static String formStr(Map params) {
        if (!params) return null
        return params.collect { k, v -> "$k=$v" }.join(';')
    }

    private static String dataToStr(Map data = [:], Map headers = [:]) {
        if (!data) return null
        def contentType = headers.containsKey('Content-Type') ? headers['Content-Type'] : null
        def response = null

        switch (contentType) {
            case 'application/json': response = JsonOutput.toJson(data); break
            default: response = data.collect { k, v -> "$k=$v" }.join('&'); break
        }
        return response
    }

    static Map jsonParse(def json) {
        try {
            return new JsonSlurperClassic().parseText(json) as Map
        } catch (err) {
            return json
        }
    }
}