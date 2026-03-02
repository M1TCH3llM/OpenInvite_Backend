// ============================================================
// OpenInvite Backend — Jenkins Pipeline
// ============================================================
// Put this file as 'Jenkinsfile' in the ROOT of your
// OpenInvite_Backend GitHub repo.
//
// Triggers:
//   - Automatically on push to main branch (via webhook)
//   - Manually from Jenkins UI
// ============================================================

pipeline {
    agent any

    environment {
        AWS_REGION       = 'us-east-1'
        ECR_REGISTRY     = '094840069693.dkr.ecr.us-east-1.amazonaws.com'
        ECR_REPO         = 'open-invite/backend'
        EKS_CLUSTER      = 'staging-eks-cluster'
        K8S_NAMESPACE    = 'backend'
        K8S_DEPLOYMENT   = 'open-invite-backend'
        IMAGE_TAG        = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(7) ?: 'latest'}"
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/M1TCH3llM/OpenInvite_Backend.git'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build \
                        -t ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG} \
                        -t ${ECR_REGISTRY}/${ECR_REPO}:latest \
                        .
                """
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | \
                        docker login --username AWS --password-stdin ${ECR_REGISTRY}

                    docker push ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${ECR_REPO}:latest
                """
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh """
                    aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER}

                    kubectl set image deployment/${K8S_DEPLOYMENT} \
                        backend=${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG} \
                        -n ${K8S_NAMESPACE}

                    kubectl rollout status deployment/${K8S_DEPLOYMENT} \
                        -n ${K8S_NAMESPACE} --timeout=180s
                """
            }
        }
    }

    post {
        success {
            echo "Backend deployed successfully! Version: ${IMAGE_TAG}"
        }
        failure {
            echo "Backend deployment failed!"
        }
        always {
            sh 'docker image prune -f || true'
        }
    }
}
