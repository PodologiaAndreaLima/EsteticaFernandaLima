# podologiaAndreaLima

## Executar os dois backends

Com JDK 21 e Maven no `PATH`, execute na raiz do projeto:

```powershell
.\start-backends.ps1
```

O script inicia `backend\esteticaFernandaLima` e `backend\ordem-servico-ms`
com `mvn -Dmaven.test.skip=true spring-boot:run`, exibindo os logs no mesmo terminal.
A inicializacao pula a compilacao e a execucao dos testes; para executa-los,
use `mvn test` na pasta de cada backend. Pressione
**Ctrl+C** para encerrar os dois backends e seus processos filhos. Se um
backend encerrar, o script encerra o outro tambem. Os servicos externos
configurados em cada projeto devem estar disponiveis.
