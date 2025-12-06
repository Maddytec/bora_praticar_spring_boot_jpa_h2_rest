package br.com.maddytec.cliente.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<Cliente, Long> {
}
