package br.com.maddytec.cliente.http.controller;

import br.com.maddytec.cliente.entity.Cliente;
import br.com.maddytec.cliente.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente salvar(@RequestBody Cliente cliente){
        return clienteService.salvar(cliente);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cliente> listaCliente(){
        return clienteService.listaCliente();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cliente buscarClientePorId(@PathVariable("id") Long id){
        return clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado."));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerCliente(@PathVariable("id") Long id){
        clienteService.buscarPorId(id)
                .map(cliente -> {
                    clienteService.removerPorId(cliente.getId());
                    return Void.TYPE;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado."));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarCliente(@PathVariable("id") Long id, @RequestBody Cliente cliente){
        clienteService.buscarPorId(id)
                .map(clienteBase -> {
                    modelMapper.map(cliente, clienteBase);
                    clienteService.salvar(clienteBase);
                    return Void.TYPE;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado."));
    }


}
