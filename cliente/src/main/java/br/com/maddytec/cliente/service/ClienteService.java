package br.com.maddytec.cliente.service;

import org.springframework.stereotype.Service;

import br.com.maddytec.cliente.entity.Cliente;
import br.com.maddytec.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
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
