package com.pe.devops.repository

import com.pe.devops.Script
import com.pe.devops.defaults.Defaults
import com.pe.devops.library.CurlHttpClient


class JiraRepository {
    static root = Script.root
    String server = Defaults.JIRA_SERVER
    String credential = Defaults.JIRA_CREDENTIAL
    CurlHttpClient client = new CurlHttpClient(server: this.server)

    Map getInfo(String jiraTicket){
        Map responseJson	= [:]
        applyCommand { Map data ->
            responseJson = new CurlHttpClient(server: this.server).get(
                    username: data.username,
                    password: data.password,
                    path: "/rest/api/2/issue/${jiraTicket}",
                    insecure: [s: true],
                    headers: [
                            'Content-Type': 'application/json'
                    ]
            )
        }
        return responseJson
    }

    void updateInfo(String jiraTicket, String transitionID="41", String scmCode, String commitHeadID=""){
        Map data_info = [
                transition: [id: "${transitionID}"]
        ]
        applyCommand { Map data ->
            this.client.post(
                    username: data.username,
                    password: data.password,
                    path: "/rest/api/2/issue/${jiraTicket}/transitions",
                    insecure: [s: true],
                    headers: [
                            'Content-Type': 'application/json'
                    ],
                    data: data_info
            )
        }//fin closure

        if(transitionID == "111"){
            Map data_json = [:]
            Map responseJson = [:]
            data_json =[
                    fields: [customfield_11306: "${scmCode}"]
            ]
            applyCommand { Map data ->
                responseJson = this.client.get(
                        username: data.username,
                        password: data.password,
                        path: "/rest/api/2/issue/${jiraTicket}",
                        headers: [
                                'Content-Type': 'application/json'
                        ]
                )
            }//fin closure
            def ticket = responseJson.get("fields").get("customfield_11306")
            if (!ticket){
                applyCommand { Map data ->
                    this.client.put(
                            username: data.username,
                            password: data.password,
                            path: "/rest/api/2/issue/${jiraTicket}",
                            headers: [
                                    'Content-Type': 'application/json'
                            ],
                            data: data_json
                    )
                }//fin closure
            }
            def commitDescription = "Commit Head ID: ${commitHeadID}"
            def ticket_desc = (responseJson.get("fields").get("description")).trim().replace("{", "").replace("}", "").replace("*", "").replaceAll("(?m)^[ \t]*\r?\n", "")
            def description = "${ticket_desc} ${commitDescription}"
            def payload = [
                    fields: [description: "${description}"
            ]]
            applyCommand { Map data ->
                this.client.put(
                        username: data.username,
                        password: data.password,
                        path: "/rest/api/2/issue/${jiraTicket}",
                        headers: [
                                'Content-Type': 'application/json'
                        ],
                        data: payload
                )
            }//fin closure
        }//fin if

    }

    void applyCommand(Closure closure) {
        root.withCredentials([
                root.usernamePassword(credentialsId: this.credential, usernameVariable: 'username', passwordVariable: 'password')
        ]) {
            closure(
                    username: root.env.username,
                    password: root.env.password
            )
        }
    }

    Map updateTicketStatus(String jiraTicket, String transitionID){
        Map data_info = [
                transition: [id: "${transitionID}"]
        ]
        applyCommand { Map data ->
            this.client.post(
                    username: data.username,
                    password: data.password,
                    path: "/rest/api/2/issue/${jiraTicket}/transitions",
                    insecure: [s: true],
                    headers: [
                            'Content-Type': 'application/json'
                    ],
                    data: data_info
            )
        }//fin closure



        if(transitionID == "101"){ // 101 DEPLOYED ON UAT-DF
            Map data_json = [:]
            Map responseJson = [:]
            data_json =[
                    body: "Desplegado a UAT-DF  CC:[~s7859219]"
            ]
            applyCommand { Map data ->
                responseJson = this.client.post(
                        username: data.username,
                        password: data.password,
                        path: "/rest/api/2/issue/${jiraTicket}/comment",
                        headers: [
                                'Content-Type': 'application/json'
                        ],
                        data: data_json
                )
            }//fin closure
            return responseJson

        }//fin if

        if(transitionID == "91"){ // 91 DEPLOYED ON UAT-TI
            Map data_json1 = [:]
            Map responseJson1 = [:]
            data_json1 =[
                    body: "Desplegado a UAT-TI  CC:[~s7859219]"
            ]
            applyCommand { Map data ->
                responseJson1 = this.client.post(
                        username: data.username,
                        password: data.password,
                        path: "/rest/api/2/issue/${jiraTicket}/comment",
                        headers: [
                                'Content-Type': 'application/json'
                        ],
                        data: data_json1
                )
            }//fin closure
            return responseJson1
        }//fin if

        if(transitionID == "131"){ //131 DEPLOYED ON PROD 2
            Map data_json2 = [:]
            Map responseJson2 = [:]
            data_json2 =[
                    body: "Desplegado a en prod  CC:[~s7859219]"
            ]
            applyCommand { Map data ->
                responseJson2 = this.client.post(
                        username: data.username,
                        password: data.password,
                        path: "/rest/api/2/issue/${jiraTicket}/comment",
                        headers: [
                                'Content-Type': 'application/json'
                        ],
                        data: data_json2
                )
            }//fin closure
            return responseJson2

        }//fin if

    }



    Map getStatusInfo(String jiraTicket){
        Map responseJson	= [:]
        applyCommand { Map data ->
            responseJson = new CurlHttpClient(server: this.server).get(
                    username: data.username,
                    password: data.password,
                    path: "/rest/api/2/issue/${jiraTicket}",
                    insecure: [s: true],
                    headers: [
                            'Content-Type': 'application/json'
                    ]
            )
        }
        return responseJson

        // Map responseJson_check = [:]
        // applyCommand { Map data ->
        // responseJson_check = this.client.get(
        //         username: data.username,
        //         password: data.password,
        //         path: "/rest/greenhopper/1.0/xboard/work/allData.json?rapidViewId=41374&selectedProjectKey=DBSCAL",
        //         headers: [
        //                 'Content-Type': 'application/json'
        //         ]
        // )
        // }//fin closure
        // return responseJson_check




    }

    Map getChangelog(String jiraTicket){
        Map responseJson_check = [:]
        applyCommand { Map data ->
        responseJson_check = this.client.get(
                username: data.username,
                password: data.password,
                path: "/rest/api/2/issue/${jiraTicket}?expand=changelog",
                insecure: [s: true],
                headers: [
                        'Content-Type': 'application/json'
                ]
        )
        }//fin closure
        return responseJson_check


   }







}//fin class JiraRepository


