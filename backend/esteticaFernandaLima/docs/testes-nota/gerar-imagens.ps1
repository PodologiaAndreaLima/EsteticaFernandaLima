# Gera as imagens fictícias usadas nos cenários de teste do ETL de nota fiscal.
# Uso: powershell -ExecutionPolicy Bypass -File gerar-imagens.ps1 -Destino <pasta>
# Todas as notas são FICTÍCIAS (fornecedor de exemplo, sem valor fiscal).
param([Parameter(Mandatory = $true)][string]$Destino)

Add-Type -AssemblyName System.Drawing
New-Item -ItemType Directory -Force -Path $Destino | Out-Null

function Gerar-Chave([string]$base) {
    # Dígito verificador da chave de acesso (módulo 11), igual ao NormalizadorExtracaoNota.chaveValida.
    $base43 = $base.Substring(0, 43)
    $soma = 0
    for ($i = 0; $i -lt 43; $i++) {
        $soma += [int]::Parse($base43[$i].ToString()) * (2 + (42 - $i) % 8)
    }
    $dv = 11 - ($soma % 11)
    if ($dv -ge 10) { $dv = 0 }
    return $base43 + $dv.ToString()
}

function Chave-Com-DV-Errado([string]$chaveValida) {
    $dvCerto = [int]::Parse($chaveValida[43].ToString())
    $dvErrado = ($dvCerto + 1) % 10
    return $chaveValida.Substring(0, 43) + $dvErrado.ToString()
}

function Desenhar([string]$arquivo, [string[]]$linhas, [int]$tamanhoFonte = 26, [switch]$ilegivel, [switch]$semTexto) {
    $bmp = New-Object System.Drawing.Bitmap 900, 1200
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.Clear([System.Drawing.Color]::White)
    if ($semTexto) {
        # Foto que não é nota: formas coloridas, sem texto.
        $g.FillEllipse([System.Drawing.Brushes]::Orange, 100, 100, 600, 400)
        $g.FillRectangle([System.Drawing.Brushes]::DarkGreen, 0, 800, 900, 400)
        $rodapeFoto = New-Object System.Drawing.Font("Arial", 9)
        $g.DrawString("lote $LOTE", $rodapeFoto, [System.Drawing.Brushes]::Gray, 40, 1150)
        $rodapeFoto.Dispose()
    } else {
        $fonte = New-Object System.Drawing.Font("Arial", $tamanhoFonte)
        $y = 80
        foreach ($linha in $linhas) {
            $g.DrawString($linha, $fonte, [System.Drawing.Brushes]::Black, 40, $y)
            $y += [int]($tamanhoFonte * 2.4)
        }
        $fonte.Dispose()
        $rodape = New-Object System.Drawing.Font("Arial", 9)
        $g.DrawString("lote $LOTE", $rodape, [System.Drawing.Brushes]::Gray, 40, 1150)
        $rodape.Dispose()
    }
    if ($ilegivel) {
        # Ruído forte para inviabilizar a leitura.
        $rnd = New-Object System.Random 42
        for ($i = 0; $i -lt 60000; $i++) {
            $bmp.SetPixel($rnd.Next(0, 900), $rnd.Next(0, 1200), [System.Drawing.Color]::FromArgb($rnd.Next(0, 256), $rnd.Next(0, 256), $rnd.Next(0, 256)))
        }
    }
    $g.Dispose()
    $bmp.Save((Join-Path $Destino $arquivo), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

$CNPJ_VALIDO = "11.222.333/0001-81"   # dígitos verificadores conferidos pelo normalizador
$CNPJ_INVALIDO = "00.000.000/0001-00"
$ITENS_OK = @(
    "Creme hidratante 200g   2 x 35,90   71,80",
    "Acido hialuronico 10ml   1 x 89,00   89,00"
)
# Cada lote usa uma chave e um rodapé próprios, para que as imagens não colidam com lotes anteriores
# (a deduplicação por imagem e por chave é por usuária, então imagens iguais seriam recusadas).
$LOTE = Get-Date -Format "yyyyMMddHHmmss"
$CHAVE = Gerar-Chave "3526102233344400019955001000000002223456789012345678"
$CHAVE_DV_ERRADO = Chave-Com-DV-Errado $CHAVE

function Cabecalho([string]$cnpj, [string]$data, [string]$chave) {
    $linhas = @("NOTA FISCAL DE TESTE - SEM VALOR FISCAL", "DISTRIBUIDORA EXEMPLO LTDA", "CNPJ: $cnpj")
    if ($data) { $linhas += "Data de emissao: $data" }
    # A chave (44 dígitos) vai em duas linhas de 22: em uma linha só ela passa da borda da imagem e é cortada.
    if ($chave) { $linhas += "Chave de acesso:"; $linhas += $chave.Substring(0, 22); $linhas += $chave.Substring(22) }
    return $linhas
}

# Cenários de sucesso
Desenhar "c01-completa-com-chave.png" ((Cabecalho $CNPJ_VALIDO "20/10/2026" $CHAVE) + $ITENS_OK + @("TOTAL: 160,80"))
Desenhar "c02-completa-sem-chave.png" ((Cabecalho $CNPJ_VALIDO "21/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80"))
Desenhar "c04-mesma-nota-sem-chave-outra-foto.png" ((Cabecalho $CNPJ_VALIDO "21/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80", "Via do cliente"))
Desenhar "c17-para-descarte.png" ((Cabecalho $CNPJ_VALIDO "22/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80", "Teste descarte"))
Desenhar "c18-para-rejeitar-tudo.png" ((Cabecalho $CNPJ_VALIDO "23/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80", "Teste rejeitar tudo"))

# Chave de acesso
Desenhar "c03-chave-repetida-outra-foto.png" ((Cabecalho $CNPJ_VALIDO "20/10/2026" $CHAVE) + $ITENS_OK + @("TOTAL: 160,80", "Via 2"))
Desenhar "c06-chave-dv-errado.png" ((Cabecalho $CNPJ_VALIDO "24/10/2026" $CHAVE_DV_ERRADO) + $ITENS_OK + @("TOTAL: 160,80"))

# Dados faltando ou inválidos
Desenhar "c07-sem-total.png" ((Cabecalho $CNPJ_VALIDO "25/10/2026" $null) + $ITENS_OK)
Desenhar "c08-sem-data.png" ((Cabecalho $CNPJ_VALIDO $null $null) + $ITENS_OK + @("TOTAL: 160,80"))
Desenhar "c09-data-futura.png" ((Cabecalho $CNPJ_VALIDO "01/01/2030" $null) + $ITENS_OK + @("TOTAL: 160,80"))
Desenhar "c12-total-divergente.png" ((Cabecalho $CNPJ_VALIDO "26/10/2026" $null) + $ITENS_OK + @("TOTAL: 150,00"))
Desenhar "c13-cnpj-invalido.png" ((Cabecalho $CNPJ_INVALIDO "27/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80"))
Desenhar "c11-um-item-inconsistente.png" ((Cabecalho $CNPJ_VALIDO "28/10/2026" $null) + @("Creme hidratante 200g   2 x 35,90   100,00", "Acido hialuronico 10ml   1 x 89,00   89,00") + @("TOTAL: 189,00"))
Desenhar "c12b-todos-itens-inconsistentes.png" ((Cabecalho $CNPJ_VALIDO "29/10/2026" $null) + @("Creme hidratante 200g   2 x 35,90   100,00", "Acido hialuronico 10ml   1 x 89,00   200,00") + @("TOTAL: 300,00"))
Desenhar "c19-item-sem-quantidade.png" ((Cabecalho $CNPJ_VALIDO "30/10/2026" $null) + @("Creme hidratante 200g   71,80", "Acido hialuronico 10ml   1 x 89,00   89,00") + @("TOTAL: 160,80"))

# Qualidade da imagem
Desenhar "c10-ilegivel.png" ((Cabecalho $CNPJ_VALIDO "31/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80")) -tamanhoFonte 8 -ilegivel
# Ilegível de verdade: a nota é reduzida a 90x120 e ampliada de volta com pixels grandes.
Desenhar "c10-tmp.png" ((Cabecalho $CNPJ_VALIDO "31/10/2026" $null) + $ITENS_OK + @("TOTAL: 160,80")) -tamanhoFonte 26
$original = [System.Drawing.Bitmap]::FromFile((Join-Path $Destino "c10-tmp.png"))
$pequena = New-Object System.Drawing.Bitmap 90, 120
$g1 = [System.Drawing.Graphics]::FromImage($pequena)
$g1.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g1.DrawImage($original, 0, 0, 90, 120)
$g1.Dispose()
$original.Dispose()
$ampliada = New-Object System.Drawing.Bitmap 900, 1200
$g2 = [System.Drawing.Graphics]::FromImage($ampliada)
$g2.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$g2.DrawImage($pequena, 0, 0, 900, 1200)
$g2.Dispose()
$ampliada.Save((Join-Path $Destino "c10b-muito-ilegivel.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$ampliada.Dispose()
$pequena.Dispose()
Remove-Item (Join-Path $Destino "c10-tmp.png")

Desenhar "c20-nao-e-nota.png" @() -semTexto

# Imagem grande (acima de 5 MB) para testar o limite de upload
$grande = New-Object System.Drawing.Bitmap 3000, 3000
$rnd = New-Object System.Random 7
for ($y = 0; $y -lt 3000; $y += 1) {
    for ($x = 0; $x -lt 3000; $x += 7) {
        $grande.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($rnd.Next(0, 256), $rnd.Next(0, 256), $rnd.Next(0, 256)))
    }
}
$grande.Save((Join-Path $Destino "c15-maior-que-5mb.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$grande.Dispose()

# Arquivo que não é imagem
Set-Content -Path (Join-Path $Destino "c14-texto.txt") -Value "isto nao e uma imagem" -Encoding ASCII

Write-Output "Imagens geradas em $Destino"
Write-Output "Chave valida usada: $CHAVE"
