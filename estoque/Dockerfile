# Usa uma imagem oficial do Java (com a versão 25 que você está usando)
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# CORREÇÃO AQUI: Usando COPY para trazer o mvnw e o pom.xml para dentro do container
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

# Empacota a aplicação ignorando testes
RUN ./mvnw clean package -DskipTests

# Etapa final para rodar a aplicação
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
