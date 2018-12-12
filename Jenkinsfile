pipeline {
	agent any
	tools {
		jdk 'jdk8'
	}
	stages {
		stage ('Build') {
			steps {
				if (isUnix()) {
					sh 'mvn -Dmaven.test.failure.ignore=true package'
				} else {
					bat 'mvn -Dmaven.test.failure.ignore=true package'
				}
			}
			post {
				success {
					archive 'target/codedx-teamcity-plugin.jar'
				}
			}
		}
	}
}