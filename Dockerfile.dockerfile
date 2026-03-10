FROM csbase.registry.cmbchina.cn/paas/cmb-oraclejdk-1.8.0:latest
WORKDIR /opt/deployments
COPY ./target/platform-api.jar /opt/deployments/
RUN mkdir source
WORKDIR /opt/deployments/source
COPY ./target/platform-api-source.jar /opt/deployments/source
RUN upzip -qo platform-api-sources.jar && rm -rf platform-api-sources.jar
WORKDIR /opt/deployments
ENV JAVA_OPTS="-Xms1325M -Xmx1325M -Xmn460 -Xss699K -XX:MetaspaceSize=203M -XX:MaxMetaspaceSize=203M"
ENV TIMEZONE="-Duser.timezone=Asia/Shanghai"
ENV JAVA_GC="-XX:ErrorFile=/tmp/hs_err_pid%p.log -XX:HeapDumpPath=/tmp -Xloggc:/tmp/gc%t.log"
EXPOSE 8099
CMD java $JAVA_OPTS $JAVA_GC $TIMEZONE -jar platform-api.jar