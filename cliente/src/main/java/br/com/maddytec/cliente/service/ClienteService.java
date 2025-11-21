package br.com.maddytec.cliente.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.maddytec.cliente.entity.Cliente;
import br.com.maddytec.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente salvar(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listaCliente(){
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id){
        return clienteRepository.findById(id);
    }

    public void removerPorId(Long id){
        clienteRepository.deleteById(id);
    }
}
