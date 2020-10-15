pipeline {
    agent any
    tools {
        maven "maven_jenkins"
        nodejs "nodejs"
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://guillemhs@bitbucket.org/itnove/myexampleapp.git'
            }
        }
        stage('Clean') {
            steps {
                sh 'killall node'
            }
        }
        stage('Build') {
            steps {
                sh 'npm install -g cordova'
                sh 'npm install -g ios-sim'
                sh 'npm install -g ios-deploy'
                sh 'npm config set prefix /usr/local'
                sh 'npm root -g'
                sh '/usr/local/lib/node_modules/ionic/bin/ionic serve --nobrowser &'
            }
        }
        stage('Test') {
            steps {
                echo 'Tests here'
                sh 'sleep 2m'
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
    }
}