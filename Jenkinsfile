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
                    junit '**/build/test-results/test/*.xml'

                    // Archivar los reportes de Cucumber si los tienes generados (especifica la ruta correcta)
                    archiveArtifacts artifacts: '**/build/reports/cucumber/*.html', allowEmptyArchive: true
                }
            }
        }
    }
}
