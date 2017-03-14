'''
Created on 10.11.2014

@author: M.Sc. Leonid Glanz
'''

userHome = "/Users/leonidglanz"
jreFolder = userHome + "/Libraries/JRE/JRE"  # path to analyze all .jar's in
strutsFolder = userHome + "/Libraries/Struts/Struts"
tomcatFolder = userHome + "/Libraries/Tomcat/Tomcat"
results = userHome + "/FindBugs/results"  # target to save results
findbugs = "java -jar " + userHome + "/FindBugs/lib/findbugs.jar"  # findbugs executable
includeFilter = userHome + "/FindBugs/tim/includeFilter.xml"  # path to filters

tomcatcp = "/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/bin/bootstrap.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/bin/commons-daemon.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/bin/tomcat-juli.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/annotations-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/catalina-ant.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/catalina-ha.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/catalina-tribes.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/catalina.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/ecj-4.4.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/el-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/jasper-el.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/jasper.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/jsp-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/servlet-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-coyote.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-dbcp.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-i18n-es.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-i18n-fr.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-i18n-ja.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-jdbc.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat-util.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/tomcat7-websocket.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/lib/websocket-api.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/webapps/examples/WEB-INF/lib/jstl.jar:/Users/leonidglanz/Libraries/Tomcat/Tomcat/apache-tomcat-7.0.55/apache-tomcat-7.0.55/webapps/examples/WEB-INF/lib/standard.jar"

import os
import fnmatch
from subprocess import Popen, PIPE

def executeFindBugs(folder):

    
    for dir1 in os.listdir(folder):
        cp = ""
        for root, _, files in os.walk(folder + "/" + dir1):
                
                for items in fnmatch.filter(files, "*.jar"):
                        cp += (root + "/" + items + os.pathsep).replace("\\", "/")
        
        cp += tomcatcp 
        for root, _, files in os.walk(folder + "/" + dir1):
                if not os.path.exists(results + "/" + dir1):
                    os.mkdir(results + "/" + dir1)
                count = 0
                for items in fnmatch.filter(files, "*.jar"):
                        print root + "/" + items
                        count += 1
                        outputPath = results + "/" + dir1 + "/" + items + str(count) + "default.html"
                        pathToAnalyze = root + "/" + items
                        cmd1 = findbugs + " -include " + includeFilter + " -html:fancy-hist.xsl -output " + outputPath + " -auxclasspath " + cp + " " + pathToAnalyze
                        lines = Popen(cmd1, shell=True, stdout=PIPE).stdout.readlines()
                        print "\n".join(lines)

 
executeFindBugs(strutsFolder)
executeFindBugs(tomcatFolder)
executeFindBugs(jreFolder)

