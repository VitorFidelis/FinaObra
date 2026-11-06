# ==========================================
# Estágio 1: Build da aplicação (Maven + JDK)
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copia apenas os arquivos de dependência primeiro para aproveitar o cache do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e realiza o empacotamento
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Estágio 2: Imagem final de execução (JRE)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o arquivo .jar gerado no estágio anterior
COPY --from=builder /build/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define o comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
