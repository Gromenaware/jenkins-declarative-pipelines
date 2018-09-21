pipeline {
    agent any
    stages {
        stage('Checkout'){
            steps {
            git 'https://bitbucket.org/itnove/simple-maven-spring-boot-example.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Development Test') {
            steps {
                sh 'mvn -Dgroups=unit,integration test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/surefire-reports', reportFiles: 'index.html', reportName: 'Development Test', reportTitles: ''])
                }
            }
        }
        stage('Deliver') {
            steps {
                sh './jenkins/scripts/deliver.sh'
            }
        }
        stage('Functional Test') {
            steps {
                sh 'mvn -Dgroups=functional test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/surefire-reports', reportFiles: 'index.html', reportName: 'Functional Test', reportTitles: ''])
                }
            }
        }
    }
    post{
        success {
            archiveArtifacts artifacts: 'target/hello-*.war', onlyIfSuccessful: true
        }
    }
}
