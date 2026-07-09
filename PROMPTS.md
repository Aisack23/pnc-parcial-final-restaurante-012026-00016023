PROMPT 1: Me ayudo a nombrar los comits tras explicarle que habia hehco en cada uno

Ayudame con la redacdion de manera correcta y explicativa de los commits

1. chore: estructura inicial del proyecto Spring Boot con capas
2. feat: entidades de dominio y repositories JPA
3. feat: servicios de negocio para pedidos, mesas y usuarios
4. feat: sistema base, dtos y security base implementados

PROMPT 2: Crea el sistema de Entity y de repository

PROMPT 3: Crea ek sistema de DTO

PROMPT 4: Arregla el error de importacion de jsonwebtoken añadelo al gradle
Arreglé el error agregando las dependencias de jsonwebtoken/JJWT en build.gradle:
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
Validé con:
sh gradlew compileJava

PROMPT 5:Ayudame a crear el dpckerfile
# Etapa 1: build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]