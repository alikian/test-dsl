def services = ["cst", "customer", "environment","orchestrator","reactor","resolver","rule","solution"]
String cid="ak186136"
services.each {
    def service = it
    def jobName = "sa-ca-aws-deploy-app-${service}-dev"
    job(jobName) {
        logRotator(30, 10, -1, -1)
        parameters {
          password{
            name('AWS_ACCESS_KEY_ID')
            defaultValue('')
            description('Enter your key ID to access AWS')
          }
          password{
            name('AWS_SECRET_ACCESS_KEY')
            defaultValue('')
            description('Enter your secret key to access AWS')
          }
          string{
            name('APP_DEPLOYER_VERSION')
            defaultValue('0.0.1-SNAPSHOT')
            description('app deployer template zip version')
          }
          password{
            name('ARTIFACTORY_APIKEY')
            defaultValue('')
            description('')
          }
          string{
            name('DOCKER_IMAGENAME')
            defaultValue('springio/springboot-sample')
            description('')
          }
          string{
            name('DOCKER_TAG')
            defaultValue('latest')
            description('')
          }
          string{
            name('PROPERTIES_VERSION')
            defaultValue('0.0.1-SNAPSHOT')
            description('app deployer template zip version')
          }
          
        }
        wrappers {
            credentialsBinding {
              usernamePassword{
                usernameVariable("tmc.public.user")
                passwordVariable("tmc.public.password")
                credentialsId(cid)
              }
              usernamePassword{
                usernameVariable("tmc.dev.user")
                passwordVariable("tmc.dev.password")
                credentialsId(cid)
              }
              usernamePassword{
                usernameVariable("tmc.preprod.user")
                passwordVariable("tmc.preprod.password")
                credentialsId(cid)
              }
              usernamePassword{
                usernameVariable("tmc.prod.user")
                passwordVariable("tmc.prod.password")
                credentialsId(cid)
              }
            }
        }
        steps {
          maven("dependency:unpack -Dartifact=com.teradata.tmc.sa.app:app-deployer:\${APP_DEPLOYER_VERSION}:zip -DoutputDirectory=.")
          maven("dependency:unpack -Dartifact=com.teradata.tmc.sa.ms.cst:deploy-properties:\${PROPERTIES_VERSION}:zip -DoutputDirectory=.")
          maven("package -f pom_deploy.xml -DOWNER_ID=\${BUILD_USER_ID}  -DARTIFACTORY_APIKEY=\${ARTIFACTORY_APIKEY}  -DDOCKER_TAG=\${DOCKER_TAG} -DDOCKER_IMAGENAME=\${DOCKER_IMAGENAME}  -DENVIRONMENT=dev")
          shell("export AWS_ACCESS_KEY_ID=\${AWS_ACCESS_KEY_ID}\n"+
				"set +x\n"+
				"export AWS_SECRET_ACCESS_KEY=\${AWS_SECRET_ACCESS_KEY}\n"+
				"set -x\n"+
				"export AWS_DEFAULT_REGION=us-west-2\n"+
				"aws cloudformation create-stack --stack-name T-Drive-dev-app-cst-service --template-body file://aws-ready-resources/tdrive_app_template.json --parameters file://aws-ready-resources/tdrive_app_parameters.json --capabilities CAPABILITY_IAM\n"+
				"export AWS_ACCESS_KEY_ID=\n"+
				"export AWS_SECRET_ACCESS_KEY=\n"+
				"export AWS_DEFAULT_REGION=\n")
        }
    }
}
