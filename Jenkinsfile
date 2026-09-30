pipeline {
    agent any

    tools {
        jdk 'JDK21'
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
                bat 'java --version'
                bat 'mvn --version'
                bat 'mvn clean compile'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating JAR file...'
                bat 'mvn package -DskipTests'
            }
        }

        stage('Selenium Test') {
            steps {
                echo 'Starting Contact Management application...'

                bat '''
                start "ContactManagementApp" /B cmd /c "java -jar target\\ContactManagement.jar > app.log 2>&1"
                timeout /t 15 /nobreak
                '''

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
                echo 'Logging into Docker Hub and pushing image...'

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

                echo 'Application verification successful!'
            }
        }
    }

    post {
        success {
            echo '======================================'
            echo 'PIPELINE SUCCESSFUL!'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo 'PIPELINE FAILED!'
            echo '======================================'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}