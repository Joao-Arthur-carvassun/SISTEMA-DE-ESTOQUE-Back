# Usa uma imagem oficial do Java (OpenJDK)
FROM eclipse-temurin:25-jdk-alpine as build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de configuração e dependências do Maven
mvnw pom.xml ./
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

# Empacota a aplicação ignorando testes para ser mais rápido
RUN ./mvnw clean package -DskipTests

# Etapa final para rodar a aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o arquivo .jar gerado na etapa anterior para o container
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão que o Render vai ler
EXPOSE 8080

# Comando para iniciar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
