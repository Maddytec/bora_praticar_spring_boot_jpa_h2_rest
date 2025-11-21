package br.com.maddytec.cliente.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import br.com.maddytec.cliente.entity.Cliente;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
}
