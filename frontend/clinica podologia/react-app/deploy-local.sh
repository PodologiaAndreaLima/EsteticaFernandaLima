#!/bin/bash

echo "======================================"
echo " Iniciando o deploy local do frontend"
echo "======================================"

# 1. Para e remove o container antigo caso ele esteja rodando
echo "-> Parando container antigo (se houver)..."
docker stop frontend-clinica 2>/dev/null
docker rm frontend-clinica 2>/dev/null

# 2. Constroi a imagem Docker
echo "-> Construindo a nova imagem Docker..."
docker build \
  --build-arg VITE_API_BASE_URL="${VITE_API_BASE_URL:-http://localhost:8080}" \
  --build-arg VITE_ORDEM_SERVICO_BASE_URL="${VITE_ORDEM_SERVICO_BASE_URL:-http://localhost:8081}" \
  --build-arg VITE_API_BASE_URL_DEV="${VITE_API_BASE_URL_DEV:-http://localhost:8080}" \
  --build-arg VITE_API_BASE_URL_PROD="${VITE_API_BASE_URL_PROD:-http://localhost:8080}" \
  --build-arg VITE_API_MS_BASE_URL_DEV="${VITE_API_MS_BASE_URL_DEV:-http://localhost:8081}" \
  --build-arg VITE_API_MS_BASE_URL_PROD="${VITE_API_MS_BASE_URL_PROD:-http://localhost:8081}" \
  --build-arg VITE_API_TIMEOUT_MS="${VITE_API_TIMEOUT_MS:-15000}" \
  --build-arg VITE_API_MS_TIMEOUT_MS="${VITE_API_MS_TIMEOUT_MS:-20000}" \
  --build-arg VITE_ORDEM_SERVICO_ENDPOINT="${VITE_ORDEM_SERVICO_ENDPOINT:-/ordens-servico}" \
  -t frontend-clinica .

# 3. Roda o novo container em segundo plano (-d)
echo "-> Subindo o container..."
docker run -d \
  --name frontend-clinica \
  -p 3000:80 \
  frontend-clinica

echo "======================================"
echo "Frontend rodando em background:"
echo "   http://localhost:3000"
echo "Para acompanhar os logs em tempo real, digite:"
echo "   docker logs -f frontend-clinica"
echo "======================================"
