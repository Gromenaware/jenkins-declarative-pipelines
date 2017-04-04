pipeline {
    agent any
    tools {
        maven "maven_jenkins"
        jdk "java_jenkins"
        nodejs "nodejs"
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://guillemhs@bitbucket.org/itnove/myexampleapp.git'
            }
        }
        stage('Build') {
            steps {
                sh 'npm install -g cordova'
                sh 'npm install -g ios-sim'
                sh 'npm install -g ios-deploy'
                sh 'npm config set prefix /usr/local'
                sh 'npm root -g'
                sh 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home'
                sh 'export M2_HOME=/Users/guillemhernandezsola/maven/apache-maven-3.3.9'
                sh 'export MAVEN_HOME=/Users/guillemhernandezsola/maven/apache-maven-3.3.9'
                sh 'export ANDROID_HOME=/usr/local/Caskroom/android-sdk/25.2.3'
                sh 'export PATH=$PATH:$M2_HOME/bin:$MAVEN_HOME/bin:$JAVA_HOME/bin:/usr/local/Caskroom/android-sdk/25.2.3/build-tools/25.0.2:/usr/local/Caskroom/android-sdk/25.2.3/platform-tools:/usr/local/Caskroom/android-sdk/25.2.3/tools/bin:/usr/local/Caskroom/android-sdk'
                sh 'export PATH=$PATH:$ANDROID_HOME/platform-tools'
                sh 'export PATH=$PATH:$ANDROID_HOME/build-tools/25.0.2'
                sh 'export PATH=$PATH:$ANDROID_HOME/tools'
                sh 'source /Users/guillemhernandezsola/.profile'
                sh '/usr/local/lib/node_modules/ionic/bin/ionic build $OS'
            }
        }
        stage('Test') {
            steps {
                git url: 'git@github.com:Gromenaware/corball-test-automation-demo.git'
                sh 'mvn -Dtest=WebDriverTest -Dbrowser=chrome -Dhub=http://selgp:8b819d9d-0298-4f8f-9430-6d44e87ac7b5@ondemand.saucelabs.com:80/wd/hub test'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
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