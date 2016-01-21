SootKeeper
==========

Do you have a large evaluation base to run your analysis on? Tired of rerunning the basic analyses when only your specific analysis changes? We developed OSGi modularity for static analysis to counter this.

Motivation
----------
The use of static analysis allows developers to analyze the source code of a program. On one hand, developers get accurate data representing any issues with the program. On the other hand, static analysis programs can have lengthy execution times. This results in a large amount of developer idleness, waiting for their program to execute. A developer's debug cycles can be particularly time consuming, when tweaking their analyses. They then have to wait around for long periods of time to test their minor changes. In order to avoid this needless idle time, we have looked into modularizing a static analysis program in order to cut down on time wasted.

Team
-----------
* Ben Hermann
* Florian Kübler
* Johannes Lerch
* Patrick Müller
* Ben Setzer


Usage
-----------

You need Soot in your local maven repository. 
If you made Modifications of your own to Soot you need to build it yourself. 
This requires you to build [Jasmin](https://github.com/Sable/jasmin),[Heros](https://github.com/Sable/heros)  and [Soot](https://github.com/Sable/soot) with `ant publish-local-maven`
If you just want to use Soot it is enough to add the current nightly with:
```
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=soot-trunk.jar -DgroupId=ca.mcgill.sable -DartifactId=soot -Dversion=trunk -Dpackaging=jar
```

TODO