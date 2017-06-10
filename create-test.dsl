def services = ["Apple", "Banana", "Orange"]
def jobBase = 'sa-ca-test'
services.each {
    def service = it
    def jobName = "${jobBase}-service
    job(jobName) {
        scm {
            git("git://github.com/${service}.git", 'develop')
        }
        steps {
            maven("test -Dproject.name={service}")
        }
    }
