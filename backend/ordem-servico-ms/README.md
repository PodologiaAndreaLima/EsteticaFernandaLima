# ordem-servico-ms

Microservico isolado de Ordem de Servico em Clean Architecture.

## Estrutura

- `domain`: regras e modelos de dominio
- `application`: casos de uso e DTOs
- `infrastructure`: persistencia (JPA)
- `adapters`: entrada HTTP (controller)

## Endpoints

- `POST /ordens-servico`
- `GET /ordens-servico/{id}`
- `GET /ordens-servico?page=0` (paginacao fixa de 10)
- `PUT /ordens-servico/{id}`
- `DELETE /ordens-servico/{id}`

## Executar

```bash
mvn spring-boot:run
```

## Testar

```bash
mvn test
```

