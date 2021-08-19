pipeline {
	agent {
		// basic build environment; only need Java and Maven
		label 'codebuild-small'
	}

	stages {
		stage('Build plugin') {
			steps {
				withCache(name: 'codedx-teamcity-cache', baseFolder: env.HOME, contents: '.m2') {
					sh 'mvn clean package'
				}
			}

			post {
				success {
					archiveArtifacts artifacts: 'target/codedx-teamcity-plugin.zip', fingerprint: true, onlyIfSuccessful: true
				}
			}
		}
	}

	post {
		failure {
			script {
				slack.error 'TeamCity Plugin build FAILED'
			}
		}
	}
}
