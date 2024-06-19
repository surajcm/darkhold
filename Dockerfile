FROM eclipse-temurin:22.0.1_8-jdk-ubi9-minimal
COPY . .
RUN sed -i '/JAVA_HOME/d' build.sh && chmod +x /start.sh && chmod +x build.sh && chmod +x gradlew
VOLUME ["/tmp/db"]
EXPOSE 8181
ENTRYPOINT ["/start.sh"]
