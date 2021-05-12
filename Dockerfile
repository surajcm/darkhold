FROM openjdk:16-jdk
COPY . .
RUN sed -i '/JAVA_HOME/d' build.sh
RUN chmod +x /start.sh && chmod +x build.sh && chmod +x gradlew
EXPOSE 8181
ENTRYPOINT ["/start.sh"]
