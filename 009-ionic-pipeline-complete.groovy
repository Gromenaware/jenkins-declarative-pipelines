pipeline {
    agent any
    environment {
        ENV = '$platform'
    }
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
        stage('Build Android') {
            when {
                environment name: "ENV", value: "android"
            }
            steps {
                sh 'npm install -g cordova'
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
                sh '/usr/local/lib/node_modules/ionic/bin/ionic build $platform'
            }
        }
        stage('Build IOS') {
            when {
                environment name: "ENV", value: "ios"
            }
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
                sh '/usr/local/lib/node_modules/ionic/bin/ionic build $platform'
            }
        }
        stage('Deploy Web App') {
            when {
                environment name: "ENV", value: "browser"
            }
            steps {
                echo 'Cleaning former WebApp deployments'
                sh 'killall node'
                echo 'Starting Browser Web App Deployment'
                sh 'npm install -g cordova'
                sh 'npm config set prefix /usr/local'
                sh 'npm root -g'
                sh '/usr/local/lib/node_modules/ionic/bin/ionic serve --nobrowser &'
            }
        }
        stage('Test Web App Locally Server') {
            when {
                environment name: "ENV", value: "browser"
            }
            steps {
                echo 'Starting Web App locally'
                sh 'cd /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/'
                sh 'mvn -f /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/pom.xml -Dtest=IonicDesktopTest test'
            }
        }
        stage('Test Android Local Appium') {
            when {
                environment name: "ENV", value: "android"
            }
            steps {
                echo 'Starting Tests on Android App locally'
                sh 'cd /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/'
                sh 'mvn -f /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/pom.xml -Dtest=IonicTest -Ddevice=android test'
            }
        }
        stage('Test IOS Local Appium') {
            when {
                environment name: "ENV", value: "ios"
            }
            steps {
                echo 'Starting Tests on IOS App locally'
                sh 'cd /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/'
                sh 'mvn -f /Users/guillemhernandezsola/.jenkins/workspace/ionic-complete-pipeline/maven_tests/pom.xml -Dtest=IonicTest -Ddevice=ios test'
            }
        }
    }
}