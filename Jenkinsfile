pipeline {
    agent any

    tools {
        maven 'Maven-3.9.16'
    }

    environment {
        JAVA_HOME = "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.12.101-hotspot"
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

                bat '"%JAVA_HOME%\\bin\\java.exe" --version'
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
                echo 'Starting Contact Management application on port 8082...'

                bat '''
                start "ContactManagementApp" /B cmd /c ""%JAVA_HOME%\\bin\\java.exe" -jar target\\ContactManagement.jar --server.port=8082 > app.log 2>&1"

                "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe" -NoProfile -Command "Start-Sleep -Seconds 15"
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
                echo 'Logging into Docker Hub...'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    bat 'echo %DOCKER_PASSWORD%| docker login -u "%DOCKER_USER%" --password-stdin'

                    echo 'Pushing Docker image...'

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

                echo 'Waiting for application to start...'

                bat '''
                "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe" -NoProfile -Command "$success=$false; for($i=1;$i -le 30;$i++){ try { $r=Invoke-WebRequest -Uri 'http://localhost:8081' -UseBasicParsing -TimeoutSec 3; if($r.StatusCode -eq 200){ Write-Host 'Application is running successfully!'; $success=$true; break } } catch { Write-Host ('Waiting for application... Attempt ' + $i) }; Start-Sleep -Seconds 2 }; if(-not $success){ Write-Host 'Application did not become ready.'; docker logs %CONTAINER_NAME%; exit 1 }"
                '''

                echo '======================================'
                echo 'APPLICATION VERIFICATION SUCCESSFUL!'
                echo '======================================'
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