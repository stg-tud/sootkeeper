# SootKeeper
Do you have a large evaluation base to run your analysis on? Tired of rerunning the basic analyses when only your specific analysis changes? We developed OSGi modularity for static analysis to counter this.

## Motivation
The use of static analysis allows developers to analyze the source code of a program. On one hand, developers get accurate data representing any issues with the program. On the other hand, static analysis programs can have lengthy execution times. This results in a large amount of developer idleness, waiting for their program to execute. A developer's debug cycles can be particularly time consuming, when tweaking their analyses. They then have to wait around for long periods of time to test their minor changes. In order to avoid this needless idle time, we have looked into modularizing a static analysis program in order to cut down on time wasted.

## Team
* Ben Hermann
* Florian Kübler
* Johannes Lerch
* Patrick Müller
* Ben Setzer

## Requirements
* Java 1.8
* Maven (At least Version 2)
* Ant (If you want to install soot from source)
* soot in your local maven repository (See below)
* OSGi framework distribution, either [apache felix](https://felix.apache.org/downloads.cgi) or [eclipse equinox](http://download.eclipse.org/equinox/) work
* [SootConfig](https://github.com/stg-tud/sootconfig) in your local maven repository

## Installing soot
### From nightly
Download the current [nightly build of Soot](https://github.com/Sable/soot#how-do-i-obtain-the-nightly-builds), for instance using curl:

    curl https://ssebuild.cased.de/nightly/soot/lib/soot-trunk.jar > soot-trunk.jar
    
And install it using maven:

    mvn org.apache.maven.plugins:maven-install-plugin:install-file -Dfile=soot-trunk.jar -DgroupId=ca.mcgill.sable -DartifactId=soot -Dversion=trunk -Dpackaging=jar


### From Source
If you made Modifications of your own to Soot you need to build it yourself.
This requires you to build [jasmin](https://github.com/Sable/jasmin),[heros](https://github.com/Sable/heros) and [soot](https://github.com/Sable/soot) (in this order) using
`ant publish-local-maven` in each project.

## Getting the Source Code
If you want to install the latest version from git, clone the repository with:

    git clone https://github.com/stg-tud/sootkeeper.git

## Compiling and installing
Simply use `mvn package`. The Resulting jars will be in the top level out directory.
## Running
### In IntelliJ IDEA
In IntelliJ IDEA go to `Run > Edit Configurations...`, click on `+` and select `OSGi Bundles`.

Give the configuration a name of your choice and select either Felix or Equinox as OSGi Framework. (Make sure you added a OSGi Framework to IntelliJ IDEA as shown [here](https://www.jetbrains.com/idea/help/osgi-framework-instances.html))

Now add the SootKeeper bundles by clicking `+` and select framework, container and the analysis bundles (e.g. soot-bundle).

Furthermore we recommend to clean the OSGi cache on each run (in the Parameters tap, select `Runtime directory: Recreate each time`

### In eclipse
TODO

### Manually
For this example we are going to use the felix framework.

Copy the built jars from the out folder to the bundle folder of the felix framework.
Run felix using in the top level folder of the felix distribution.

    java -jar bin/felix.jar

**Note:** If you changed a bundle (e.g. your analysis) it is not sufficient enough to just replace the jar within the bundle directory. Furthermore you have to delete the felix-cache directory.

### Using the OSGi shell

Sootkeeper provides two commands in the OSGi shell, `listAnalyses` and `runAnalysis` and their respective shorthands `la` and `ra`.

Any listed Analysis can then be run with `runAnalysis <analysisName> <Optional Parameter>`.


## Implement your own Analysis
The easiest way to implement an modular analysis is creating a maven project and extending our framework.

### The pom.xml
Within your pom file you need to care about three things:

1. Say maven that your project is an OSGi bundle and should be exported as one (by `<packaging>bundle</packaging>`).
2. Add the SootKeeper framework to your dependencies (GroupId: `de.tud.cs.peaks.osgi` ArtifactId `framework`).
3. Configure the maven-bundle-plugin s.t. you list your packages to export (that should be used by other bundles), import (that you use from other bundles and must include the SootKeeper API `de.tud.cs.peaks.osgi.framework.api.*`) and the private packages (code that should be in your bundle jar but is not used outside you bundle). More over you need to set your Bundle Activator class.

Step three can be done when an implementation draft exists.

Here is an example of a valid pom.xml.

```xml
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>de.tud.cs.peaks.osgi</groupId>
      <artifactId>hello-world</artifactId>
      <version>1.0</version>
      <packaging>bundle</packaging>
      <dependencies>
          <dependency>
              <groupId>org.osgi</groupId>
              <artifactId>org.osgi.core</artifactId>
              <version>6.0.0</version>
          </dependency>
          <dependency>
              <groupId>de.tud.cs.peaks.osgi</groupId>
              <artifactId>framework</artifactId>
              <version>nightly</version>
          </dependency>
      </dependencies>
      <build>
          <plugins>
              <plugin>
                  <groupId>org.apache.felix</groupId>
                  <artifactId>maven-bundle-plugin</artifactId>
                  <version>2.5.3</version>
                  <extensions>true</extensions>
                  <configuration>
                      <instructions>
                          <Export-Package>de.tud.cs.peaks.osgi.hello-world.api</Export-Package>
                          <Private-Package>de.tud.cs.peaks.osgi.hello-world.*</Private-Package>
                          <Import-Package>de.tud.cs.peaks.osgi.framework.api.*</Import-Package>
                          <Bundle-Activator>de.tud.cs.peaks.osgi.hello-world.Activator</Bundle-Activator>
                      </instructions>
                  </configuration>
              </plugin>
          </plugins>
      </build>
  </project>
```
