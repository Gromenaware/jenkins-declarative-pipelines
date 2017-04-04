pipeline {
    agent any
    tools {
        maven "maven_jenkins"
        jdk "java_jenkins"
    }
    environment {
        saucelabsCredentialId = '8b819d9d-0298-4f8f-9430-6d44e87ac7b5'
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'git@github.com:Gromenaware/corball-test-automation-demo.git'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -Dtest=WebDriverTest -Dbrowser=$BROWSER -Dhub=http://selgp:8b819d9d-0298-4f8f-9430-6d44e87ac7b5@ondemand.saucelabs.com:80/wd/hub test'
            }
        }
    }
    post {
        success {
            emailext (
                    subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}""",
                    to: 'guillem@gromenaware.com'
            )

        }
        failure {
            emailext (
                    subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}""",
                    to: 'guillem@gromenaware.com'
            )
        }
        always {
            step([$class: 'XUnitBuilder',
                  thresholds: [
                          [$class: 'SkippedThreshold', failureThreshold: '0'],
                          // Allow for a significant number of failures
                          // Keeping this threshold so that overwhelming failures are guaranteed
                          //     to still fail the build
                          [$class: 'FailedThreshold', failureThreshold: '10']],
                  tools: [[$class: 'JUnitType', pattern: 'target/surefire-reports/junitreports/**']]])

            saucePublisher()
        }
    }
}