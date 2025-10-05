FROM amazoncorretto:22-alpine
WORKDIR /app

# Copy your Spring Boot jar
COPY target/libs/product-service-1.0-SNAPSHOT.jar product-service.jar

# JVM options
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Force Spring profile to dev
ENV SPRING_PROFILES_ACTIVE=dev

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar product-service.jar"]
