package br.com.maddytec.cliente.repository;

import br.com.maddytec.cliente.entity.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
}
