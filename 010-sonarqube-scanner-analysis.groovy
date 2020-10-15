pipeline {
 agent any
 tools {
  maven "maven_jenkins"
 }
 stages {
  stage('SCM') {
   steps {
    git 'https://github.com/jenkins-docs/simple-java-maven-app.git'
   }
  }
  stage('maven compile') {
   steps {
    sh 'mvn install'
   }
  }
  stage('SonarQube Analysis') {
   steps {
    def scannerHome = tool 'Sonar';
    withSonarQubeEnv('Sonar') {
     sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.3.0.603:sonar -Dsonar.projectKey=com.itnove.example -Dsonar.projectName=com.itnove.example -Dsonar.projectVersion=${BUILD_NUMBER} -Dsonar.language=java -Dsonar.sources=src/ -Dsonar.sourcesEnconding=UTF-8 -Dsonar.java.binaries=target/classes -Dsonar.exclusions=src/test/**'
    }
   }
  }
 }
}
