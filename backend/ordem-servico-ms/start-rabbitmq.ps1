# Script para iniciar RabbitMQ com Docker (Windows/PowerShell)

Write-Host "================================"
Write-Host "Iniciando RabbitMQ com Docker..."
Write-Host "================================"

# Verificar se Docker está instalado
try {
    docker --version | Out-Null
} catch {
    Write-Host "❌ Docker não está instalado!" -ForegroundColor Red
    exit 1
}

# Parar container anterior se existir
Write-Host "Parando container RabbitMQ anterior (se existir)..."
docker stop rabbitmq -ErrorAction SilentlyContinue
docker rm rabbitmq -ErrorAction SilentlyContinue

# Iniciar RabbitMQ
Write-Host "Iniciando novo container RabbitMQ..."
docker run -d `
    --name rabbitmq `
    -p 5672:5672 `
    -p 15672:15672 `
    -e RABBITMQ_DEFAULT_USER=fernanda `
    -e RABBITMQ_DEFAULT_PASS=fernanda123 `
    rabbitmq:3-management

# Aguardar inicialização
Write-Host "Aguardando RabbitMQ ficar pronto..."
Start-Sleep -Seconds 3

# Verificar status
$status = docker ps --filter "name=rabbitmq" --format "{{.Status}}"
if ($status) {
    Write-Host "✅ RabbitMQ iniciado com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Acessar Management UI:"
    Write-Host "  URL: http://localhost:15672"
    Write-Host "  Usuário: fernanda"
    Write-Host "  Senha: fernanda123"
    Write-Host ""
    Write-Host "Acessar RabbitMQ (Port):"
    Write-Host "  Host: localhost"
    Write-Host "  Port: 5672"
} else {
    Write-Host "❌ Falha ao iniciar RabbitMQ!" -ForegroundColor Red
    exit 1
}

