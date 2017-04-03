pipeline {
    agent any
    tools {
        maven "maven_jenkins"
        jdk "java_jenkins"
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'git@github.com:Gromenaware/corball-test-automation-demo.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn compile -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -Dtest=WebDriverTest -Dbrowser=chrome -Dhub=http://selgp:8b819d9d-0298-4f8f-9430-6d44e87ac7b5@ondemand.saucelabs.com:80/wd/hub test'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            emailext (
                    subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}""",
                    to: 'guillem@gromenware.com'
            )

        }
        failure {
            emailext (
                    subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}""",
                    to: 'guillem@gromenware.com'
            )
        }
        always{
            // publish html
            publishHTML target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/surefire-reports/',
                    reportFiles: 'emailable-report.html',
                    reportName: 'Emailable Report'
            ]
        }
    }
}