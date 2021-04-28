

pipeline {

  environment {
    ORG_GRADLE_PROJECT_appName = 'mod-agreements'
    GRADLEW_OPTS = '--console plain --no-daemon'
    BUILD_DIR = "${env.WORKSPACE}/service"
    MD = "${env.WORKSPACE}/service/build/resources/main/okapi/ModuleDescriptor.json"
    doKubeDeploy = true

  }

  options {
    timeout(30)
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  agent {
    node {
      label 'jenkins-agent-java11'
    }
  }

  stages {
    stage ('Setup') {
      steps {
        dir(env.BUILD_DIR) {
          script {
            def foliociLib = new org.folio.foliociCommands()
            def gradleVersion = foliociLib.gradleProperty('appVersion')

            env.name = env.ORG_GRADLE_PROJECT_appName
        
            // if release 
            if ( foliociLib.isRelease() ) {
              // make sure git tag and version match
              if ( foliociLib.tagMatch(gradleVersion) ) {
                env.isRelease = true 
                env.dockerRepo = 'folioorg'
                env.version = gradleVersion
              }
              else { 
                error('Git release tag and Maven version mismatch')
              }
            }
            else {
              env.dockerRepo = 'folioci'
              env.version = "${gradleVersion}"
            }
          }
        }
        sendNotifications 'STARTED'  
      }
    }

    stage('Gradle Build') { 
      steps {
        dir(env.BUILD_DIR) {
          sh "./gradlew $env.GRADLEW_OPTS assemble"
        }
      }
    }
   
    stage('Build Docker') {
      steps {
        dir(env.BUILD_DIR) {
          sh "./gradlew $env.GRADLEW_OPTS -PdockerRepo=${env.dockerRepo} buildImage"
        }
        // debug
        sh "cat $env.MD"
      } 
    }

    stage('Publish Docker Image') { 
      when { 
        anyOf {
          branch 'master'
          expression { return env.isRelease }
        }
      }
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', 'DockerHubIDJenkins') {
            sh "docker tag ${env.dockerRepo}/${env.name}:${env.version} ${env.dockerRepo}/${env.name}:latest"
            sh "docker push ${env.dockerRepo}/${env.name}:${env.version}"
            sh "docker push ${env.dockerRepo}/${env.name}:latest"
          }
        }
      }
    }

    stage('Debug Module Descriptor') {
      steps {
        script {
          sh "mv $MD ${MD}.orig"
          sh """
          cat ${MD}.orig | jq '.launchDescriptor.dockerImage |= \"${env.dockerRepo}/${env.name}:${env.version}\" |
              .launchDescriptor.dockerPull |= \"true\"' > $MD
          """
        }
        // debug
        sh "cat $env.MD"
      }
    }

    stage('Publish Module Descriptor') {
      when {
        anyOf { 
          branch 'master'
          expression { return env.isRelease }
        }
      }
      steps {
        script {
          def foliociLib = new org.folio.foliociCommands()
          foliociLib.updateModDescriptor(env.MD) 
        }
        postModuleDescriptor(env.MD)
      }
    }

  } // end stages

  post {
    always {
      sh "docker rmi ${env.name}:${env.version} || exit 0"
      sh "docker rmi ${env.name}:latest || exit 0"
      sh "docker rmi ${env.dockerRepo}/${env.name}:${env.version} || exit 0"
      sh "docker rmi ${env.dockerRepo}/${env.name}:${env.version}.${env.BUILD_NUMBER} || exit 0"
      sh "docker rmi ${env.dockerRepo}/${env.name}:latest || exit 0"
      sendNotifications currentBuild.result 
    }
  }
}
         

