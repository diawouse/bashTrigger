#!/usr/bin/env groovy

node {
    def mvnHome = ""
    def mvnVersion = "Maven3"
    def mvnConfigId = "88c76799-4e5b-4cf8-aa5b-f24dfcdc3d24"
	
    def rolesDir = "roles"
    def backFloatingIp= "213.32.114.203"

	
    stage('Installing tools') {
        parallel(
                " Installing Maven ": {
                    mvnHome = tool mvnVersion
                }
        )
    }

    stage('Clone project from Bitbucket') {
        // Wipe the workspace so we are building completely clean
        deleteDir()
        checkout scm
    }

    stage('Build back service') {
        ansiColor('xterm') {
                configFileProvider([configFile(fileId: mvnConfigId, variable: 'MAVEN_SETTINGS')]) {
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh 'export JAVA_HOME=$JAVA_HOME && mvn -s "$MAVEN_SETTINGS" clean deploy -B'
                    }
                }
        }
    }

	stage('Deploy on tenant') {
	    sleep 20
	        dir(rolesDir) {
                sshagent (credentials: ['analytics-test-ubuntu']) {
					sh "ansible-playbook --user=ubuntu --ssh-extra-args='-o StrictHostKeyChecking=no  -o UserKnownHostsFile=/dev/null  -o ProxyCommand=\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -W %h:%p ubuntu@$backFloatingIp\"' -i '$backFloatingIp,' deploy-bashTrigger.yml --become -vv"
                }
			}
    }
}
