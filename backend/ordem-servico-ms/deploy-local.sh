#!/bin/bash

echo "============================================="
echo " Iniciando o deploy local do ordem-servico-ms"
echo "============================================="

# 1. Para e remove o container antigo caso ele esteja rodando
echo "-> Parando container antigo (se houver)..."
docker stop api-ordem-servico-ms 2>/dev/null
docker rm api-ordem-servico-ms 2>/dev/null

# 2. Compila o projeto e gera o novo .jar
echo "-> Compilando o projeto com Maven..."
if [ -f "./mvnw" ]; then
  ./mvnw clean package
else
  mvn clean package
fi

# 3. Constroi a imagem Docker
echo "-> Construindo a nova imagem Docker..."
docker build -t ordem-servico-ms .

# 4. Roda o novo container em segundo plano (-d)
echo "-> Subindo o container..."
docker run -d \
  --name api-ordem-servico-ms \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/esteticafernandalima?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD:-sua_senha}" \
  -e RABBITMQ_HOST="host.docker.internal" \
  -e RABBITMQ_PORT="5672" \
  -e RABBITMQ_USERNAME="fernanda" \
  -e RABBITMQ_PASSWORD="${RABBITMQ_PASSWORD:-fernanda123}" \
  ordem-servico-ms

echo "============================================="
echo "Ordem-servico-ms rodando em background na porta 8081!"
echo "Para acompanhar os logs em tempo real, digite:"
echo "   docker logs -f api-ordem-servico-ms"
echo "============================================="
