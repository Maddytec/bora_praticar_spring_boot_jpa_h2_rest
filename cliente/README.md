# BoraPraticar: Reativo vs JDBC — quem segura a bronca de 10k requisições?

- Objetivo: comparar, na prática, dois jeitos de salvar/consultar cliente no Postgres — reativo (Spring WebFlux + R2DBC) e clássico síncrono (Spring MVC + JDBC) — usando o mesmo teste de carga.
- Branches do projeto:
  - Reativo: https://github.com/Maddytec/bora_praticar_spring_boot_jpa_h2_rest/tree/reative
  - JDBC: https://github.com/Maddytec/bora_praticar_spring_boot_jpa_h2_rest/tree/postgresql

## O que vamos medir
- Throughput: requisições por segundo
- Latência p50/p95/p99: tempo de resposta sob pressão
- Estabilidade: erros e consistência dos códigos de status
- Comportamento de threads: reativo tende a usar poucas threads para muita I/O

## Repository Reativo (elo com o banco)
- `src/main/java/br/com/maddytec/cliente/repository/ClienteRepository.java`
```
public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
}
```

## Serviço Reativo (onde o banco acontece)`
```
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Mono<Cliente> salvar(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public Flux<Cliente> listaCliente(){
        return clienteRepository.findAll();
    }

    public Mono<Cliente> buscarPorId(Long id){
        return clienteRepository.findById(id);
    }

    public Mono<Void> removerPorId(Long id){
        return clienteRepository.deleteById(id);
    }
}
```
## Controller Reativo (endpoints do teste)
```
@RestController
@RequestMapping("api/v1/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final ModelMapper modelMapper;

    @PostMapping
    public Mono<ResponseEntity<Cliente>> salvar(@RequestBody Mono<Cliente> cliente){
        return cliente
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corpo da requisição vazio.")))
                .flatMap(clienteService::salvar)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Cliente> listaCliente(){
        return clienteService.listaCliente();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> buscarClientePorId(@PathVariable("id") Long id){
        return clienteService.buscarPorId(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado.")))
                .map(ResponseEntity::ok);
    }
}
```

## Setup
- Pré‑requisitos:
  - Java 21, Maven, Docker
  - Ferramenta de carga (escolha uma)
    - k6 (Windows/Linux/mac)
    - wrk (Linux/mac)
    - autocannon (Node, cross‑platform)
    - hey (mac)
- Subir Postgres:
  - `docker-compose -f docker/postgresql-docker-compose.yaml up -d`
  - Banco: `localhost:5433`, db/user/pass: `cliente`
- Rodar cada branch:
  - Reativo: `git checkout reative` e `mvn -DskipTests spring-boot:run`
  - JDBC: `git checkout postgresql` e `mvn -DskipTests spring-boot:run`
  - Ambas sobem em `http://localhost:8080/` com o endpoint `POST /api/v1/cliente`

## O endpoint que recebe as requisições
- Reativo POST em `src/main/java/br/com/maddytec/cliente/http/controller/ClienteController.java:31–37`
- Corpo esperado:
  - `{"nome":"Maddytec 1","email":"maddytec1@test.com","cpf":"1"}`

## Um comando para 10.000 requisições e 1.000 simultâneas
- k6 (Windows/Linux/mac)
  - Crie `k6-post.js`:
```
import http from 'k6/http';
export const options = { vus: 1000, iterations: 10000 };
export default function () {
  const url = 'http://localhost:8080/api/v1/cliente';
  const body = JSON.stringify({ nome: 'Maddytec 1', email: 'maddytec1@test.com', cpf: '1' });
  const headers = { 'Content-Type': 'application/json' };
  http.post(url, body, { headers });
}
```
  - Rodar: `k6 run k6-post.js`
- wrk (Linux/mac)
  - `post.lua`:
```
wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"
wrk.body = '{"nome":"Maddytec 1","email":"maddytec1@test.com","cpf":"1"}'
```
  - Rodar: `wrk -t4 -c1000 -d30s -s post.lua http://localhost:8080/api/v1/cliente`
- autocannon (Node)
  - `autocannon -c 1000 -d 30 -m POST -H 'Content-Type: application/json' -b '{"nome":"Maddytec 1","email":"maddytec1@test.com","cpf":"1"}' http://localhost:8080/api/v1/cliente`
- hey (mac)
  - `hey -m POST -H 'Content-Type: application/json' -d '{"nome":"Maddytec 1","email":"maddytec1@test.com","cpf":"1"}' -c 1000 -n 10000 http://localhost:8080/api/v1/cliente`

## Dica esperta
- Limpe a tabela entre os testes para não misturar resultados:
  - `docker exec -i cliente-postgres psql -U cliente -d cliente -c "DELETE FROM cliente;"`

## Como ler os resultados
- Throughput maior: ponto para o stack que estiver no topo
- Latências baixas e p95/p99 comportadas: stack está segurando o rojão
- Erros zero: saúde em dia sob concorrência
- No reativo, espere bom uso de I/O e menos threads; no JDBC, espere mais threads e possível saturação com muita concorrência

## Resumo da brincadeira
- Reativo (WebFlux + R2DBC): I/O não bloqueante, ótimo para cenários de muita requisição, pouca thread e bastante acesso a banco/serviços externos
- JDBC (MVC + JDBC): simples, conhecido, robusto; mas bloqueante e pode sofrer mais com altíssima concorrência

## BoraPraticar
- O reativo não deixa seu código mais rápido — ele deixa seu servidor atender muito mais gente com o mesmo hardware.
- Rode, compare, tire print dos resultados, compartilhe e nos marque.
- Links:
  - Reativo: https://github.com/Maddytec/bora_praticar_spring_boot_jpa_h2_rest/tree/reative
  - JDBC: https://github.com/Maddytec/bora_praticar_spring_boot_jpa_h2_rest/tree/postgresql