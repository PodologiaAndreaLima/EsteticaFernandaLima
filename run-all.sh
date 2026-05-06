#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_DIR="$ROOT_DIR/.tools"
JDK_DIR="$TOOLS_DIR/jdk-21"
FRONTEND_DIR="$ROOT_DIR/frontend/clinica podologia/react-app"
BACKEND_DIR="$ROOT_DIR/backend/esteticaFernandaLima"
ORDEM_MS_DIR="$ROOT_DIR/backend/ordem-servico-ms"

info() {
  echo ""
  echo "-> $1"
}

success() {
  echo "OK: $1"
}

warn() {
  echo "Aviso: $1"
}

fail() {
  echo ""
  echo "Erro: $1"
  exit 1
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

download_file() {
  local url="$1"
  local output="$2"

  echo "Baixando arquivo:"
  echo "  Origem:  $url"
  echo "  Destino: $output"

  if command_exists curl; then
    echo "Usando curl para o download..."
    curl -L --fail "$url" -o "$output"
  elif command_exists wget; then
    echo "Usando wget para o download..."
    wget -O "$output" "$url"
  else
    fail "curl ou wget e necessario para baixar o Java 21 automaticamente."
  fi
}

java_major_version() {
  local java_bin="${1:-java}"
  "$java_bin" -version 2>&1 | awk -F '"' '/version/ { split($2, v, "."); print v[1]; exit }'
}

detect_java_home() {
  java -XshowSettings:properties -version 2>&1 | awk -F '= ' '/java.home/ { print $2; exit }'
}

to_windows_path() {
  if command_exists cygpath; then
    cygpath -w "$1"
  else
    echo "$1"
  fi
}

ensure_java_21() {
  info "Verificando Java 21"
  echo "O backend usa Java 21. Vou procurar uma instalacao existente antes de baixar outra."

  if command_exists java && [ "$(java_major_version java)" = "21" ]; then
    JAVA_HOME="$(detect_java_home)"
    export JAVA_HOME
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Java 21 encontrado em: $JAVA_HOME"
    success "Java pronto para compilar os backends."
    return
  fi

  if [ -x "$JDK_DIR/bin/java" ] || [ -x "$JDK_DIR/bin/java.exe" ]; then
    export JAVA_HOME="$JDK_DIR"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Java 21 local encontrado em: $JAVA_HOME"
    success "Java pronto para compilar os backends."
    return
  fi

  info "Baixando Java 21 localmente em .tools"
  echo "Nao encontrei Java 21 instalado. Vou baixar uma copia local do Eclipse Temurin."
  echo "Isso nao instala nada no sistema: fica somente dentro da pasta do projeto."

  local os
  local arch
  local archive
  local url
  local tmp_dir

  case "$(uname -s)" in
    Linux*) os="linux"; archive="jdk-21.tar.gz" ;;
    Darwin*) os="mac"; archive="jdk-21.tar.gz" ;;
    MINGW*|MSYS*|CYGWIN*) os="windows"; archive="jdk-21.zip" ;;
    *) fail "Sistema operacional nao suportado para download automatico do Java. Instale Java 21 manualmente." ;;
  esac

  case "$(uname -m)" in
    x86_64|amd64) arch="x64" ;;
    arm64|aarch64) arch="aarch64" ;;
    *) fail "Arquitetura nao suportada para download automatico do Java. Instale Java 21 manualmente." ;;
  esac

  mkdir -p "$TOOLS_DIR"
  tmp_dir="$TOOLS_DIR/jdk-download"
  rm -rf "$tmp_dir" "$JDK_DIR" "$TOOLS_DIR/$archive"
  mkdir -p "$tmp_dir"

  url="https://api.adoptium.net/v3/binary/latest/21/ga/$os/$arch/jdk/hotspot/normal/eclipse?project=jdk"
  download_file "$url" "$TOOLS_DIR/$archive"

  if [ "$os" = "windows" ]; then
    if command_exists unzip; then
      unzip -q "$TOOLS_DIR/$archive" -d "$tmp_dir"
    elif command_exists powershell.exe; then
      local archive_windows
      local tmp_windows
      archive_windows="$(to_windows_path "$TOOLS_DIR/$archive")"
      tmp_windows="$(to_windows_path "$tmp_dir")"
      powershell.exe -NoProfile -Command "Expand-Archive -LiteralPath '$archive_windows' -DestinationPath '$tmp_windows' -Force" >/dev/null
    else
      fail "unzip ou powershell.exe e necessario para extrair o Java no Windows."
    fi
  else
    tar -xzf "$TOOLS_DIR/$archive" -C "$tmp_dir"
  fi

  local extracted_dir
  extracted_dir="$(find "$tmp_dir" -mindepth 1 -maxdepth 1 -type d | head -n 1)"
  [ -n "$extracted_dir" ] || fail "Nao foi possivel encontrar o diretorio extraido do Java."

  if [ -x "$extracted_dir/Contents/Home/bin/java" ]; then
    mv "$extracted_dir/Contents/Home" "$JDK_DIR"
  else
    mv "$extracted_dir" "$JDK_DIR"
  fi
  rm -rf "$tmp_dir" "$TOOLS_DIR/$archive"

  export JAVA_HOME="$JDK_DIR"
  export PATH="$JAVA_HOME/bin:$PATH"

  echo "Java 21 baixado em: $JAVA_HOME"
  success "Java pronto para compilar os backends."
}

ensure_node_modules() {
  info "Instalando dependencias do frontend"
  echo "Pasta do frontend:"
  echo "  $FRONTEND_DIR"

  cd "$FRONTEND_DIR"

  if command_exists npm; then
    echo "npm encontrado. Rodando:"
    echo "  npm install"
    npm install
    success "Dependencias do frontend instaladas."
    return
  fi

  if command_exists docker; then
    warn "npm nao encontrado localmente. Vou usar Docker com node:22-alpine para instalar node_modules."
    echo "Rodando npm install dentro de um container temporario..."
    docker run --rm \
      -v "$FRONTEND_DIR:/app" \
      -w /app \
      node:22-alpine \
      npm install
    success "Dependencias do frontend instaladas via Docker."
    return
  fi

  fail "npm nao encontrado. Instale Node.js ou Docker para baixar as dependencias do frontend."
}

build_backend() {
  info "Compilando backend principal"
  echo "Pasta do backend principal:"
  echo "  $BACKEND_DIR"
  echo "Rodando:"
  echo "  ./mvnw clean package -DskipTests"

  cd "$BACKEND_DIR"
  chmod +x ./mvnw 2>/dev/null || true
  ./mvnw clean package -DskipTests
  success "Backend principal compilado."
}

build_ordem_ms() {
  info "Compilando microservico ordem-servico-ms"
  echo "Pasta do microservico:"
  echo "  $ORDEM_MS_DIR"

  cd "$ORDEM_MS_DIR"

  if command_exists mvn; then
    echo "Maven encontrado. Rodando:"
    echo "  mvn clean package -DskipTests"
    mvn clean package -DskipTests
    success "Microservico compilado."
  else
    warn "Maven nao encontrado localmente."
    echo "Tudo bem: o Dockerfile do microservico vai compilar com Maven dentro do container."
  fi
}

ensure_docker() {
  info "Verificando Docker Compose"
  echo "Docker e necessario para subir frontend, backend e microservico juntos."

  command_exists docker || fail "Docker nao encontrado. Instale o Docker Desktop e tente novamente."
  docker compose version >/dev/null || fail "Docker Compose nao esta disponivel nesse Docker."
  echo "Versao do Docker Compose:"
  docker compose version
  success "Docker Compose pronto."
}

ensure_rabbitmq() {
  info "Garantindo RabbitMQ local"
  echo "O microservico usa RabbitMQ. Vou iniciar um container local se ainda nao existir."

  if [ "${START_RABBITMQ:-true}" != "true" ]; then
    warn "START_RABBITMQ diferente de true. Pulando RabbitMQ."
    return
  fi

  if docker ps --format '{{.Names}}' | grep -qx "rabbitmq"; then
    echo "RabbitMQ ja esta rodando."
    success "RabbitMQ pronto."
    return
  fi

  if docker ps -a --format '{{.Names}}' | grep -qx "rabbitmq"; then
    echo "Container RabbitMQ ja existe. Iniciando..."
    docker start rabbitmq >/dev/null
    success "RabbitMQ iniciado."
    return
  fi

  echo "Criando container RabbitMQ..."
  echo "Usuario: ${RABBITMQ_USERNAME:-fernanda}"
  echo "Senha:   ${RABBITMQ_PASSWORD:-fernanda123}"
  docker run -d \
    --name rabbitmq \
    -p 5672:5672 \
    -p 15672:15672 \
    -e RABBITMQ_DEFAULT_USER="${RABBITMQ_USERNAME:-fernanda}" \
    -e RABBITMQ_DEFAULT_PASS="${RABBITMQ_PASSWORD:-fernanda123}" \
    rabbitmq:3-management >/dev/null
  success "RabbitMQ criado e iniciado."
}

start_project() {
  info "Subindo os containers do projeto"
  echo "Agora vou buildar as imagens e subir tudo em background."
  echo "Comando:"
  echo "  docker compose up --build -d"

  cd "$ROOT_DIR"
  docker compose up --build -d
  success "Containers solicitados ao Docker Compose."
}

print_done() {
  echo ""
  echo "=============================================="
  echo "Projeto rodando bonito:"
  echo "Frontend:            http://localhost:3000"
  echo "Backend principal:   http://localhost:8080"
  echo "Ordem-servico-ms:    http://localhost:8081"
  echo "RabbitMQ UI:         http://localhost:15672"
  echo ""
  echo "Logs:"
  echo "  docker compose logs -f"
  echo ""
  echo "Parar tudo:"
  echo "  docker compose down"
  echo "=============================================="
}

main() {
  cd "$ROOT_DIR"

  echo "=============================================="
  echo " Bootstrap local - Estetica Fernanda Lima"
  echo "=============================================="
  echo "Raiz do projeto:"
  echo "  $ROOT_DIR"
  echo ""
  echo "O script vai preparar dependencias, compilar os servicos e subir tudo com Docker Compose."
  echo "Se algo demorar, e normalmente download, npm install, Maven ou build Docker trabalhando."

  ensure_java_21
  ensure_node_modules
  build_backend
  build_ordem_ms
  ensure_docker
  ensure_rabbitmq
  start_project
  print_done
}

main "$@"
