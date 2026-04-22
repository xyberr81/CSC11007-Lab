pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-25'
    }
    
    environment {
        // List of all services in monorepo
        SERVICES = 'product cart customer order inventory payment payment-paypal rating search media location tax promotion webhook sampledata recommendation delivery'
    }
    
    stages {
        // Stage 1: Detect which services changed
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(
                        script: 'git diff --name-only HEAD~1 HEAD || echo ""',
                        returnStdout: true
                    ).trim()
                    
                    echo "Changed files:\n${changedFiles}"
                    
                    def allServices = env.SERVICES.split(' ')
                    def changedServices = []
                    
                    // Check each service for changes
                    allServices.each { service ->
                        if (changedFiles.contains("${service}/")) {
                            changedServices.add(service)
                        }
                    }
                    
                    // If pom.xml changed, build all services
                    if (changedFiles.contains('pom.xml')) {
                        changedServices = allServices
                    }
                    
                    // Store for later stages
                    env.CHANGED_SERVICES = changedServices.join(',')
                    env.SERVICES_COUNT = changedServices.size()
                    
                    echo "Changed services: ${env.CHANGED_SERVICES}"
                    echo "Number of services to build: ${env.SERVICES_COUNT}"
                }
            }
        }
        
        // Stage 2: Test Phase
        stage('Test') {
            when {
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    
                    services.each { service ->
                        echo "Running tests for service: ${service}"
                        
                        dir(service) {
                            // Run Maven tests
                            sh 'mvn clean test'
                            
                            // Collect test results
                            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                            
                            // Archive coverage report
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target/site/jacoco',
                                reportFiles: 'index.html',
                                reportName: "Coverage Report - ${service}"
                            ])
                        }
                    }
                }
            }
            post {
                always {
                    // Archive test results
                    archiveArtifacts artifacts: '**/target/surefire-reports/*.xml', allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }
        
        // Stage 3: Build Phase
        stage('Build') {
            when {
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    
                    services.each { service ->
                        echo "Building service: ${service}"
                        
                        dir(service) {
                            // Build package (skip tests as they ran in Test stage)
                            sh 'mvn clean package -DskipTests'
                            
                            // Archive JAR artifact
                            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Clean workspace
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
