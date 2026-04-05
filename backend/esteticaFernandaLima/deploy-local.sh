#!/bin/bash

echo "========================================="
echo " Iniciando o deploy local da Clínica..."
echo "========================================="

# 1. Para e remove o contêiner antigo caso ele esteja rodando
# O comando '2>/dev/null' esconde mensagens de erro caso o contêiner não exista na primeira vez
echo "-> Parando contêiner antigo (se houver)..."
docker stop api-estetica-fernanda-lima 2>/dev/null
docker rm api-estetica-fernanda-lima 2>/dev/null

# 2. Compila o projeto e gera o novo .jar
# Usamos o ./mvnw (Maven Wrapper) que já vem no Spring Boot, assim não precisamos do Maven instalado no sistema
# O -DskipTests pula a execução de testes para o build ser bem mais rápido
echo "-> Compilando o projeto com Maven..."
./mvnw clean package -DskipTests

# 3. Constrói a imagem Docker
echo "-> Construindo a nova imagem Docker..."
docker build -t backend-clinica .

# 4. Roda o novo contêiner em segundo plano (-d)
echo "-> Subindo o contêiner..."
docker run -d \
  --name api-estetica-fernanda-lima \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/esteticafernandalima?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo" \
  backend-clinica

echo "========================================="
echo "✅ Backend rodando com sucesso em background na porta 8080!"
echo "📄 Para acompanhar os logs em tempo real, digite:"
echo "   docker logs -f api-estetica-fernanda-lima"
echo "========================================="