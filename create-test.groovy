def services = ["cst", "customer", "environment","orchestrator","reactor","resolver","rule","solution"]
def jobBase = 'sa-ca-aws-deploy-app-{}-dev'
services.each {
    def service = it
    def jobName = "sa-ca-aws-deploy-app-${service}-dev"
    job(jobName) {
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
        }
        scm {
            git("git://github.com/${service}.git", 'develop')
        }
        steps {
          maven("dependency:unpack -Dartifact=com.teradata.tmc.sa.app:app-deployer:\${APP_DEPLOYER_VERSION}:zip -DoutputDirectory=.")
        }
    }
}
