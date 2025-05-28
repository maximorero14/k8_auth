# Etapa de compilación
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .

# Ejecutar tests y generar reporte de Jacoco
RUN mvn clean verify -B

# Etapa de ejecución
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el artefacto final
COPY --from=build /app/target/*.jar application.jar

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]

#docker build -t k8_auth . && docker run -p 8080:8080 --name k8_auth_container k8_auth