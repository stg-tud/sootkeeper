# SootKeeper
Do you have a large evaluation base to run your analysis on? Tired of rerunning the basic analyses when only your specific analysis changes? We developed OSGi modularity for static analysis to counter this.

##Motivation
The use of static analysis allows developers to analyze the source code of a program. On one hand, developers get accurate data representing any issues with the program. On the other hand, static analysis programs can have lengthy execution times. This results in a large amount of developer idleness, waiting for their program to execute. A developer's debug cycles can be particularly time consuming, when tweaking their analyses. They then have to wait around for long periods of time to test their minor changes. In order to avoid this needless idle time, we have looked into modularizing a static analysis program in order to cut down on time wasted.

##Team
* Ben Hermann
* Florian Kübler
* Johannes Lerch
* Patrick Müller
* Ben Setzer

##Requirements
* Java 1.8
* Maven
* soot in your local maven repository (See below)
* OSGi framework distribution, either [apache felix](https://felix.apache.org/downloads.cgi) or [eclipse equinox](http://download.eclipse.org/equinox/) work

##Installing soot
### From nightly
Install the current soot [nightly](https://github.com/Sable/soot#how-do-i-obtain-the-nightly-builds) with:

    mvn org.apache.maven.plugins:maven-install-plugin:install-file -Dfile=soot-trunk.jar -DgroupId=ca.mcgill.sable -DartifactId=soot -Dversion=trunk -Dpackaging=jar


### From Source 
If you made Modifications of your own to Soot you need to build it yourself. 
This requires you to build [jasmin](https://github.com/Sable/jasmin),[heros](https://github.com/Sable/heros) and [soot](https://github.com/Sable/soot) (in this order) using 
`ant publish-local-maven` in each project.

##Getting the Source Code
If you want to install the latest version from git, clone the repository with:

    git clone https://github.com/stg-tud/sootkeeper.git

## Compiling and installing
You have to run

    mvn install    
in the framework, container and soot-bundle folders.

## Running 
### In IntelliJ IDEA

### In eclipse
TODO

### Manually
For this example we are going to use the felix framework.
 
Copy the built jars from the different target folders to the bundle folder of the felix framework.
Run felix using in the top level folder of the felix distribution.
   
    java -jar bin/felix.jar
    
### Using the OSGi shell

Sootkeeper provides two commands in the OSGi shell, `listAnalyses` and `runAnalysis` and their respective shorthands `la` and `ra`.

Any listed Analysis can then be run with `runAnalysis <analysisName> <Optional Parameter>`.
