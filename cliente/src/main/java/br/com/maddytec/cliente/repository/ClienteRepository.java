package br.com.maddytec.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.maddytec.cliente.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
