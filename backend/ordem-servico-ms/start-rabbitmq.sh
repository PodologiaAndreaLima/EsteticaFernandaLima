#!/bin/bash

# Script para iniciar RabbitMQ com Docker (Linux/Mac)

echo "================================"
echo "Iniciando RabbitMQ com Docker..."
echo "================================"

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado!"
    exit 1
fi

# Parar container anterior se existir
echo "Parando container RabbitMQ anterior (se existir)..."
docker stop rabbitmq 2>/dev/null
docker rm rabbitmq 2>/dev/null

# Iniciar RabbitMQ
echo "Iniciando novo container RabbitMQ..."
docker run -d \
    --name rabbitmq \
    -p 5672:5672 \
    -p 15672:15672 \
    -e RABBITMQ_DEFAULT_USER=fernanda \
    -e RABBITMQ_DEFAULT_PASS=fernanda123 \
    rabbitmq:3-management

# Aguardar inicialização
echo "Aguardando RabbitMQ ficar pronto..."
sleep 3

# Verificar status
if docker ps --filter "name=rabbitmq" | grep -q rabbitmq; then
    echo "✅ RabbitMQ iniciado com sucesso!"
    echo ""
    echo "Acessar Management UI:"
    echo "  URL: http://localhost:15672"
    echo "  Usuário: fernanda"
    echo "  Senha: fernanda123"
    echo ""
    echo "Acessar RabbitMQ (Port):"
    echo "  Host: localhost"
    echo "  Port: 5672"
else
    echo "❌ Falha ao iniciar RabbitMQ!"
    exit 1
fi

