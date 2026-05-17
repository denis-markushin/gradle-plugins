FROM eclipse-temurin:21-jre-alpine AS layers
WORKDIR /application
COPY build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
RUN adduser -S spring-user
USER spring-user
WORKDIR /application

COPY --from=layers /application/extracted/dependencies/ ./
COPY --from=layers /application/extracted/spring-boot-loader/ ./
COPY --from=layers /application/extracted/snapshot-dependencies/ ./
COPY --from=layers /application/extracted/application/ ./

RUN java -XX:ArchiveClassesAtExit=app.jsa -Dspring.context.exit=onRefresh -jar app.jar & exit 0

ENV JAVA_RESERVED_CODE_CACHE_SIZE="240M" \
    JAVA_MAX_DIRECT_MEMORY_SIZE="10M" \
    JAVA_MAX_METASPACE_SIZE="179M" \
    JAVA_XSS="1M" \
    JAVA_XMS="345M" \
    JAVA_XMX="345M" \
    JAVA_CDS_OPTS="-XX:SharedArchiveFile=app.jsa -Xlog:class+load:file=/tmp/classload.log" \
    JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"

ENTRYPOINT exec java \
    -XX:ReservedCodeCacheSize=$JAVA_RESERVED_CODE_CACHE_SIZE \
    -XX:MaxDirectMemorySize=$JAVA_MAX_DIRECT_MEMORY_SIZE \
    -XX:MaxMetaspaceSize=$JAVA_MAX_METASPACE_SIZE \
    -Xss$JAVA_XSS -Xms$JAVA_XMS -Xmx$JAVA_XMX \
    $JAVA_ERROR_FILE_OPTS $JAVA_CDS_OPTS \
    -jar app.jar
