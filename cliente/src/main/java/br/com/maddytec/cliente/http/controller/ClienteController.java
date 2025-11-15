package br.com.maddytec.cliente.http.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.maddytec.cliente.entity.Cliente;
import br.com.maddytec.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
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

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> removerCliente(@PathVariable("id") Long id){
        return clienteService.buscarPorId(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado.")))
                .flatMap(c -> clienteService.removerPorId(id)
                        .thenReturn(ResponseEntity.noContent().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> atualizarCliente(@PathVariable("id") Long id, @RequestBody Cliente cliente){
        return clienteService.buscarPorId(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado.")))
                .flatMap(clienteBase -> {
                    modelMapper.map(cliente, clienteBase);
                    return clienteService.salvar(clienteBase).thenReturn(ResponseEntity.noContent().build());
                });
    }


}
