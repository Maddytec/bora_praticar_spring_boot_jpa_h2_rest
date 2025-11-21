# BoraPraticar: Spring WebFlux + R2DBC com Postgres

Este BoraPraticar mostra como inserir e consultar clientes de forma reativa usando Spring WebFlux e R2DBC com Postgres. A ideia é demonstrar eficiência em I/O concorrente, incluindo um teste com 10.000 inserções e 1.000 requisições simultâneas.

## Pré‑requisitos
- Java 21
- Maven
- Docker (para Postgres)

## Subindo o Postgres
- Inicie o Docker e suba o banco:
```
docker-compose -f docker/postgresql-docker-compose.yaml up -d
```
- O banco fica em `localhost:5433`, usuário/senha/db: `cliente`.
- Verifique:
```
docker exec -i cliente-postgres psql -U cliente -d cliente -c "\dt"
```

## Variáveis de ambiente (opcional)
Se quiser alterar credenciais/host/porta sem editar `application.yml`:
```
export R2DBC_HOST=localhost
export R2DBC_PORT=5433
export R2DBC_DB=cliente
export R2DBC_USER=cliente
export R2DBC_PASSWORD=cliente
```

## Rodando a aplicação (porta 8081)
```
mvn -DskipTests spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Endpoints
- Criar cliente (POST): `http://localhost:8081/api/v1/cliente`
```
curl -X POST -H 'Content-Type: application/json' \
  -d '{"nome":"Ana","email":"ana@example.com","cpf":"123"}' \
  http://localhost:8081/api/v1/cliente
```
- Buscar por id (GET): `http://localhost:8081/api/v1/cliente/{id}`
```
curl http://localhost:8081/api/v1/cliente/1
```
- Listar (GET): `http://localhost:8081/api/v1/cliente`
```
curl http://localhost:8081/api/v1/cliente
```

## Preparando o cenário
- Limpar a tabela antes dos testes:
```
docker exec -i cliente-postgres psql -U cliente -d cliente -c "DELETE FROM cliente;"
```

## Teste: 10.000 inserções
- Sequencial (baseline):
```
seq 1 10000 | while read i; do \
  curl -s -X POST -H 'Content-Type: application/json' \
    -d "{\"nome\":\"Cliente$i\",\"email\":\"cliente$i@example.com\",\"cpf\":\"$i\"}" \
    http://localhost:8081/api/v1/cliente > /dev/null; \
done
```
- Concorrente (1.000 em paralelo, até completar 10.000):
```
seq 1 10000 | xargs -n1 -P 1000 -I{} \
  curl -s -X POST -H 'Content-Type: application/json' \
    -d '{"nome":"Cli{}","email":"cli{}@example.com","cpf":"{}"}' \
    http://localhost:8081/api/v1/cliente > /dev/null
```
- Conferir total:
```
docker exec -i cliente-postgres psql -U cliente -d cliente -c "SELECT count(*) FROM cliente;"
```

## Teste: 1.000 requisições simultâneas
- Listagem reativa com 1.000 conexões:
```
# Usando hey (instale com: brew install hey)
hey -c 1000 -n 5000 http://localhost:8081/api/v1/cliente
```
- Alternativa com wrk:
```
# Instale: brew install wrk
wrk -t4 -c1000 -d30s http://localhost:8081/api/v1/cliente
```

## Extra: POST com wrk
Crie um arquivo `post.lua` (somente para teste local) com:
```
wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"
wrk.body = '{"nome":"Load","email":"load@example.com","cpf":"999"}'
```
Execute:
```
wrk -t4 -c1000 -d30s -s post.lua http://localhost:8081/api/v1/cliente
```

## Por que reativo ajuda aqui?
- I/O não bloqueante do HTTP ao banco (WebFlux + R2DBC) permite sustentar alta concorrência com poucas threads.
- Backpressure e operadores (`Mono`/`Flux`) evitam sobrecarga quando o ritmo de produção/consumo varia.
- Netty (servidor) + R2DBC (driver) trabalham assíncronos, reduzindo tempo ocioso de threads e melhorando throughput.

## Dicas de troubleshooting
- Porta errada: se chamar `8080` e o app estiver em `8081`, você verá respostas vazias ou de outro processo.
- Banco fora do ar: verifique o container e credenciais (`R2DBC_*`).
- Tabela ausente: execute o `schema.sql` automaticamente (já habilitado) ou crie manualmente:
```
docker exec -i cliente-postgres psql -U cliente -d cliente -c \
  "CREATE TABLE IF NOT EXISTS cliente (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, email VARCHAR(255), cpf VARCHAR(20));"
```

# BoraPraticar
Agora é só rodar os testes e comparar tempos de inserção e resposta sob concorrência. Publica no seu blogue e marca que este foi um BoraPraticar!