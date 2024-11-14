pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/miguelvargas2110/AutomatizationTest.git'
        BRANCH = 'master'
    }

    stages {
        stage('Checkout') {
            steps {
                // Descarga el código desde el repositorio
                git branch: "${env.BRANCH}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                // Dar permisos de ejecución a gradlew
                sh 'chmod +x gradlew'

                // Compilar el proyecto usando Gradle
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                // Ejecutar las pruebas con Gradle, especificando la clase TestRunner si es necesario
                sh './gradlew test --tests runners.TestRunner'
            }
            post {
                always {
                    // Publicar los resultados de las pruebas en el reporte de Jenkins (usando el formato JUnit)
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/cucumber-reports.html', allowEmptyArchive: true
                }
            }
        }
    }
}
