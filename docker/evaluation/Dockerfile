From patrickmueller/sootkeeper:1.0

ENV DEPS peaks-core peaks-direct-native peaks-reflection peaks-static
ENV BUNDLES soot-infoflow soot-infoflow-android flowdroid-base-osgi flowdroid-osgi jdkdeadcode peaks-core-bundle peaks-direct-native-bundle peaks-static-bundle peaks-reflection-bundle

ENV ANALYSES jdkdeadcode-soot soot-infoflow-android peaks-direct-native-main peaks-static-main peaks-reflection-main

RUN apk add --update --no-cache ruby ruby-dev build-base && \
    mkdir /root/projects/ && \
    gem install json --no-document  && \
    apk del build-base ruby-dev && \
    rm -rf /tmp/* /var/tmp/* /var/cache/apk/* /usr/lib/ruby/gems/*/cache/*.gem

COPY projects /root/projects/

RUN for d in ${DEPS}; do \
      cd /root/projects/$d && \
      mvn install -DlocalRepositoryPath=${MAVEN_REPO} -Dmaven.repo.local=${MAVEN_REPO}; \
    done; \
    for b in ${BUNDLES}; do \
      cd /root/projects/$b && \
      mvn install -DlocalRepositoryPath=${MAVEN_REPO} -Dmaven.repo.local=${MAVEN_REPO} && \
      cp target/*.jar /root/felix/bundle/; \
    done; \
    mkdir -p /root/felix/analyses && \
    for a in ${ANALYSES}; do \
      cd /root/projects/$a && \
      mvn install -DlocalRepositoryPath=${MAVEN_REPO} -Dmaven.repo.local=${MAVEN_REPO} && \
      cp target/*.jar /root/felix/analyses/; \
    done; \
    cp /root/projects/*/*.txt /root/felix && \
    cp ${MAVEN_REPO}/org/slf4j/slf4j-simple/1.7.5/slf4j-simple-1.7.5.jar /root/felix/bundle && \
    cp ${MAVEN_REPO}/org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar /root/felix/bundle && \
    cp ${MAVEN_REPO}/com/google/guava/guava/20.0/guava-20.0.jar /root/felix/bundle && \
    rm -rf projects

COPY experiment.rb /root/felix/

CMD ruby experiment.rb

VOLUME ["/input/","/output/","/platforms/"]
