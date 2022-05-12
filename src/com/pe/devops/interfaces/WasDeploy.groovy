package com.pe.devops.interfaces

interface WasDeploy {
    Map getExistAppByName()
    void cleanPath()
    void doUninstall()
    /*LinkedHashMap getMapConexionConsoleWas(String env)
    String getWsAdminConexion(Map conexionConsoleWeb, String wsadminPath)
    boolean getStatusCheckServer(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doStartServer(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doStopServer(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doSynchronizeNode(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    String getStatusRunApp(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    String doParentLast(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doUninstall(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doInstall(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doInstallWar(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doStatusReady(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)
    void doStartApp(Map remote, String wsadminParam, Map jythonFileParams, String pathDevops)*/
}