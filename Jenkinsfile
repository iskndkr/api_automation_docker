pipeline {
    agent any

    environment {
        // Docker image name
        DOCKER_IMAGE = 'bookstore-api-tests'
        DOCKER_TAG = "${BUILD_NUMBER}"

        // Environment variables for API configuration
        BASE_URL = 'https://fakerestapi.azurewebsites.net'
        API_VERSION = '/api/v1'

        // Maven settings
        MAVEN_OPTS = '-Xmx1024m'
    }

    tools {
        maven 'Maven 3.8.7' // Make sure this is configured in Jenkins Global Tool Configuration
        jdk 'JDK 11'        // Make sure this is configured in Jenkins Global Tool Configuration
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Run Tests in Docker') {
            steps {
                script {
                    echo 'Running API tests in Docker container...'

                    // Run tests in container and copy results
                    sh """
                        docker run --name api-tests-${BUILD_NUMBER} \
                            -e BASE_URL=${BASE_URL} \
                            -e API_VERSION=${API_VERSION} \
                            ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            mvn clean test
                    """

                    // Copy test results from container to Jenkins workspace
                    sh """
                        docker cp api-tests-${BUILD_NUMBER}:/app/target ./target || true
                        docker rm -f api-tests-${BUILD_NUMBER} || true
                    """
                }
            }
            post {
                always {
                    // Clean up container even if tests fail
                    sh "docker rm -f api-tests-${BUILD_NUMBER} || true"
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    echo 'Generating Allure report...'

                    // Generate Allure report using Maven
                    sh 'mvn allure:report || true'
                }
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    echo 'Publishing test results...'

                    // Publish TestNG results
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'TestNG Report',
                        reportTitles: 'API Test Report'
                    ])

                    // Publish Allure report
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    echo 'Cleaning up old Docker images...'

                    // Keep only last 5 images
                    sh """
                        docker images ${DOCKER_IMAGE} --format '{{.Tag}}' | \
                        sort -rn | tail -n +6 | \
                        xargs -I {} docker rmi ${DOCKER_IMAGE}:{} || true
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed!'

            // Archive test results
            archiveArtifacts artifacts: 'target/surefire-reports/**/*.xml', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'logs/**/*.log', allowEmptyArchive: true

            // Publish test results
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }

        success {
            echo 'Tests passed successfully!'
            emailext(
                subject: "✅ Build #${BUILD_NUMBER} - SUCCESS",
                body: """
                    <h2>API Test Execution Successful</h2>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Build URL:</b> ${BUILD_URL}</p>
                    <p><b>View Allure Report:</b> ${BUILD_URL}allure/</p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }

        failure {
            echo 'Tests failed!'
            emailext(
                subject: "❌ Build #${BUILD_NUMBER} - FAILED",
                body: """
                    <h2>API Test Execution Failed</h2>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Build URL:</b> ${BUILD_URL}</p>
                    <p><b>View Console Output:</b> ${BUILD_URL}console</p>
                    <p><b>View Allure Report:</b> ${BUILD_URL}allure/</p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }
    }
}
