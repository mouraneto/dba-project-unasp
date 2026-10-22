# Multi-stage Dockerfile para portabilidade total
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar definicao de dependencias
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

# Gerar o pacote JAR
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Estagio de execucao leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
