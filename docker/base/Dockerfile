FROM maven:alpine

ENV FELIX_URL=https://archive.apache.org/dist/felix/ FELIX_TAR=org.apache.felix.main.distribution-5.6.1.tar.gz FELIX_FOLDER=felix MAVEN_REPO=/usr/share/maven/ref/repository CONSOLE_API_JAR=/org/knopflerfish/bundle/console-API/4.0.1/console-API-4.0.1.jar

RUN apk add --update --no-cache git && rm -rf /var/cache/apk/*
RUN cd /root/ \
    && curl ${FELIX_URL}/${FELIX_TAR} > ${FELIX_TAR} \
    && mkdir -p ${FELIX_FOLDER} \
    && tar -xf ${FELIX_TAR} -C ${FELIX_FOLDER} --strip 1 \
    &&  rm ${FELIX_TAR}
RUN curl http://soot-build.cs.uni-paderborn.de/nightly/soot/soot-trunk.jar > soot-trunk.jar \
    && mvn org.apache.maven.plugins:maven-install-plugin:install-file -Dfile=soot-trunk.jar -DgroupId=ca.mcgill.sable -DartifactId=soot -Dversion=trunk -Dpackaging=jar -DlocalRepositoryPath=${MAVEN_REPO} \
    && rm soot-trunk.jar
RUN git clone https://github.com/stg-tud/sootkeeper.git /root/sootkeeper/ \
    && cd /root/sootkeeper/ \
    && mvn install -DlocalRepositoryPath=${MAVEN_REPO} -Dmaven.repo.local=${MAVEN_REPO} \
    && cp out/*.jar ${MAVEN_REPO}/${CONSOLE_API_JAR} /root/${FELIX_FOLDER}/bundle \
    && rm -rf /root/sootkeeper/
WORKDIR /root/${FELIX_FOLDER}/

ENTRYPOINT []
CMD java -jar bin/felix.jar
