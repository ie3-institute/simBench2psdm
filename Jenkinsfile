#!groovy

////////////////////////////////
// general config values
////////////////////////////////



//// project build dir order
//// this list contains the build order
//// normally it *should* start with the project under investigation
//// but if this projects depends on some of our other projects, you have to *build the dependencies first*!
//// *IMPORTANT:* you *MUST* use exact repo names as this will used for checkout!
//// *IMPORTANT2:* you must provide exact 4 elements!
projects = ['simBench2psdm']

orgNames = ['ie3-institute']
urls = ['git@github.com:' + orgNames.get(0)]

def sonarqubeProjectKey = "edu.ie3:simbench4ie3"

/// code coverage token id
codeCovTokenId = "simbench4ie3-codecov-token"

//// internal jenkins credentials link for git ssh keys
//// requires the ssh key to be stored in the internal jenkins credentials keystore
def sshCredentialsId = "19f16959-8a0d-4a60-bd1f-5adb4572b702"

//// define and setjava version ////
//// requires the java version to be set in the internal jenkins java version management
//// use identifier accordingly
def javaVersionId = 'jdk-17'

//// set java version method (needs node{} for execution)
void setJavaVersion(javaVersionId) {
    env.JAVA_HOME = "${tool javaVersionId}"
    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
}

/// global config variables that should be available during runtime
/// and will be overwritten during runtime -> DO NOT CHANGE THEM
String featureBranchName = ""

//// gradle tasks that are executed
def gradleTasks = "--refresh-dependencies clean spotlessCheck pmdMain pmdTest check" // the gradle tasks that are executed on ALL projects
def mainProjectGradleTasks = "reportScoverage checkScoverage" // additional tasks that are only executed on project 0 (== main project)
// if you need additional tasks for deployment add them here
// NOTE: artifactory task with credentials will be added below
def deployGradleTasks = ""

/// prepare debugging info about deployed artifacts
String deployedArtifacts = "none"

/// commit hash
def commitHash = ""

if (env.BRANCH_NAME == "main") {

    // setup
    getMasterBranchProps()

    // merge of features

    node {
        ansiColor('xterm') {
            try {
                // set java version
                setJavaVersion(javaVersionId)

                // checkout from scm
                stage('checkout from scm') {
                    try {
                        // merged mode
                        commitHash = gitCheckout(projects.get(0), urls.get(0), 'refs/heads/main', sshCredentialsId).GIT_COMMIT
                    } catch (exc) {
                        sh 'exit 1' // failure due to not found main branch
                    }
                }

                // get information based on commit hash
                def jsonObject = getGithubCommitJsonObj(commitHash, orgNames.get(0), projects.get(0))
                featureBranchName = splitStringToBranchName(jsonObject.commit.message)


                // set build display name
                currentBuild.displayName = ((featureBranchName?.trim()) ? "merge pr branch '${featureBranchName}'" : "commit '" +
                        "${jsonObject.commit.message.length() <= 20 ? jsonObject.commit.message : jsonObject.commit.message.substring(0, 20)}...'") + " (" + currentBuild.displayName + ")"


                // test the project
                stage("gradle check ${projects.get(0)}") {
                    // build and test the project
                    gradle("${gradleTasks} ${mainProjectGradleTasks}")
                }

                // execute sonarqube code analysis
                stage('SonarQube analysis') {
                    withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                        gradle("sonar -Dsonar.branch.name=main -Dsonar.projectKey=$sonarqubeProjectKey ")
                    }
                }


                // wait for the sonarqube quality gate
                stage("Quality Gate") {
                    timeout(time: 1, unit: 'HOURS') {
                        // Just in case something goes wrong, pipeline will be killed after a timeout
                        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }

                // post processing
                stage('publish reports + coverage') {
                    // publish reports
                    publishReports()

                    // inform codecov.io
                    withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                        // call codecov
                        sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                    }

                }


            } catch (Exception e) {
                // set build result to failure
                currentBuild.result = 'FAILURE'

                // publish reports even on failure
                publishReports()

                // print exception
                Date date = new Date()
                println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + e)

            }

        }

    }

} else {

    // setup
    getFeatureBranchProps()

    node {

        def repoName = ""
        // init variables depending of this build is triggered by a branch with PR or without PR
        if (env.CHANGE_ID == null) {
            // no PR exists
            featureBranchName = env.BRANCH_NAME
            repoName = orgNames.get(0) + "/" + projects.get(0)
        } else {
            // PR exists
            /// curl the api to get debugging details
            def jsonObj = getGithubPRJsonObj(env.CHANGE_ID, orgNames.get(0), projects.get(0))

            featureBranchName = jsonObj.head.ref
            repoName = jsonObj.head.repo.full_name

        }


        ansiColor('xterm') {
            try {
                // set java version
                setJavaVersion(javaVersionId)

                /// set the build name
                currentBuild.displayName = featureBranchName + " (" + currentBuild.displayName + ")"

                stage('checkout from scm') {

                    try {
                        commitHash = gitCheckout(projects.get(0), urls.get(0), featureBranchName, sshCredentialsId).GIT_COMMIT
                    } catch (exc) {
                        // our target repo failed during checkout
                        sh 'exit 1' // failure due to not found forcedPR branch
                    }

                }

                // test the project
                stage("gradle check ${projects.get(0)}") {

                    // build and test the project
                    gradle("${gradleTasks} ${mainProjectGradleTasks}")
                }

                // execute sonarqube code analysis
                stage('SonarQube analysis') {
                    withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                         // do we have a PR?
                        String gradleCommand = "sonar -Dsonar.projectKey=$sonarqubeProjectKey"

                        if (env.CHANGE_ID != null) {
                            gradleCommand = gradleCommand + " -Dsonar.pullrequest.branch=${featureBranchName} -Dsonar.pullrequest.key=${env.CHANGE_ID} -Dsonar.pullrequest.base=main -Dsonar.pullrequest.github.repository=${orgNames.get(0)}/${projects.get(0)} -Dsonar.pullrequest.provider=Github"
                        } else {
                            gradleCommand = gradleCommand + " -Dsonar.branch.name=$featureBranchName"
                        }
                        gradle(gradleCommand)

                    }
                }

                // wait for the sonarqube quality gate
                stage("Quality Gate") {
                    timeout(time: 1, unit: 'HOURS') {
                        // Just in case something goes wrong, pipeline will be killed after a timeout
                        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }

                // post processing
                stage('post processing') {
                    // publish reports
                    publishReports()

                    withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                        // call codecov
                        sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                    }

                }
            } catch (Exception e) {
                // set build result to failure
                currentBuild.result = 'FAILURE'

                // publish reports even on failure
                publishReports()

                // print exception
                Date date = new Date()
                println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + e)

            }

        }
    }
}


def getFeatureBranchProps() {

    properties(
            [pipelineTriggers([
                    issueCommentTrigger('.*!test.*')])
            ])

}


def getMasterBranchProps() {
    properties([parameters(
            [string(defaultValue: '', description: '', name: 'release', trim: true)]),
                [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 0, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project']
    ])
}

////////////////////////////////////
// git checkout
// NOTE: requires node {}
////////////////////////////////////
def gitCheckout(String relativeTargetDir, String baseUrl, String branch, String sshCredentialsId) {
    checkout([
            $class                           : 'GitSCM',
            branches                         : [[name: branch]],
            doGenerateSubmoduleConfigurations: false,
            extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]],
            submoduleCfg                     : [],
            userRemoteConfigs                : [[credentialsId: sshCredentialsId, url: baseUrl + "/" + relativeTargetDir + ".git"]]
    ])
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// publish reports
// IMPORTANT: has to be called inside the same node{} as where the build process (report generation) took place!
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
def publishReports() {
    // publish test reports
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/tests/allTests', reportFiles: 'index.html', reportName: "${projects.get(0)}_java_tests_report", reportTitles: ''])

    // publish scoverage reports
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/scoverage', reportFiles: 'scoverage.xml', reportName: "${projects.get(0)}_scoverage_report", reportTitles: ''])
    
    // publish pmd report for main project only
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/pmd', reportFiles: 'main.html', reportName: "${projects.get(0)}_pmd_report", reportTitles: ''])
}


// gradle wrapper method for easy execution
// requires the gradle version to be configured with the same name under tools in jenkins configuration
def gradle(String command) {
    env.JENKINS_NODE_COOKIE = 'dontKillMe' // this is necessary for the Gradle daemon to be kept alive

    // switch directory to bew able to use gradle wrapper
    sh """cd ${projects.get(0)}""" + ''' set +x; ./gradlew ''' + """$command"""
}

def getGithubPRJsonObj(String prId, String orgName, String repoName) {
    def jsonObj = readJSON text: curlByPR(prId, orgName, repoName)
    return jsonObj
}


def curlByPR(String prId, String orgName, String repoName) {

    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/pulls/" + prId
    String jsonResponseString = sh(script: curlUrl, returnStdout: true)

    return jsonResponseString
}

def getGithubCommitJsonObj(String commit_sha, String orgName, String repoName) {
    def jsonObj = readJSON text: curlByCSHA(commit_sha, orgName, repoName)
    return jsonObj
}

def curlByCSHA(String commit_sha, String orgName, String repoName) {

    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/commits/" + commit_sha
    String jsonResponseString = sh(script: curlUrl, returnStdout: true)

    return jsonResponseString
}

def splitStringToBranchName(String string) {
    def obj = string.split().find { it.startsWith("ie3-institute") }
    if (obj)
        return (obj as String).substring(14)
    else
        return ""
}
