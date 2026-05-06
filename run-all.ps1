param(
    [switch]$SkipRabbitMQ
)

$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ToolsDir = Join-Path $RootDir ".tools"
$JdkDir = Join-Path $ToolsDir "jdk-21"
$FrontendDir = Join-Path $RootDir "frontend\clinica podologia\react-app"
$BackendDir = Join-Path $RootDir "backend\esteticaFernandaLima"
$OrdemMsDir = Join-Path $RootDir "backend\ordem-servico-ms"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "-> $Message"
}

function Write-Success {
    param([string]$Message)
    Write-Host "Concluido: $Message"
}

function Write-Warn {
    param([string]$Message)
    Write-Host "Atencao: $Message"
}

function Fail {
    param([string]$Message)
    Write-Host ""
    Write-Host "Erro: $Message"
    exit 1
}

function Test-Command {
    param([string]$Name)
    return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

function Get-JavaMajorVersion {
    param([string]$JavaCommand)

    $versionOutput = & $JavaCommand -version 2>&1 | Out-String
    if ($versionOutput -match 'version "([^"]+)"') {
        return ($Matches[1].Split(".")[0])
    }

    return ""
}

function Get-JavaHomeFromCommand {
    $settings = java -XshowSettings:properties -version 2>&1 | Out-String
    $match = [regex]::Match($settings, "java\.home\s*=\s*(.+)")
    if ($match.Success) {
        return $match.Groups[1].Value.Trim()
    }

    return ""
}

function Ensure-Java21 {
    Write-Step "Verificando Java 21"
    Write-Host "Java 21 e necessario para compilar os backends."
    Write-Host "Verificando instalacao local..."

    if (Test-Command "java") {
        $major = Get-JavaMajorVersion "java"
        if ($major -eq "21") {
            $javaHome = Get-JavaHomeFromCommand
            if ($javaHome) {
                $env:JAVA_HOME = $javaHome
                $env:Path = "$env:JAVA_HOME\bin;$env:Path"
                Write-Host "Java 21 encontrado em: $env:JAVA_HOME"
            } else {
                Write-Host "Java 21 encontrado no PATH."
            }

            Write-Success "Java 21 disponivel para compilacao."
            return
        }
    }

    $localJava = Join-Path $JdkDir "bin\java.exe"
    if (Test-Path $localJava) {
        $env:JAVA_HOME = $JdkDir
        $env:Path = "$env:JAVA_HOME\bin;$env:Path"
        Write-Host "Java 21 local encontrado em: $env:JAVA_HOME"
        Write-Success "Java 21 disponivel para compilacao."
        return
    }

    Write-Step "Baixando Java 21 localmente em .tools"
    Write-Host "Java 21 nao foi encontrado instalado."
    Write-Host "Uma copia local do Eclipse Temurin sera baixada para a pasta do projeto."
    Write-Host "Nenhuma instalacao global do sistema sera alterada."

    $arch = switch ($env:PROCESSOR_ARCHITECTURE) {
        "AMD64" { "x64" }
        "ARM64" { "aarch64" }
        default { Fail "Arquitetura nao suportada para download automatico do Java. Instale Java 21 manualmente." }
    }

    $archive = Join-Path $ToolsDir "jdk-21.zip"
    $tmpDir = Join-Path $ToolsDir "jdk-download"
    $url = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/$arch/jdk/hotspot/normal/eclipse?project=jdk"

    New-Item -ItemType Directory -Force -Path $ToolsDir | Out-Null
    if (Test-Path $tmpDir) {
        Remove-Item -LiteralPath $tmpDir -Recurse -Force
    }
    if (Test-Path $JdkDir) {
        Remove-Item -LiteralPath $JdkDir -Recurse -Force
    }
    if (Test-Path $archive) {
        Remove-Item -LiteralPath $archive -Force
    }
    New-Item -ItemType Directory -Force -Path $tmpDir | Out-Null

    Write-Host "Baixando arquivo:"
    Write-Host "  Origem:  $url"
    Write-Host "  Destino: $archive"
    Invoke-WebRequest -Uri $url -OutFile $archive

    Write-Host "Extraindo Java 21..."
    Expand-Archive -LiteralPath $archive -DestinationPath $tmpDir -Force

    $extractedDir = Get-ChildItem -LiteralPath $tmpDir -Directory | Select-Object -First 1
    if (-not $extractedDir) {
        Fail "Nao foi possivel encontrar o diretorio extraido do Java."
    }

    Move-Item -LiteralPath $extractedDir.FullName -Destination $JdkDir
    Remove-Item -LiteralPath $tmpDir -Recurse -Force
    Remove-Item -LiteralPath $archive -Force

    $env:JAVA_HOME = $JdkDir
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"

    Write-Host "Java 21 baixado em: $env:JAVA_HOME"
    Write-Success "Java 21 disponivel para compilacao."
}

function Ensure-NodeModules {
    Write-Step "Instalando dependencias do frontend"
    Write-Host "Pasta do frontend:"
    Write-Host "  $FrontendDir"

    Push-Location $FrontendDir
    try {
        if (Test-Command "npm") {
            Write-Host "npm encontrado."
            Write-Host "Comando:"
            Write-Host "  npm install"
            npm install
            Write-Success "Dependencias do frontend instaladas."
            return
        }

        if (Test-Command "docker") {
            Write-Warn "npm nao encontrado localmente."
            Write-Host "Instalacao das dependencias sera feita com Docker usando node:22-alpine."
            Write-Host "Comando executado dentro do container temporario:"
            Write-Host "  npm install"
            docker run --rm -v "${FrontendDir}:/app" -w /app node:22-alpine npm install
            Write-Success "Dependencias do frontend instaladas via Docker."
            return
        }

        Fail "npm nao encontrado. Instale Node.js ou Docker para baixar as dependencias do frontend."
    } finally {
        Pop-Location
    }
}

function Build-Backend {
    Write-Step "Compilando backend principal"
    Write-Host "Pasta do backend principal:"
    Write-Host "  $BackendDir"
    Write-Host "Comando:"
    Write-Host "  .\mvnw.cmd clean package -DskipTests"

    Push-Location $BackendDir
    try {
        .\mvnw.cmd clean package -DskipTests
        Write-Success "Backend principal compilado."
    } finally {
        Pop-Location
    }
}

function Build-OrdemMs {
    Write-Step "Compilando microservico ordem-servico-ms"
    Write-Host "Pasta do microservico:"
    Write-Host "  $OrdemMsDir"

    Push-Location $OrdemMsDir
    try {
        if (Test-Command "mvn") {
            Write-Host "Maven encontrado."
            Write-Host "Comando:"
            Write-Host "  mvn clean package -DskipTests"
            mvn clean package -DskipTests
            Write-Success "Microservico compilado."
        } else {
            Write-Warn "Maven nao encontrado localmente."
            Write-Host "O Dockerfile do microservico fara a compilacao com Maven dentro do container."
        }
    } finally {
        Pop-Location
    }
}

function Ensure-Docker {
    Write-Step "Verificando Docker Compose"
    Write-Host "Docker e necessario para subir frontend, backend e microservico juntos."

    if (-not (Test-Command "docker")) {
        Fail "Docker nao encontrado. Instale o Docker Desktop e tente novamente."
    }

    docker compose version | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Fail "Docker Compose nao esta disponivel nesse Docker."
    }

    Write-Host "Versao do Docker Compose:"
    docker compose version
    Write-Success "Docker Compose pronto."
}

function Ensure-RabbitMQ {
    Write-Step "Garantindo RabbitMQ local"
    Write-Host "RabbitMQ e necessario para o microservico."
    Write-Host "Verificando container local..."

    if ($SkipRabbitMQ -or $env:START_RABBITMQ -eq "false") {
        Write-Warn "Inicializacao do RabbitMQ desativada."
        return
    }

    $runningNames = docker ps --format "{{.Names}}"
    if ($runningNames -contains "rabbitmq") {
        Write-Host "RabbitMQ ja esta rodando."
        Write-Success "RabbitMQ pronto."
        return
    }

    $allNames = docker ps -a --format "{{.Names}}"
    if ($allNames -contains "rabbitmq") {
        Write-Host "Container RabbitMQ ja existe. Iniciando..."
        docker start rabbitmq | Out-Null
        Write-Success "RabbitMQ iniciado."
        return
    }

    $rabbitUser = if ($env:RABBITMQ_USERNAME) { $env:RABBITMQ_USERNAME } else { "fernanda" }
    $rabbitPassword = if ($env:RABBITMQ_PASSWORD) { $env:RABBITMQ_PASSWORD } else { "fernanda123" }

    Write-Host "Criando container RabbitMQ..."
    Write-Host "Usuario: $rabbitUser"
    Write-Host "Senha:   $rabbitPassword"

    docker run -d `
        --name rabbitmq `
        -p 5672:5672 `
        -p 15672:15672 `
        -e RABBITMQ_DEFAULT_USER="$rabbitUser" `
        -e RABBITMQ_DEFAULT_PASS="$rabbitPassword" `
        rabbitmq:3-management | Out-Null

    Write-Success "RabbitMQ criado e iniciado."
}

function Start-Project {
    Write-Step "Subindo os containers do projeto"
    Write-Host "As imagens serao buildadas e os containers serao iniciados em background."
    Write-Host "Comando:"
    Write-Host "  docker compose up --build -d"

    Push-Location $RootDir
    try {
        docker compose up --build -d
        Write-Success "Containers solicitados ao Docker Compose."
    } finally {
        Pop-Location
    }
}

function Print-Done {
    Write-Host ""
    Write-Host "=============================================="
    Write-Host "Projeto em execucao:"
    Write-Host "Frontend:            http://localhost:3000"
    Write-Host "Backend principal:   http://localhost:8080"
    Write-Host "Ordem-servico-ms:    http://localhost:8081"
    Write-Host "RabbitMQ UI:         http://localhost:15672"
    Write-Host ""
    Write-Host "Logs:"
    Write-Host "  docker compose logs -f"
    Write-Host ""
    Write-Host "Parar tudo:"
    Write-Host "  docker compose down"
    Write-Host "=============================================="
}

function Main {
    Set-Location $RootDir

    Write-Host "=============================================="
    Write-Host " Bootstrap local - Estetica Fernanda Lima"
    Write-Host "=============================================="
    Write-Host "Raiz do projeto:"
    Write-Host "  $RootDir"
    Write-Host ""
    Write-Host "Etapas: dependencias, compilacao dos servicos e inicializacao via Docker Compose."
    Write-Host "Operacoes demoradas esperadas: downloads, npm install, Maven e build Docker."

    Ensure-Java21
    Ensure-NodeModules
    Build-Backend
    Build-OrdemMs
    Ensure-Docker
    Ensure-RabbitMQ
    Start-Project
    Print-Done
}

Main
