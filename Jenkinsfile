pipeline {
	agent any
	tools {
		jdk 'jdk8'
	}
	stages {
		stage ('Build') {
			steps {
				bat 'mvn -Dmaven.test.failure.ignore=true package'
			}
			post {
				success {
					archive 'target/*.jar'
				}
			}
		}
	}
}