FROM eclipse-temurin:25_36-jdk-noble
COPY . .
RUN sed -i '/JAVA_HOME/d' build.sh && chmod +x /start.sh && chmod +x build.sh && chmod +x gradlew
VOLUME ["/tmp/db"]
EXPOSE 8181
ENTRYPOINT ["/start.sh"]
