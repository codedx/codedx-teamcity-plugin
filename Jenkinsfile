pipeline {
	agent any

	parameters {
		string(defaultValue: '', description: 'Leave this blank for snapshot builds', name: 'RELEASE_VERSION')
	}

	tools {
		jdk 'jdk8'
	}

	stages {
		stage ('Build') {
			steps {
				script {
					if (isUnix()) {
						sh 'mvn -Dmaven.test.failure.ignore=true package'
					} else {
						bat 'mvn -Dmaven.test.failure.ignore=true package'
					}
				}
			}
			post {
				success {
					archiveArtifacts artifacts: 'target/codedx-teamcity-plugin.zip'
				}
			}
		}
	}
}