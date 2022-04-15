pipeline {
    
    agent {
        kubernetes {
            yamlFile 'builder.yaml'
        }
    }
    environment {
        DOCKERHUB_USERNAME = "shobhan"
        APP_NAME = "docker-spring"
        IMAGE_TAG = "${BUILD_NUMBER}"
        IMAGE_NAME = "${DOCKERHUB_USERNAME}" + "/" + "${APP_NAME}"
    }
    
    stages {
        stage('Build Maven project & Archive Artifact') {
            steps {
                container('maven') {
                    script {
                        sh '''
                        mvn clean install -Dmaven.test.skip
                        '''
                    }
                }
                archiveArtifacts '**/target/*.jar'
            }
        }
        
        stage('Kaniko Image Build & Push Image') {
            when {
                branch 'main'
            }
            steps {
                container('kaniko') {
                    script {
                        sh '''
                        /kaniko/executor --dockerfile `pwd`/Dockerfile \
                                        --context `pwd` \
                                        --destination=${IMAGE_NAME}:${BUILD_NUMBER} \
                                        --destination=${IMAGE_NAME}:latest
                        '''
                    }
                }
            }
        }

        stage('Checkout K8S manifest SCM'){
            when {
                branch 'main'
            }
            steps {
                git credentialsId: 'gh_shobhan_key', 
                url: 'https://github.com/shobhans/argocd_app_k8s_manifests.git',
                branch: 'main'
            }
        }
        
        stage('Staging Deploy - Update K8S manifest & push to Repo'){
            when {
                branch 'main'
            }
            steps {
                milestone(1)
                script{
                    withCredentials([usernamePassword(credentialsId: 'gh_shobhan_key', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        sh '''
                        cd staging
                        cat deployment.yaml
                        sed -i "s/${APP_NAME}.*/${APP_NAME}:${BUILD_NUMBER}/g" deployment.yaml
                        cat deployment.yaml
                        git config --global user.name "shobhan"
                        git config --global user.email "post.shobhan@gmail.com"
                        git add deployment.yaml
                        git commit -m 'Updated the Staging App deployment | Jenkins Pipeline'
                        git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/${GIT_USERNAME}/argocd_app_k8s_manifests.git HEAD:main
                        '''                        
                    }
                }
            }
        }

        stage('Prod Deploy - Update K8S manifest & push to Repo'){
            when {
                branch 'main'
            }
            steps {
                input 'Deploy to Production ?'
                milestone(2)
                script{
                    withCredentials([usernamePassword(credentialsId: 'gh_shobhan_key', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        sh '''
                        cd prod
                        cat deployment.yaml
                        sed -i "s/${APP_NAME}.*/${APP_NAME}:${BUILD_NUMBER}/g" deployment.yaml
                        cat deployment.yaml
                        git config --global user.name "shobhan"
                        git config --global user.email "post.shobhan@gmail.com"
                        git add deployment.yaml
                        git commit -m 'Updated the Prod App deployment | Jenkins Pipeline'
                        git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/${GIT_USERNAME}/argocd_app_k8s_manifests.git HEAD:main
                        '''                        
                    }
                }
            }
        }

    }
}
