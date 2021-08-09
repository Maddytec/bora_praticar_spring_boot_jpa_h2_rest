package br.com.maddytec.cliente.repository;

import br.com.maddytec.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
