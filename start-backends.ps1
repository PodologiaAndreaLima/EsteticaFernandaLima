# Execute na raiz: .\start-backends.ps1
# Requer JDK 21 e Maven no PATH (ordem-servico-ms nao possui Maven Wrapper).
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projects = @(
    'backend\esteticaFernandaLima'
    'backend\ordem-servico-ms'
)
$processes = @()
$consoleConfigured = $false
$previousControlCMode = $false

try {
    $maven = Get-Command mvn.cmd -CommandType Application -ErrorAction SilentlyContinue
    if (-not $maven) {
        throw 'Maven nao encontrado. Instale o Maven e adicione mvn.cmd ao PATH.'
    }
    if (-not (Get-Command java.exe -CommandType Application -ErrorAction SilentlyContinue)) {
        throw 'Java nao encontrado. Instale o JDK 21 e configure JAVA_HOME e PATH.'
    }

    foreach ($project in $projects) {
        $projectPath = Join-Path $PSScriptRoot $project
        if (-not (Test-Path -LiteralPath (Join-Path $projectPath 'pom.xml'))) {
            throw "pom.xml nao encontrado em: $projectPath"
        }
    }

    # Captura Ctrl+C antes que o Windows encerre o cmd/Maven e deixe a JVM orfa.
    # Em hosts sem console, o bloco finally ainda trata o cancelamento do PowerShell.
    try {
        if (-not [Console]::IsInputRedirected) {
            $previousControlCMode = [Console]::TreatControlCAsInput
            [Console]::TreatControlCAsInput = $true
            $consoleConfigured = $true
        }
    }
    catch {
        $consoleConfigured = $false
    }

    foreach ($project in $projects) {
        $projectPath = Join-Path $PSScriptRoot $project
        Write-Host "Iniciando $project..." -ForegroundColor Cyan
        # /d desabilita AutoRun; as aspas permitem caminhos com espacos.
        # spring-boot:run tambem passa por test-compile. Para iniciar os servicos,
        # pulamos a compilacao dos testes (skipTests sozinho nao faz isso).
        $command = '/d /s /c ""{0}" -Dmaven.test.skip=true spring-boot:run"' -f $maven.Source
        $process = Start-Process -FilePath $env:ComSpec -ArgumentList $command `
            -WorkingDirectory $projectPath -NoNewWindow -PassThru
        $processes += [PSCustomObject]@{ Name = $project; Process = $process }
    }

    Write-Host 'Backends iniciados. Pressione Ctrl+C para encerrar ambos.' -ForegroundColor Green
    $stopRequested = $false
    while (-not $stopRequested) {
        if ($consoleConfigured) {
            while ([Console]::KeyAvailable) {
                $key = [Console]::ReadKey($true)
                if ($key.Key -eq [ConsoleKey]::C -and
                    ($key.Modifiers -band [ConsoleModifiers]::Control)) {
                    $stopRequested = $true
                    break
                }
            }
        }

        foreach ($entry in $processes) {
            if ($entry.Process.HasExited) {
                throw "$($entry.Name) encerrou. Encerrando os backends."
            }
        }
        if (-not $stopRequested) {
            Start-Sleep -Milliseconds 200
        }
    }
}
finally {
    # Uma falha de limpeza nao deve impedir o encerramento dos demais processos.
    $ErrorActionPreference = 'Continue'
    foreach ($entry in $processes) {
        if (-not $entry.Process.HasExited) {
            Write-Host "Encerrando $($entry.Name)..." -ForegroundColor Yellow
            # /T inclui Maven e a JVM do Spring Boot; somente nossas arvores sao encerradas.
            & "$env:SystemRoot\System32\taskkill.exe" /PID $entry.Process.Id /T /F 2>&1 | Out-Host
        }
        $entry.Process.Dispose()
    }
    if ($consoleConfigured) {
        [Console]::TreatControlCAsInput = $previousControlCMode
    }
}
