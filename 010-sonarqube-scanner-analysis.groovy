pipeline {
 agent any
 tools {
  maven "maven_3_8_6"
 }
 stages {
  stage('SCM') {
   steps {
    git 'https://github.com/Gromenaware/simple-maven-spring-boot-example.git'
   }
  }
  stage('maven compile') {
   steps {
    sh 'mvn install'
   }
  }
 }
 post {
     always {
            withSonarQubeEnv('Sonar') {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.3.0.603:sonar -Dsonar.projectKey=com.itnove.pipeline.example -Dsonar.projectName=com.itnove.pipeline.example -Dsonar.projectVersion=${BUILD_NUMBER} -Dsonar.language=java -Dsonar.sources=src/ -Dsonar.sourcesEnconding=UTF-8 -Dsonar.java.binaries=target/classes -Dsonar.exclusions=src/test/**'
        }
    }
   }
}