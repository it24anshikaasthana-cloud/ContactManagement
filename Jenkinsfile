pipeline {
    agent any

    tools {
        maven 'Maven-3.9.16'
    }

    environment {
        DOCKER_IMAGE = "anshikaasthana/contactmanagement:latest"
        CONTAINER_NAME = "contactmanagement-container"
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building project...'
                bat 'mvn clean compile'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating JAR...'
                bat 'mvn package -DskipTests'
            }
        }

        stage('Selenium Test') {
            steps {
                echo 'Running Selenium tests...'
                bat 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                bat 'docker build -t %DOCKER_IMAGE% .'
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to Docker Hub...'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    bat 'echo %DOCKER_PASSWORD%| docker login -u "%DOCKER_USER%" --password-stdin'
                    bat 'docker push %DOCKER_IMAGE%'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Docker container...'

                bat '''
                docker rm -f %CONTAINER_NAME% 2>nul || exit /b 0
                docker run -d --name %CONTAINER_NAME% -p 8081:8080 %DOCKER_IMAGE%
                '''
            }
        }

        stage('Verify') {
            steps {
                echo 'Verifying Docker container...'
                bat 'docker ps'
                bat 'curl -f http://localhost:8081'
            }
        }
    }

    post {
        success {
            echo 'PIPELINE SUCCESSFUL!'
        }

        failure {
            echo 'PIPELINE FAILED!'
        }
    }
}