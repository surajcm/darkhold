FROM eclipse-temurin:25.0.1_8-jdk-noble
RUN apt-get update && \
    apt-get install -y findutils && \
    rm -rf /var/lib/apt/lists/*
COPY . .
RUN sed -i '/JAVA_HOME/d' build.sh && chmod +x /start.sh && chmod +x build.sh && chmod +x gradlew
VOLUME ["/tmp/db"]
EXPOSE 8181
ENTRYPOINT ["/start.sh"]
